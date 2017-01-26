## processXML.py - Script for processing the XMLs from the talkbank corpus
##
## Copyright (C) 2017 Aaron Keesing
## Version: 2.0


import xmltodict
import nltk, nltk.corpus
from pycorenlp import StanfordCoreNLP
import os.path
import os
from concurrent.futures import ThreadPoolExecutor, ProcessPoolExecutor
from itertools import groupby, islice, chain
from operator import itemgetter
from pathlib import Path
import argparse
import pickle
import re


# This class was copied from http://streamhacker.com/2009/02/23/chunk-extraction-with-nltk/
class TagChunker(nltk.ChunkParserI):
    def __init__(self, chunk_tagger):
        self._chunk_tagger = chunk_tagger
 
    def parse(self, tokens):
        # split words and part of speech tags
        (words, tags) = zip(*tokens)
        # get IOB chunk tags
        chunks = self._chunk_tagger.tag(tags)
        # join words with chunk tags
        wtc = zip(words, chunks)
        # w = word, t = part-of-speech tag, c = chunk tag
        lines = [' '.join([w, t, c]) for (w, (t, c)) in wtc if c]
        # create tree from conll formatted chunk lines
        return nltk.conllstr2tree('\n'.join(lines))


def stanfordProcess(utters):
    # Set up Stanford Stuff
    stserver = StanfordCoreNLP('http://localhost:9000/')
    processed = []
    try:
        for utter in utters:
            annot = stserver.annotate(utter, properties={
                'annotators': 'tokenize,ssplit,pos,ner,lemma',
                #'pos.model': 'edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger',
                'pos.model': 'gate-EN-twitter.model',
                'ner.model': 'edu/stanford/nlp/models/ner/english.conll.4class.distsim.crf.ser.gz',
                'outputFormat': 'json'
            })
            processed.append([(tok['word'], tok['pos'], tok['lemma'], tok['ner']) for tok in annot['sentences'][0]['tokens']])
    except Exception:
        print("Unable to connect to Stanford CoreNLP server. Make sure it is running at http://localhost:9000/")
        print("You can start the server with the command: java -mx1000m edu.stanford.nlp.pipeline.StanfordCoreNLPServer 9000")
        exit()

    return processed


def nltkProcess(utters, chunk=False, printDebug=False):
    tokenizedUtters = [nltk.word_tokenize(utter) for utter in utters]
    #taggedUtters = nltk.pos_tag_sents(tokenizedUtters)
    taggedUtters = tagger.tag_sents(tokenizedUtters)
    # Assume each unknown tag is a proper noun
    taggedUtters = [[(w, t) if t else (w, 'NNP') for w, t in utter] for utter in taggedUtters]
    
    stemmer = nltk.stem.snowball.EnglishStemmer()
    stemmedUtters = [[(w, t, stemmer.stem(w)) for w, t in utter] for utter in taggedUtters]

    if printDebug:
        print()
        print('Utterances:')
        for utter in utters[:20]:
            print(utter)
        print('...')

        print()
        print('Tagged utterances:')
        for utter in taggedUtters[:20]:
            print(' '.join([nltk.tuple2str(tok) for tok in utter]))
            print()
        print('...')

    if chunk:
        chunkedUtters = chunker.parse_sents(taggedUtters)
        if printDebug:
            print()
            print('Chunks:')
            for utter in islice(chunkedUtters, 10):
                print(utter)
            print('...')
    
    neTags = list(nltk.ne_chunk_sents(taggedUtters))
    if printDebug:
        print()
        print('NE tags:')
        for utter in neTags[:10]:
            print(utter)
        print('...')
    
    for i in range(len(neTags)):
        utter = neTags[i]
        k = 0
        for j in range(len(utter)):
            tok = utter[j]
            if isinstance(tok, nltk.Tree):
                for _ in range(len(tok)):
                    w, t, s = stemmedUtters[i][k]
                    stemmedUtters[i][k] = (w, t, s, tok.label())
                    k += 1
            else:
                w, t, s = stemmedUtters[i][k]
                stemmedUtters[i][k] = (w, t, s, 'O')
                k += 1
    
    return stemmedUtters


