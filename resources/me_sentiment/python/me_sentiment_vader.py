#!/usr/bin/python3

"""
File: me_sentiment_vader.py
Description: Python script implementing sentiment analysis using NLTK module (VADER sentiment analysis)
Author: xorman00 <xorman00@stud.fit.vutbr.cz>
"""

import nltk
import sys
from nltk.sentiment.vader import SentimentIntensityAnalyzer
from nltk import tokenize

#===================================================================================================================
#												MAIN
#===================================================================================================================
def main():
	analyzer = SentimentIntensityAnalyzer()

	we_rollin = True
	while (we_rollin):
		try:
			message = input()
			#message = sys.stdin.readlines()
		except:
			we_rollin = False
		else:
			if message == "<<exit>>":
				we_rollin = False
			else:
				polarity_data = analyzer.polarity_scores(message)
				#Return positive, negative, neutral
				if polarity_data['compound'] > 0.2 :
					print("positive")
				elif polarity_data['compound'] < -0.2:
					print("negative")
				else:
					print("neutral")
	print("<<exit>>")


#===================================================================================================================
#												FUNKCIE
#===================================================================================================================

#===================================================================================================================
#												TRIEDY
#===================================================================================================================

if __name__ == "__main__":
	main()

#    - '/home/adam/nltk_data'
 #   - '/usr/share/nltk_data'
  #  - '/usr/local/share/nltk_data'
   # - '/usr/lib/nltk_data'
    #- '/usr/local/lib/nltk_data'
