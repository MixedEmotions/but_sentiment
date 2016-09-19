FROM library/ubuntu
MAINTAINER Adam Ormandy xorman00@stud.fit.vutbr.cz

RUN apt-get update
RUN apt-get upgrade
RUN apt-get install -y maven
RUN apt-get install -y openjdk-8-jdk maven
RUN apt-get install -y python3
RUN apt-get install -y wget
RUN apt-get install -y unzip
RUN apt-get clean

RUN mkdir /usr/src/sentiment
RUN mkdir /usr/src/sentiment/libs
RUN mkdir /usr/src/sentiment/resources
RUN mkdir /usr/src/sentiment/src

ADD ./resources  /usr/src/sentiment/resources
ADD ./config.ini  /usr/src/sentiment/config.ini
ADD ./src  /usr/src/sentiment/src
ADD ./pom.xml  /usr/src/sentiment/pom.xml
ADD ./download_datasets.sh  /usr/src/sentiment/download_datasets.sh
WORKDIR /usr/src/sentiment

ENV LANG C.UTF-8

RUN mvn install
RUN ./download_datasets.sh

ENTRYPOINT ["java","-jar","target/mefw-0.0.1-jar-with-dependencies.jar"]