badPatterns = [
    re.compile(r'^[OoHh]+$'),
    re.compile(r'^[UuHhMm]+$'),
    re.compile(r'^[UuGgHh]+$'),
    re.compile(r'^[HhAa]+$'),
    re.compile(r'^[Xx]+$'),
    re.compile(r'^([AaEeIiOoUu])\1{2,}$'),
    re.compile(r'^(_)+$')
]
def isBad(word):
    if word.startswith('-') or word.endswith('-'):
        return True
    for pat in badPatterns:
        if pat.match(word):
            return True


def writexml(utters, id, filename):
    if not filename.endswith('.xml'):
        filename = filename + '.xml'
    tree = {
        'conversation': {
            '@id': id,
            'u': []
        }
    }
    for utter in utters:
        u = {'t': []}
        for t in utter:
            u['t'].append({'@pos': t[1], '#text': t[0], '@stem': t[2], '@ner': t[3]})
        tree['conversation']['u'].append(u)
    
    #print(xmltodict.unparse(tree, pretty=True))
    filename = os.path.join(outputDir, filename)
    print('Writing to {}'.format(filename))
    outfile = open(filename, mode='bw')
    xmltodict.unparse(tree, output=outfile, pretty=True)
    outfile.close()


def joinSentence(tokens):
    PUNCTUATION = set(',.?;-\'"')
    outStr = ""
    for tok in tokens:
        if tok not in PUNCTUATION:
            outStr += ' '
        outStr += tok
    
    return outStr


def processTBFile(filename, processUtterFunc):
    print("Processing file {}".format(filename))
    with open(filename, encoding='utf8') as f:
        xmlDoc = xmltodict.parse(f.read(), encoding='utf-8')
        if (len(xmlDoc['CHAT']['@Lang'].split()) > 1
            or not isinstance(xmlDoc['CHAT']['Participants']['participant'], list)):
            # Ignore files with multiple languages or only one speaker
            return
        
        utters = []
        for utter in xmlDoc['CHAT']['u']:
            if 'w' in utter:
                who = utter['@who']
                wordList = []
                if isinstance(utter['w'], list): # Multiple words
                    wordList = utter['w']
                else: # Single word
                    wordList = [utter['w']]
                words = []
                for w in wordList:
                    if isinstance(w, dict):
                        if '@untranscribed' in w or '@type' in w: # Skip non-sensical things
                            continue
                        elif '#text' in w:
                            words.append(w['#text'])
                    else:
                        words.append(w)
                # Get rid of bad stuff in words
                
                words = list(chain(*[w.split('_') for w in words]))
                words = [re.sub(r'[^A-Za-z0-9)(,.?!$%&;:\'"]', '', w) for w in words]
                words = [w for w in words if not isBad(w)]
                words = [re.sub(r'([A-Za-z])\1\1+', r'\1\1', w) for w in words]

                if len(words) > 0:
                    utters.append((who, joinSentence(words)))

        # Join contiguous responses from identical speakers
        groupedUtters = [' '.join([x[1] for x in g]) for k, g in groupby(utters, itemgetter(0))]
        processedUtters = processUtterFunc(groupedUtters)
        
        if WRITEOUT:
            writexml(processedUtters, xmlDoc['CHAT']['@Id'], 'conv_' + os.path.basename(filename))


