## processXML.py - Script for processing the XMLs from the talkbank corpus
##
## Copyright (C) 2017 Aaron Keesing
## Version: 2.4.0


import xmltodict
import nltk, nltk.corpus
from pycorenlp import StanfordCoreNLP
import os.path
import os
from concurrent.futures import ProcessPoolExecutor
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


class FileProcessor():
    """This is a utility class for processing files.
    """
    def __init__(self, processFileFunc, processUtterFunc, debug=False, writeout=True, outputDir='xmlconv/'):
        self.processFileFunc = processFileFunc
        self.processUtterFunc = processUtterFunc
        self.debug = debug
        self.writeout = writeout
        self.outputDir = outputDir
        self.tagger = None
        self.chunker = None
    
    def processFile(self, file):
        utters, id = self.processFileFunc(file)
        sents = self.processUtterFunc(utters, self.tagger, self.debug)
        
        if self.writeout:
            FileProcessor.writexml(sents, id, 'conv_' + id + '.xml', self.outputDir)
    
    @staticmethod
    def writexml(utters, id, filename, outputDir):
        if not filename.endswith('.xml'):
            filename = filename + '.xml'

        tree = {
            'conversation': {
                '@id': id,
                'u': []
            }
        }

        for utter in utters:
            u = {'s': []}
            for sent in utter:
                s = {'t': []}
                for t in sent:
                    s['t'].append({'@pos': t[1], '#text': t[0], '@stem': t[2], '@ner': t[3]})
                u['s'].append(s)
            tree['conversation']['u'].append(u)
        
        #print(xmltodict.unparse(tree, pretty=True))
        filename = os.path.join(outputDir, filename)
        print('Writing to {}'.format(filename))
        outfile = open(filename, mode='bw')
        xmltodict.unparse(tree, output=outfile, pretty=True)
        outfile.close()


def stanfordProcess(utters, tagger=None, debug=False):
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
            processed.append([[(tok['word'], tok['pos'], tok['lemma'], tok['ner']) for tok in sent['tokens']] for sent in annot['sentences']])
    except Exception:
        print("Unable to connect to Stanford CoreNLP server. Make sure it is running at http://localhost:9000/")
        print("You can start the server with the command: java -mx1000m edu.stanford.nlp.pipeline.StanfordCoreNLPServer 9000")
        exit()

    return processed

def nltkProcess(utters, tagger, debug=False):
    if not isinstance(tagger, nltk.tag.TaggerI):
        raise TypeError("Argument 'tagger' is type {} must be of type {}".format(type(tagger), nltk.TaggerI))
    
    processedUtters = []
    if debug:
        print()
        print('Utterances:')
        for utter in utters[:20]:
            print(utter)
        print('...')
        print()
    
    for utter in utters:
        sents = nltk.sent_tokenize(utter)
        processedSents = []
        for sent in sents:
            tokens = nltk.word_tokenize(sent)

            #taggedSent = nltk.pos_tag(tokens)
            taggedSent = tagger.tag(tokens)
            # Assume each unknown tag is a proper noun
            taggedSent = [(w, t) if t else (w, 'NNP') for w, t in taggedSent]
            
            stemmer = nltk.stem.snowball.EnglishStemmer()
            stemmedTags = [(w, t, stemmer.stem(w)) for w, t in taggedSent]

            if debug:
                print('Tagged sentence:')
                print(' '.join([nltk.tuple2str(tok) for tok in taggedSent]))
                print()
            
            neTags = list(nltk.ne_chunk(taggedSent))
            if debug:
                print('NE tags:')
                for tag in neTags[:20]:
                    print(tag)
                print('...')
                print()
                print()
            
            k = 0
            for i in range(len(neTags)):
                tag = neTags[i]
                if isinstance(tag, nltk.Tree):
                    for _ in range(len(tag)):
                        w, t, s = stemmedTags[k]
                        stemmedTags[k] = (w, t, s, tag.label())
                        k += 1
                else:
                    w, t, s = stemmedTags[k]
                    stemmedTags[k] = (w, t, s, 'O')
                    k += 1
            
            processedSents.append(stemmedTags)
        processedUtters.append(processedSents)
    
    return processedUtters


def joinSentence(tokens):
    PUNCTUATION = set(',.?;%!')
    outStr = ""
    for tok in tokens:
        if tok not in PUNCTUATION and "'" not in tok:
            outStr += ' '
        outStr += tok
    
    return outStr

