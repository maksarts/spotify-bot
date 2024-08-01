FROM openjdk:11-slim
LABEL maintaner="maksarts <mmartsa1318@gmail.com>"

ARG JAR_FILE
COPY ${JAR_FILE} app.jar

ARG PYSCRIPTS=/pyscripts
COPY ${PYSCRIPTS} pyscripts/

RUN apt update

RUN apt install -y python3
RUN apt install -y python3-pip

RUN pip3 install vkpymusic
RUN pip3 install argparse

ENTRYPOINT ["java", "-jar", "/app.jar"]