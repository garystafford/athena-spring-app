# Developing Spring Boot Applications for Querying Data Lakes on AWS

Source code for the blog post, [Developing Spring Boot Applications for Querying Data Lakes on AWS
](https://garystafford.medium.com/developing-spring-boot-applications-for-querying-data-lakes-on-aws-eeefa298741): Learn to develop Cloud-native RESTful Java services that query and data in an AWS data lake using Amazon Athena

See the post for instructions on using the source code.

## Starting the Spring Boot Service Locally

```shell
export AWS_ACCESS_KEY_ID="<your_access_key>"
export AWS_SECRET_ACCESS_KEY="<your_secret_key>"
export AWS_REGION="<your_region>"
export RESULTS_BUCKET="<s3://<your_athena_results_bucket>"
export NAMED_QUERY_ID="<your_named_query_id>"

./gradlew clean bootRun
```

## Sample Endpoints

```text
http://localhost:8080/v1/acturator
http://localhost:8080/v1/actuator/prometheus
http://localhost:8080/v1/actuator/health

http://localhost:8080/v1/buyerlikes
http://localhost:8080/v1/categories
http://localhost:8080/v1/categories/3
http://localhost:8080/v1/dates?limit=25&offset=50
http://localhost:8080/v1/dates/1827
http://localhost:8080/v1/events?offset=5
http://localhost:8080/v1/events/3
http://localhost:8080/v1/listings?limit=10&offset=2
http://localhost:8080/v1/listings/2
http://localhost:8080/v1/sales?limit=15&offset=25
http://localhost:8080/v1/sales/3
http://localhost:8080/v1/salesbycategory?date=2020-1-1&limit=10
http://localhost:8080/v1/salesbyseller/3
http://localhost:8080/v1/users?limit=25&offset=8
http://localhost:8080/v1/users/3
http://localhost:8080/v1/venues?limit=15&offset=10
http://localhost:8080/v1/venues/5
```