def isBad(word):
    badPatterns = [
        re.compile(r'^[OoHh]+$'),
        re.compile(r'^[UuHhMm]+$'),
        re.compile(r'^[UuGgHh]+$'),
        re.compile(r'^[HhAa]+$'),
        re.compile(r'^[Xx]+$'),
        re.compile(r'^([AaEeIiOoUu])\1{2,}$'),
        re.compile(r'^(_)+$')
    ]

    if word.startswith('-') or word.endswith('-'):
        return True
    elif word == '':
        return True
    for pat in badPatterns:
        if pat.match(word):
            return True

def removeBadWords(words):
    words = list(chain(*[w.split('_') for w in words]))
    words = [re.sub(r'[^A-Za-z0-9)(,.?!$%&;:\'"]', '', w) for w in words]
    words = [re.sub(r'([A-Za-z])\1\1+', r'\1\1', w) for w in words]
    words = [w for w in words if not isBad(w)]

    return words


def processTBFile(filename):
    print("Processing file {}".format(filename))
    utters = []
    with open(filename, encoding='utf8') as f:
        xmlDoc = xmltodict.parse(f.read(), encoding='utf-8')
        if (len(xmlDoc['CHAT']['@Lang'].split()) > 1
            or not isinstance(xmlDoc['CHAT']['Participants']['participant'], list)):
            # Ignore files with multiple languages or only one speaker
            return
        
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
                if 't' in utter:
                    ttype = utter['t']['@type']
                else:
                    ttype = 'p'
                if ttype == 'q':
                    t = '?'
                elif ttype == 'e':
                    t = '!'
                else:
                    t = '.'
                words.append(t)
                words = removeBadWords(words)

                if len(words) > 0:
                    utters.append((who, joinSentence(words)))

    # Join contiguous responses from identical speakers
    groupedUtters = [' '.join([x[1] for x in g]) for k, g in groupby(utters, itemgetter(0))]

    return (groupedUtters, xmlDoc['CHAT']['@Id'])

def processTwitterFile(filename):
    print("Processing file {}".format(filename))
    utters = []
    with open(filename, encoding='utf8') as f:
        alllines = list(map(lambda x: x.strip(), f.readlines()))
        for utter in [line[20:] for line in alllines]: # Ignore '123456789012345678: '
            words = utter.split(' ')
            words = removeBadWords(words)
            if len(words) > 0:
                utters.append(' '.join(words)) # Fine because we split on spaces
    
    return (utters, os.path.basename(filename))

def processANCFile(filename):
    print("Processing file {}".format(filename))
    utters = []
    with open(filename, encoding='utf8') as f:
        xmlDoc = xmltodict.parse(f.read(), encoding='utf-8')
        for utter in xmlDoc['cesDoc']['body']['turn']:
            currentUtter = []
            sents = []
            if isinstance(utter['u'], list):
                sents = utter['u']
            else:
                sents = [utter['u']]
            for u in sents:
                if 'u' in u:
                    if isinstance(u['u'], list):
                        us = u['u']
                    else:
                        us = [u['u']]
                else:
                    us = [u]
                sent = []
                for ut in us:
                    toks = []
                    if 'tok' in ut:
                        if isinstance(ut['tok'], list):
                            toks = ut['tok']
                        else:
                            toks = [ut['tok']]
                        for tok in toks:
                            if not isBad(tok['#text']):
                                sent.append((tok['#text'], tok['@msd'], tok['@base'], 'O'))
                if len(sent) > 0:
                    currentUtter.append(sent)
            if len(currentUtter) > 0:
                utters.append(currentUtter)
    
    return (utters, os.path.basename(filename))


def processIdent(utters, tagger, debug):
    return utters

def main():
    argparser = argparse.ArgumentParser()
    subparsers = argparser.add_subparsers(dest='method')

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

    args = argparser.parse_args()

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
        outputDir = args.outputdir
        print("Output directory: '{}'".format(outputDir))

        if outputDir:
            WRITEOUT = True
            if not os.path.exists(outputDir):
                os.makedirs(name=outputDir)
        else:
            WRITEOUT = False
        
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
            processUtterFunc = processIdent # No processing necessary for OANC
        else:
            raise Exception("Type of file(s) not specified")
        
        
        processor = FileProcessor(processfilefunc, processUtterFunc, DEBUG, WRITEOUT, outputDir)
        if not STANFORD:
            processor.tagger = tagger
            processor.chunker = chunker
        
        if os.path.isdir(infile):
            files = filter(lambda x: x.endswith('.xml') or x.endswith('.txt'), (str(p) for p in Path(infile).rglob('*.*')))
            # Do tree concurrently
            with ProcessPoolExecutor() as executor:
                for file in files:
                    executor.submit(processor.processFile, file)
        else:
            processor.processFile(infile)


if __name__ == '__main__':
    main()
