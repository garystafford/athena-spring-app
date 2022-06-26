# Notes

Reference: <https://spring.io/guides/gs/spring-boot-docker/>

```shell
export TAG=0.10.0

# 1x
docker pull docker.io/library/amazoncorretto:17.0.3

./gradlew clean build

# or skip tests
./gradlew clean build -x test

docker build \
  -f docker/Dockerfile \
  --build-arg JAR_FILE=build/libs/\*.jar \
  -t garystafford/athena-spring-app:${TAG} .

docker push garystafford/athena-spring-app:${TAG}

docker stack deploy -c docker/docker-compose.yml athena-spring

# all commands
export TAG=0.10.0 \
&& ./gradlew clean build -x test \
&& docker build \
  -f docker/Dockerfile \
  --build-arg JAR_FILE=build/libs/\*.jar \
  -t garystafford/athena-spring-app:${TAG} . \
&& docker push garystafford/athena-spring-app:${TAG}

```