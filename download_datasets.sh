#!/bin/bash
# Simple shell script for downloading datasets for lingpipe
# Only dataset included here is sentiment140. Reason being, that it got size of 280 MB, and I am too lazy to include other smaller datasets.

drop_dir="resources/me_sentiment/corpora/"

wget -t 2 "http://cs.stanford.edu/people/alecmgo/trainingandtestdata.zip"  --directory-prefix=$drop_dir"sentiment140" ;\
 unzip $drop_dir"sentiment140/*.zip" -d $drop_dir"sentiment140/" ;ls $drop_dir"sentiment140/"
rm "resources/me_sentiment/corpora/sentiment140/trainingandtestdata.zip"   