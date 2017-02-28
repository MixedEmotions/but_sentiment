FROM library/ubuntu
MAINTAINER Adam Ormandy xorman00@stud.fit.vutbr.cz

ENV LANG C.UTF-8
RUN locale-gen en_US.UTF-8 cs_CZ.utf8

RUN apt-get update
RUN apt-get install -y default-jdk python3 maven
RUN apt-get install -y wget unzip apt-utils

RUN mkdir /home/sentiment
RUN mkdir /home/sentiment/resources
RUN mkdir /home/sentiment/src

ADD ./  /home/sentiment/
WORKDIR /home/sentiment

RUN mvn install
RUN ./download_datasets.sh
RUN apt-get remove -y unzip
RUN apt-get remove -y wget unzip maven

ENTRYPOINT ["java","-jar","./target/but_sentiment-jar-with-dependencies.jar"]
