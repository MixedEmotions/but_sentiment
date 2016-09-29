STS-Gold Sentiment Corpus
-------------------------
-------------------------

The STS-Gold corpus contains two datasets of tweets and entities that have been human-annotated with sentiment labels by 3 PhD students.

1- STS-Gold Tweet
--------------------
This dataset contains 2034 tweets, which the three annotators agreed on their sentiment labels (positive/negative)

*File: sts_gold_tweet_2034.csv
*Format: semicolon-separated CSV file
*Columns: 
	- Id: the tweet id
	- polarity: the sentiment polarity of the tweet (0: negative) (4: positive)
	- tweet: The tweet text. 


1- STS-Gold Entity
---------------------
This dataset contains 58 named entities extracted from the tweets dataset and labelled with 5 sentiment labels. 

*File1: sts_gold_entity_in_tweet.csv
*Format: semicolon-separated CSV file
*Columns:
	- tweet_id: the id of the tweet that the entity appears in
	- entity: the entity as it appears in the tweet
	- entity_sentiment: the sentiment of the entity in the tweet (agreed by the three annotators) (0: negative), (2: neutral) (4: positive) (6: mixed) (8: other)

*File2: sts_gold_entity_aggregated.csv
*Format: semicolon-separated CSV file
*Columns: 
	- entity: the named entity as it appears in the tweets dataset
	- #negative: the number of tweets in which the entity receive negative sentiment (agreed by the three annotators)
	- #positive: the number of tweets in which the entity receive positive sentiment 
	- #neutral: the number of tweets in which the entity receive neutral sentiment
	- #mixed: the number of tweets in which the entity receive mixed sentiment
	- #other: the number of tweets that the annotators agreed that "it is difficult to decide on a proper label"

*Note1: Full description about the construction and the annotation of both datasets can be found in our paper: "Evaluation Datasets for Twitter Sentiment Analysis: A survey and a new dataset, the STS-Gold"

*Note2: Some of the entities in the entity dataset might appear unannotated in some tweets. This is because the annotators did not agree on the sentiment label of these entities in those tweets.

Citation
--------
Please acknowledge the authors by citing the following publication:
Saif, H., Fernandez, M., He, Y., Alani, H.: Evaluation datasets for twitter sentiment analysis. In: Proceedings, 1st Workshop on Emotion and Sentiment in Social and Expressive Media (ESSEM) in conjunction with AI*IA Conference. Turin, Italy (2013)

Contact
-------
Hassan Saif (h.saif@open.ac.uk)
http://tweenator.com/index.php?page_id=13
	
