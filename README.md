## Prerequisites

- Java 21
- Postgres 14.15
- docker, docker-compose

Maximum files to post is 20MB, can be changed on `application.properties` file.
Accept only wav/aiff file for bot POST and GET.
I'm not using any external webserver (nginx etc).
I'm not using external storage for audio files.
User generated via uuid string, but predefined user-id is `uuid-0012` can be used to test phrase API endpoint.

## How to run

Build java app using

```bash
./gradlew api:bootJar
```

Make sure docker and docker-compose installed and connected to internet to allow download some images.

Run db+app using docker compose using

```bash
docker-compose up
```

### TODO
Find out howto simplify audio converter so it become more modular

Cleaning out temp files. If we don't use any storage with lifecycle control,
then I think we should create another java app (to have better control and logging)
to clean out temp files without adding latency to main app.

Adding integration test for audio converter and audio storage.

Wider control for ControllerAdvisor. Need to expand exception to have better control of error message.