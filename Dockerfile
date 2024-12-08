FROM public.ecr.aws/amazoncorretto/amazoncorretto:21-al2023-headless-arm64

# the JAR file path
ARG JAR_FILE=api/build/libs/api.jar

# Copy the JAR file from the build context into the Docker image
COPY ${JAR_FILE} application.jar

CMD apt-get update -y

ENTRYPOINT ["java","-jar","/application.jar"]