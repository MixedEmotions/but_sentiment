# BUT_SENTIMENT
Java wrapper around several sentiment analysis tools, that was created for MixedEmotions project.

## REGUIREMENTS

This package requires java 1.8 and python3 ( SA_VADER ), maven for instalation and wget for downloading Sentiment140 dataset.

## INSTALATION

1. Use provided shell script to download Sentiment140 dataset needed by lingpipe.
> download_datasets.sh

2. Use maven to build jar file.
> mvn install

Output of this operation will be two files, mefw-0.0.1-jar-with-dependencies.jar and mefw-0.0.1.jar in targer directory.

## USAGE

Prints help.
> mefw (-h | --help)

Prints version of interface/wrapper.
> mefw --version

Prints available processors.
> mefw list processors

Starts up HTTP server daemon. Default port is 80.
> mefw server <ip> [--port=<port> --config=<conf> --jsonld]

Prints if text in input file was positive, neutral or negative.
> mefw process <processor> <inputfile> <outputfile> [--config=<conf> --jsonld]
> Example: $ SA_lingpipe input.txt output.txt

## PROCESSORS

Provided processors are basically wrappers around varius sentiment analysis implementations.

* SA_lingpipe [site](http://alias-i.com/lingpipe/index.html)
* SA_stanford [site](http://nlp.stanford.edu/)
* SA_VADER [site1](http://www.nltk.org/) [site2](https://github.com/cjhutto/vaderSentiment)
* SA_lingpipe_czech [site](http://alias-i.com/lingpipe/index.html)

## ADDING NEW PROCESSORS

All processors have to be stored in directory:
> src/main/java/cz/vutbr/mefw/plugins
> Example: src/main/java/cz/vutbr/mefw/plugins/SA_lingpipe

If your processor uses additional classes, they should be stored in:
> src/main/java/cz/vutbr/mefw/plugins/[name_of_processor_group]
> Example src/main/java/cz/vutbr/mefw/plugins/SA/

Other resources like datasets, libs, etc. belongs in:
> resources/[name_of_processor_group]
> Example: resources/but_sentiment



All processors have to be subclass of **ProcessorAdapter** class, which is abstract class with 3 methods.
If this requirements is not met, you processor will not work.

First method is constructor with config argument. Config is used to specifi path to plugin and resources directory.
> public ProcessorAdapter(Config config);

Second method is load(). This method is used to load external resources, for example datasets.
> public void load();

Third method is process(String data). This method is used for actual processing of data. 
Method returns result of analysis, in case of sentiment analysis tool, this method return positive, neutral or negative.
> public String process(String data)

## CITATIONS

Lingpipe [site](http://alias-i.com/lingpipe/index.html):
> Alias-i. 2008. LingPipe 4.1.0. http://alias-i.com/lingpipe (accessed October 1, 2008)

StanfordCoreNLP [site](http://nlp.stanford.edu/):
> Manning, Christopher D., Mihai Surdeanu, John Bauer, Jenny Finkel, Steven J. Bethard, and David McClosky. 2014. The Stanford CoreNLP Natural Language Processing Toolkit In Proceedings of the 52nd Annual Meeting of the Association for Computational Linguistics: System Demonstrations, pp. 55-60.

VADER: [site](https://github.com/cjhutto/vaderSentiment)
> Hutto, C.J. & Gilbert, E.E. (2014). VADER: A Parsimonious Rule-based Model for Sentiment Analysis of Social Media Text. Eighth International Conference on Weblogs and Social Media (ICWSM-14). Ann Arbor, MI, June 2014.

NLTK [site](http://www.nltk.org/):

TODO

Sentiment140 [site](http://help.sentiment140.com/)

TODO

Sanders [site](http://www.sananalytics.com/lab/twitter-sentiment/):

Not found. 

Movie reviews [site](http://www.cs.cornell.edu/people/pabo/movie-review-data):

TODO

rt_polarity data [site](http://www.nltk.org/):
> Seeing stars: Exploiting class relationships for sentiment categorization with respect to rating scales., Proceedings of the ACL, 2005.

pros-cons [site](https://www.cs.uic.edu/~liub/FBS/sentiment-analysis.html#datasets):

TODO

sts_gold [site](http://www.tweenator.com/index.php?page_id=1):

TODO

Czech sentiment [site](https://lindat.mff.cuni.cz/repository/xmlui/handle/11234/1-1507):
> Fiala, Ondřej, 2015, Aspect-Term Annotated Customer Reviews in Czech, LINDAT/CLARIN digital library at Institute of Formal and Applied Linguistics, Charles University in Prague, http://hdl.handle.net/11234/1-1507. 

