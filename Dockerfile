FROM ubuntu:23.04

ARG JAVA_VERSION="21-librca"

USER root

RUN apt-get update && apt-get upgrade -y && \
apt-get install curl unzip zip -y

RUN curl -s "https://get.sdkman.io" | bash

RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && \
yes | sdk install java $JAVA_VERSION"

WORKDIR $HOME/app
COPY --chown=root ./target/SimpleMsngr-1.jar SimpleMsngr-1.jar
EXPOSE 8080
ENTRYPOINT bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && \
java -jar SimpleMsngr-1.jar"