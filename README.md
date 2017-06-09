# CBR Chat Bot

**An interactive chat bot using case based reasoning.**

This web-based chat bot uses CBR techniques to have an English conversation with
a user. It is a summer project I worked on while studying at the University of
Auckland, over the 2016-2017 holidays.


## Binaries
See [releases](https://github.com/agkphysics/ChatBot/releases) to download
binaries for the console only and web app.


## Installation
### Python Script
For processing the XML files using the python script you can use either
[Standord CoreNLP](http://stanfordnlp.github.io/CoreNLP/) or
[NLTK](http://www.nltk.org/).

If using Stanford CoreNLP you will need to download and start a server instance.
Instructions are avaiable at <http://stanfordnlp.github.io/CoreNLP/index.html>.
It is suggested to use the command
```
java -mx1g edu.stanford.nlp.pipeline.StanfordCoreNLPServer 9000
```
to start the CoreNLP server instance.

The python packages `xmltodict`, `pycorenlp` and `nltk` are also required and
can be installed with 
```
pip install xmltodict pycorenlp nltk
```

The script is able to process the Microsoft Research Twitter corpus,
[TalkBank](http://talkbank.org/ "TalkBank") XML files, and
[OANC](http://www.anc.org/data/oanc/contents/) files from the
[American National Corpus](http://www.anc.org/ "ANC") that have been converted
to [inline XML](http://www.anc.org/software/anc-tool/ "ANC Conversion Tool"):

```
python processXML.py process -p [-s] (-t|-a|-w) /path/to/file              # prints details from processing a single file.
python processXML.py process [-s] -o outputdir (-t|-a|-w) /path/to/corpus  # processes all files recursively in `/path/to/corpus` and puts the processed files to `outputdir/`.
python processXML.py [train|process] -h                                    # prints all available options.
```

### Web app
The web app can be placed in the `webapps/` directory of a Java server
instance, either as a .war archive or an exploded-war.


## Building
The project requires at least Java SE 7 to compile correctly.

The bot can be built as a Gradle project using the provided gradle wrapper.
To build the outputs for this project (a .jar and a .war archive) use the
command:
```
gradlew build
```

To run the web app in place, use the command:
```
gradlew jettyRun
```

The project can also be loaded as an Eclipse project and built from within
Eclipse.


## Running
To run the bot you can supply the directory that contains the processed XML
files as a command line argument, otherwise the bot will look for the
dependencies in the current directory as well as the `lib/` directory.

The bot is console based as well as web based. It outputs to console info about
the cases it retrieves and modifies in both console and web mode.

To run the console only bot, execute the java class `agk.chatbot.ChatBot`, e.g.
```
java -cp ".;lib\*" agk.chatbot.ChatBot
```

To run a simple standalone web server which takes requests and responds in
JSON format, execute the java class `agk.chatbot.web.BotServer`, e.g.
```
java -cp ".;lib\*" agk.chatbot.web.BotServer
```
