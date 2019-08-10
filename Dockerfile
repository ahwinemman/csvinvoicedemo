FROM java:openjdk-8-jdk
VOLUME /tmp
EXPOSE 8080

RUN echo "Etc/UTC" | tee /etc/timezone && dpkg-reconfigure --frontend noninteractive tzdata
WORKDIR /app
ADD pom.xml /app/pom.xml
ADD .mvn /app/.mvn
ADD mvnw /app/mvnw

RUN chmod +x mvnw && ./mvnw compile
ADD . /app
RUN ./mvnw clean package
RUN sh -c 'touch target/csvservice.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","target/csvservice.jar"]