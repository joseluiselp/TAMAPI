FROM openjdk:11

WORKDIR /app

COPY ./target/tam-java-api-0.0.1-SNAPSHOT.jar .

EXPOSE 8001

ENTRYPOINT ['java', '-jar', 'tam-java-api-0.0.1-SNAPSHOT.jar']