def processTwitterFile(filename, processUtterFunc):
    print("Processing file {}".format(filename))
    with open(filename, encoding='utf8') as f:
        alllines = list(map(lambda x: x.strip(), f.readlines()))
        with ThreadPoolExecutor() as executor:
            def func(lines, i):
                utters = []
                for utter in [line[20:] for line in lines]: # Ignore '123456789012345678: '
                    words = utter.split(' ')
                    words = list(chain(*[w.split('_') for w in words]))
                    words = [re.sub(r'[^A-Za-z0-9)(,.?!$%&;:\'"]', '', w) for w in words]
                    words = [w for w in words if not isBad(w)]
                    words = [re.sub(r'([A-Za-z])\1\1+', r'\1\1', w) for w in words]
                    if len(words) > 0:
                        utters.append(' '.join(words)) # Fine because we split on spaces
                utters = processUtterFunc(utters)
                if WRITEOUT:
                    writexml(utters, 'tw_' + str(i), 'conv_tw_' + str(i))
            for i in range(len(alllines) // 4):
                executor.submit(func, alllines[4*i:4*i+3], i)


def processANCFile(filename, processUtterFunc):
    print("Processing file {}".format(filename))
    with open(filename, encoding='utf8') as f:
        xmlDoc = xmltodict.parse(f.read(), encoding='utf-8')
        utters = []
        for turn in xmlDoc['cesDoc']['body']['turn']:
            sents = []
            if isinstance(turn['u'], list):
                sents = turn['u']
            else:
                sents = [turn['u']]
            for u in sents:
                if 'u' in u:
                    if isinstance(u['u'], list):
                        us = u['u']
                    else:
                        us = [u['u']]
                else:
                    us = [u]
                utter = []
                for ut in us:
                    toks = []
                    if 'tok' in ut:
                        if isinstance(ut['tok'], list):
                            toks = ut['tok']
                        else:
                            toks = [ut['tok']]
                        for tok in toks:
                            if not isBad(tok['#text']):
                                utter.append((tok['#text'], tok['@msd'], tok['@base'], 'O'))
                if len(utter) > 0:
                    utters.append(utter)
        if DEBUG:
            for utter in utters[20:]:
                print(utter)
        
        if WRITEOUT:
            writexml(utters, os.path.basename(filename), 'conv_' + os.path.basename(filename))


WRITEOUT = None
processUtterFunc = None
outputDir = None
infile = None
tagger = None
chunker = None


def main():
    global WRITEOUT, processUtterFunc, outputDir, infile, tagger, chunker

    parser = argparse.ArgumentParser()
    subparsers = parser.add_subparsers(dest='method')

    parser_train = subparsers.add_parser('train')
    parser_train.add_argument('corpus', help="The tagged corpus to train the POS tagger on.", default='../corpora/OpenANC/processed/nltk/spoken')

    parser_process = subparsers.add_parser('process')
    parser_process.add_argument('infile', help="An input file or directory. If it is a file then the file is processed. " +
                                        "If it is a directory then all .xml files will be processed recursively in " +
                                        "the directory and all subdirectories", type=str)
    parser_process.add_argument('-p', '--print', help="Print verbose info", action='store_true')
    parser_process.add_argument('-o', '--outputdir', help='The directory to place the processed file(s), default "xmlout/"', nargs='?', type=str, const='xmlout', default='')
    parser_process.add_argument('--chunk', action='store_true', help="Chunk sentences into a tree of height 1")
    parser_process.add_argument('-s', '--stanford', action='store_true', help="Use Stanford CoreNLP")

    group = parser_process.add_mutually_exclusive_group(required=True)
    group.add_argument('-t', '--talkbank', action='store_true')
    group.add_argument('-w', '--twitter', action='store_true')
    group.add_argument('-a', '--anc', action='store_true')

    args = parser.parse_args()

    if args.method == 'train':
        # Train POS tagger
        #train_sents = nltk.corpus.conll2000.tagged_sents()
        ANCCorpus = nltk.corpus.TaggedCorpusReader(args.corpus, r'.*\.txt', sep='_')
        train_sents = ANCCorpus.tagged_sents()
        patterns = [
            (r'^[Yy]eah$', 'UH'),
            (r'^[Oo]+[Hh]+$', 'UH'),
            (r'^[UuHhMm]+$', 'UH'),
            (r'^[HhAa]+$', 'UH'),
            (r'^okay$', 'JJ'),
            (r'^[Oo][Kk]$', 'JJ'),

            # The follow were copied from http://streamhacker.com/2008/11/10/part-of-speech-tagging-with-nltk-part-2/
            (r'^-?[0-9]+(.[0-9]+)?$', 'CD'),
            (r'.*ould$', 'MD'),
            (r'.*ing$', 'VBG'),
            (r'.*ed$', 'VBD'),
            (r'.*ness$', 'NN'),
            (r'.*ment$', 'NN'),
            (r'.*ful$', 'JJ'),
            (r'.*ious$', 'JJ'),
            (r'.*ble$', 'JJ'),
            (r'.*ic$', 'JJ'),
            (r'.*ive$', 'JJ'),
            (r'.*ic$', 'JJ'),
            (r'.*est$', 'JJ'),
            (r'^a$', 'IN'),
        ]
        from nltk.tag import brill, brill_trainer
        templates = brill.brill24()
        from time import time
        startTime = time()
        tagger = nltk.TrigramTagger(train_sents,
                        backoff=nltk.BigramTagger(train_sents,
                        backoff=nltk.UnigramTagger(train_sents,
                        backoff=nltk.AffixTagger(train_sents,
                        backoff=nltk.RegexpTagger(patterns)))))
        trainer = brill_trainer.BrillTaggerTrainer(tagger, templates)
        tagger = trainer.train(train_sents, max_rules=100, min_score=3)
        endTime = time()
        print('Trained tagger in {:.2f} secs'.format(endTime - startTime))
        with open('tagger.pickle', 'wb') as taggerSerial:
            pickle.dump(tagger, taggerSerial)
        
        # Train sentence chunker
        train_chunks = nltk.corpus.conll2000.chunked_sents()
        tag_sents = [nltk.tree2conlltags(tree) for tree in train_chunks]
        tag_chunks = [[(t, c) for (w, t, c) in chunk_tags] for chunk_tags in tag_sents]
        startTime = time()
        chunktagger = nltk.BigramTagger(tag_chunks,
                        backoff=nltk.UnigramTagger(tag_chunks))
        chunker = TagChunker(chunktagger)
        endTime = time()
        print('Trained chunker in {:.2f} secs'.format(endTime - startTime))
        with open('chunker.pickle', 'wb') as chunkerSerial:
            pickle.dump(chunker, chunkerSerial)
        print()
    else:
        DEBUG = bool(args.print)
        CHUNK = bool(args.chunk)
        STANFORD = bool(args.stanford)

        if args.outputdir:
            outputDir = args.outputdir
            WRITEOUT = True
        else:
            WRITEOUT = False

        if WRITEOUT and not os.path.exists(outputDir):
            os.makedirs(name=outputDir)

        if STANFORD:
            processUtterFunc = stanfordProcess
        else:
            processUtterFunc = nltkProcess
            with open('tagger.pickle', 'rb') as taggerSerial:
                tagger = pickle.load(taggerSerial)
            print("Loaded tagger")
            with open('chunker.pickle', 'rb') as chunkerSerial:
                chunker = pickle.load(chunkerSerial)
            print("Loaded chunker")

        infile = args.infile
        if args.talkbank:
            processfilefunc = processTBFile
        elif args.twitter:
            processfilefunc = processTwitterFile
        elif args.anc:
            processfilefunc = processANCFile
        else:
            raise Exception("Type of file(s) not specified")
        
        if os.path.isdir(infile):
            files = [str(p) for p in Path(infile).glob('**/*.xml')]
            # Do tree concurrently
            with ThreadPoolExecutor() as executor:
                for file in files:
                    executor.submit(processfilefunc, file, processUtterFunc)
        else:
            processfilefunc(infile, processUtterFunc)


if __name__ == '__main__':
    main()
