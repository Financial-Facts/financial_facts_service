FROM amazoncorretto:17.0.7-alpine
RUN apk --no-cache add curl
ADD target/financial_facts_service-0.0.1-SNAPSHOT.jar financial_facts_service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","financial_facts_service.jar"]