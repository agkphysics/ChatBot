## processXML.py - Script for processing the XMLs from the talkbank corpus
##
## You'll need to run the script with the -t option first to train the tagger
## and chunker, and store them serialised.
## 
## Version: 1.7
## Author: Aaron Keesing


import xmltodict
import nltk, nltk.corpus
from pycorenlp import StanfordCoreNLP
import os.path
import os
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


def processFile(file):
    inFilename = file
    print("Processing file {}".format(inFilename))
    with open(inFilename, encoding='utf8') as f:
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
                words = [re.sub(r'[^A-Za-z0-9,.?$%&;:\'"]', '', w) for w in words if '-' not in w and w.lower() not in {'um', 'uh'}]
                if len(words) > 0:
                    utters.append((who, ' '.join(words)))

        # Join contiguous responses from identical speakers
        groupedUtters = [' '.join([x[1] for x in g]) for k, g in groupby(utters, itemgetter(0))]

        # Set up Stanford Stuff
        stserver = StanfordCoreNLP('http://localhost:9000/')
        
        
        tokenizedUtters = [nltk.word_tokenize(utter) for utter in groupedUtters]
        #taggedUtters = nltk.pos_tag_sents(tokenizedUtters)
        taggedUtters = tagger.tag_sents(tokenizedUtters)

        stemmer = nltk.stem.snowball.EnglishStemmer()
        stemmedUtters = []
        for utter in taggedUtters:
            stemmedUtter = []
            for (w, t) in utter:
                stemmedUtter.append((w, t, stemmer.stem(w)))
            stemmedUtters.append(stemmedUtter)
        
        stanfordUtters = []
        if STANFORD:
            try:
                for utter in groupedUtters:
                    annot = stserver.annotate(utter, properties={
                        'annotators': 'tokenize,ssplit,pos,ner,lemma',
                        'pos.model': 'edu/stanford/nlp/models/pos-tagger/english-caseless-left3words-distsim.tagger',
                        'ner.model': 'edu/stanford/nlp/models/ner/english.conll.4class.caseless.distsim.crf.ser.gz',
                        'outputFormat': 'json'
                    })
                    stanfordUtters.append([(tok['word'], tok['pos'], tok['lemma'], tok['ner']) for tok in annot['sentences'][0]['tokens']])
            except Exception:
                print("Unable to connect to Stanford CoreNLP server. Make sure it is running at http://localhost:9000/")
                print("You can start the server with the command: java -mx100m edu.stanford.nlp.pipeline.StanfordCoreNLPServer 9000")
                exit()
            
        if DEBUG:
            print()
            print('Utterances:')
            for (who, utter) in utters[:20]:
                print("{}: {}".format(who, utter))
            print('...')

            print()
            print('Alternating responses:')
            for utter in groupedUtters[:20]:
                print(utter)
                print()
            print('...')

            print()
            print('Tagged utterances:')
            for utter in taggedUtters[:20]:
                print(' '.join([nltk.tuple2str(tok) for tok in utter]))
                print()
            print('...')

            if STANFORD:
                print()
                print('Processed using stanford corenlp:')
                for utter in stanfordUtters[:20]:
                    print(utter)
                    print()
                print('...')

        if CHUNK:
            chunkedUtters = chunker.parse_sents(taggedUtters)
            if DEBUG:
                print()
                print('Chunks:')
                for utter in islice(chunkedUtters, 10):
                    print(utter)
                print('...')
        
        if NETAG:
            neTags = nltk.ne_chunk_sents(taggedUtters)
            if DEBUG:
                print()
                print('NE tags:')
                for utter in islice(neTags, 10):
                    print(utter)
                print('...')
        
        tree = {
            'conversation': {
                '@id': xmlDoc['CHAT']['@Id'],
                'u': []
            }
        }
        if STANFORD:
            pairs = stanfordUtters
        else:
            pairs = stemmedUtters
        for utter in pairs:
            u = {'t': []}
            for t in utter:
                if STANFORD:
                    u['t'].append({'@pos': t[1], '#text': t[0], '@stem': t[2], '@ner': t[3]})
                else:
                    u['t'].append({'@pos': t[1], '#text': t[0], '@stem': t[2], '@ner': 'O'})
            tree['conversation']['u'].append(u)
        
        #print(xmltodict.unparse(tree, pretty=True))
        
        if WRITEOUT:
            outFilename = os.path.join(outputDir, 'conv_' + os.path.basename(inFilename))
            print('Writing to {}'.format(outFilename))
            outfile = open(outFilename, mode='bw')
            xmltodict.unparse(tree, output=outfile, pretty=True)
            outfile.close()


parser = argparse.ArgumentParser(description='')
parser.add_argument('infile', help="An input file or directory. If it is a file then the file is processed. " +
                                    "If it is a directory then all .xml files will be processed recursively in " +
                                    "the directory and all subdirectories.", type=str)
parser.add_argument('-p', '--print', help="Print verbose info", action='store_true')
parser.add_argument('-o', '--outputdir', help='The directory to place the processed file(s), default "xmlout/"', nargs='?', type=str, const='xmlout', default='')
parser.add_argument('-t', '--train', help="Train the tagger and chunker and serialize them as files.", action='store_true')
parser.add_argument('--netag', action='store_true')
parser.add_argument('--chunk', action='store_true')
parser.add_argument('-s', '--stanford', action='store_true', help="Use Stanford CoreNLP")
args = parser.parse_args()

DEBUG = bool(args.print)
NETAG = bool(args.netag)
CHUNK = bool(args.chunk)
STANFORD = bool(args.stanford)
if args.outputdir:
    outputDir = args.outputdir
    WRITEOUT = True
else:
    WRITEOUT = False

if WRITEOUT and not os.path.exists(outputDir):
    os.makedirs(name=outputDir)

if args.train:
    # Train POS tagger
    train_sents = nltk.corpus.conll2000.tagged_sents()
    patterns = [
        (r'^[Yy]eah$', 'UH'),
        (r'^[Oo][Hh]$', 'UH'),
        (r'^[UuHh]+$', 'UH'),
        (r'^[MmHh]+$', 'UH'),
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
    with open('tagger.pickle', 'rb') as taggerSerial:
        tagger = pickle.load(taggerSerial)
    print("Loaded tagger")
    with open('chunker.pickle', 'rb') as chunkerSerial:
        chunker = pickle.load(chunkerSerial)
    print("Loaded chunker")

infile = args.infile
if os.path.isdir(infile):
    files = [str(p) for p in Path(infile).glob('**/*.xml')]
    # Do tree concurrently
    from concurrent.futures import ProcessPoolExecutor
    try:
        with ProcessPoolExecutor() as executor:
            executor.map(processFile, files)
    except:
        for f in files:
            processFile(f)
else:
    processFile(infile)
