# Invoice Generator

Demo application to generate invoice from loaded CSV file.

[Sample CSV File](src/main/resources/static/test.csv).


## Requirements
Application was built on [Spring Boot 2](https://start.spring.io/), requirements include: 
* JDK 1.8
* Maven
* Docker (optional)

## Building with Maven
Build with packaged Maven 

```shell script
./mvnw clean package
```

or with system-wide Maven

```shell script
mvn clean package
```              

### Running with  Maven
You can run with Maven using Spring Boot:
```shell script
mvn spring-boot:run
```
 
### Running with Java  (production mode)

To run built file with Java:
```shell script
java -jar target/csvservice.jar
```                   

By default, application runs on port  8080, you can override this by setting `SERVER_PORT` environment variable.         

Visit http://localhost:8080

##  Docker

If you do not have Java pre-installed, you can build and run application using Docker.

```shell script
docker build . -t 'csvservice-app'
#run with
docker run -P csvservice-app 
```