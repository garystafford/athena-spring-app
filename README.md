# Developing Spring Boot Applications for Querying Data Lakes on AWS

Source code for the blog post, [Developing Spring Boot Applications for Querying Data Lakes on AWS](https://medium.com/@garystafford/developing-spring-boot-applications-for-querying-data-lakes-on-aws-eeefa298741): Learn to develop Cloud-native RESTful Java services that query and data in an AWS data lake using Amazon Athena

See the post for instructions on using the source code.

## Starting the Spring Boot Service Locally

```shell
export AWS_ACCESS_KEY_ID=<your_id>
export AWS_SECRET_ACCESS_KEY=<your_secret>
export RESULTS_BUCKET="s3://aws-athena-query-results-<your_bucket>/"
export NAMED_QUERY_ID="<your_named_query_id>"

./gradlew bootRun
```
