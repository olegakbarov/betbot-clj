FROM java:8
COPY ./betbot.jar /usr/app/
EXPOSE  8080
ENTRYPOINT ["java", "-jar", "/usr/app/betbot.jar"]