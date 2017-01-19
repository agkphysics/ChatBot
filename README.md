# CBR Chat Bot

**An interactive chat bot using case based reasoning.**

This chat bot uses CBR techniques to have a conversation with a user.
It is a summer project I have been working on while studying at the University
of Auckland.


## Installation
### Java
See the respective README.txt file in JavaBot/lib for instructions on how to
install the required java dependencies.

### Python Script
For processing the XML files using the python script you will need to download
[Standord CoreNLP](http://stanfordnlp.github.io/CoreNLP/) and start a server
instance. Instructions are avaiable at
<http://stanfordnlp.github.io/CoreNLP/index.html>.
It is suggested to use the command
`java -mx1024m edu.stanford.nlp.pipeline.StanfordCoreNLPServer 9000`
to start the CoreNLP server instance.

The python packages `xmltodict` and `pycorenlp` are also required and can be
installed with `pip install xmltodict pycorenlp`.

The script is able to process [TalkBank](http://talkbank.org/ "TalkBank") XML
files, as well as [OANC](http://www.anc.org/data/oanc/contents/) files from the
[American National Corpus](http://www.anc.org/ "ANC"), which have been converted
to [inline XML](http://www.anc.org/software/anc-tool/ "ANC Conversion Tool").

* `python processXML.py -p -s (-t|-a|-w) /path/to/file` prints details from
processing a single file.
* `python processXML.py -s -o outputdir (-t|-a|-w) /path/to/corpus` process all
files recursively in /path/to/corpus and puts the processed files to outputdir/
* `python processXML.py -h` prints all available options.


## Running
To run the bot you must supply the directory that contains the processed XML
files as a command line argument.
The bot is currently console based only. It will also output info about the
cases it retrieves and modifies.
