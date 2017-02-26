FROM library/ubuntu
MAINTAINER Adam Ormandy xorman00@stud.fit.vutbr.cz
ENV LANG C.UTF-8

RUN apt-get update
RUN apt-get install -y default-jre python3
RUN apt-get install -y wget unzip apt-utils

RUN mkdir /home/sentiment
RUN mkdir /home/sentiment/resources
RUN mkdir /home/sentiment/src
RUN mkdir /home/sentiment/target

ADD ./  /home/sentiment/
#ADD ./target/but_sentiment-jar-with-dependencies.jar  /home/sentiment/
#ADD ./src  /home/sentiment/src
#ADD ./resources  /home/sentiment/resources
#ADD ./config.ini  /home/sentiment/config.ini
#ADD ./download_datasets.sh  /home/sentiment/download_datasets.sh

WORKDIR /home/sentiment

RUN ls
RUN ls resources
RUN ./download_datasets.sh
RUN apt-get remove -y unzip
RUN apt-get remove -y wget unzip

ENTRYPOINT ["java","-jar","./target/but_sentiment-jar-with-dependencies.jar"]
