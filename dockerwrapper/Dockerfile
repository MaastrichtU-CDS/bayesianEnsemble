FROM maven:3.8-eclipse-temurin-17-alpine as builder
ARG SSH_PRIVATE_KEY
ARG SSH_PUBLIC_KEY

RUN apk update && apk add openssh git

RUN mkdir /build
RUN mkdir /root/.ssh
RUN echo "$SSH_PRIVATE_KEY" > /root/.ssh/id_rsa && \
    echo "$SSH_PUBLIC_KEY" > /root/.ssh/id_rsa.pub && \
    chmod 600 /root/.ssh/id_rsa && \
    chmod 600 /root/.ssh/id_rsa.pub

# make sure your domain is accepted
RUN touch /root/.ssh/known_hosts
RUN ssh-keyscan github.com >> /root/.ssh/known_hosts
RUN apk add --no-cache gcompat

# Build n-scalar-product protocol
WORKDIR /build
RUN git clone --branch 3.0-stable git@github.com:MaastrichtU-CDS/n-scalar-product-protocol.git

WORKDIR /build/n-scalar-product-protocol/java
RUN mvn install

# Build vertibayes
WORKDIR /build
RUN git clone --branch 3.0-stable-library git@github.com:MaastrichtU-CDS/vertibayes.git
WORKDIR /build/vertibayes
RUN mvn install

WORKDIR /build
RUN git clone --branch 2.0-stable git@github.com:MaastrichtU-CDS/bayesianEnsemble.git

WORKDIR /build/bayesianEnsemble/algorithm
RUN mvn install:install-file -Dfile=./openmarkov/OpenMarkov-0.4.0.jar -DgroupId=org.openmarkov -DartifactId=org.openmarkov.full -Dversion=0.4.0-SNAPSHOT -Dpackaging=jar
RUN mvn install

FROM openjdk:17-slim as runner

# This is a placeholder that should be overloaded by invoking
# docker build with '--build-arg PKG_NAME=...'
ARG PKG_NAME="com.florian.bayesianensemble.wrapper"
ENV JAR_PATH="/app/final.jar"
ENV SERVER_PORT=8888

RUN apt update && apt install -y python3 python3-pip python3-dev g++ musl-dev libffi-dev libssl-dev
RUN ln -sf python3 /usr/bin/python
RUN pip3 install --no-cache setuptools wheel poetry

COPY --from=builder /build/bayesianEnsemble/algorithm/target/final*.jar $JAR_PATH

# install federated algorithm
COPY /src/. /app/src
COPY ./pyproject.toml /app/pyproject.toml


WORKDIR /app
RUN poetry install && poetry cache clear -n --all pypi

ENV PKG_NAME=${PKG_NAME}

# Tell docker to execute `docker_wrapper()` when the image is run.
CMD poetry run python -c "from vantage6.tools.docker_wrapper import docker_wrapper; docker_wrapper('${PKG_NAME}')"