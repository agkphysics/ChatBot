# CBR Chat Bot

**An interactive chat bot using case based reasoning.**

This chat bot uses CBR techniques to have a conversation with a user.
It is a summer project I have been working on while studying at the University
of Auckland.


## Installation
### Dependencies
See the respective README.txt file in JavaBot/lib for instructions on how to
install the required java dependencies.

The bot also requires a copy of WordNet to be installed. This can be downloaded
from <https://wordnet.princeton.edu/wordnet/download/>.

### Python Script
For processing the XML files using the python script you can use either
[Standord CoreNLP](http://stanfordnlp.github.io/CoreNLP/) or
[NLTK](http://www.nltk.org/).

If using Stanford CoreNLP you will need to download and start a server instance.
Instructions are avaiable at <http://stanfordnlp.github.io/CoreNLP/index.html>.
It is suggested to use the command
`java -mx1g edu.stanford.nlp.pipeline.StanfordCoreNLPServer 9000`
to start the CoreNLP server instance.

The python packages `xmltodict`, `pycorenlp` and `nltk` are also required and can be
installed with `pip install xmltodict pycorenlp nltk`.

The script is able to process [TalkBank](http://talkbank.org/ "TalkBank") XML
files, as well as [OANC](http://www.anc.org/data/oanc/contents/) files from the
[American National Corpus](http://www.anc.org/ "ANC"), which have been converted
to [inline XML](http://www.anc.org/software/anc-tool/ "ANC Conversion Tool").

* `python processXML.py process -p [-s] (-t|-a|-w) /path/to/file` prints details
from processing a single file.
* `python processXML.py process [-s] -o outputdir (-t|-a|-w) /path/to/corpus`
process all files recursively in `/path/to/corpus` and puts the processed files
to `outputdir/`.
* `python processXML.py [train|process] -h` prints all available options.


## Building
The bot can be built as an Eclipse project. Otherwise an Ant build script is
provided in `JavaBot/build.xml`, offering a number of different build types.
In order to correctly build you may need to edit some of the properties in this
file such as `wn.dir`, `xml.dir` and `web.deploy.dir`.

## Running
To run the bot you must supply the directory that contains the processed XML
files as a command line argument, otherwise the bot will look for the
directories in the current director as well as the `lib/` directory.

The bot is console based as well as web based. It output to console info about the
cases it retrieves and modifies.

To execute a simple standalone web server which takes requests and responds in
JSON format, execute the main class `agk.chatbot.BotServer`, e.g.
`java -cp ".;lib\*" agk.chatbot.BotServer`

The webapp can also be placed in the `webapps/` directory of a Tomcat server
instance. To build the webapp execute `ant web` from within the `JavaBot/`
directory.
