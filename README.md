# Plantation project
I created this project to try-out knowledge obtained during the Udemy course about Spring Cloud.

The system itself is represented by 3 domain microservices and 2 spring-cloud-specific microservices. es.
Besides that, domain services requires PostgreSQL, MongoDB for the persistence layer, and RabbitMQ for async communication.

There is a helm chart for the system to run it within k8s, even though k8s provide capabilities to substitute
some of Spring Cloud solutions, I wanted to combine those 2 approach

### Theme of the services is tea distribution 
- `Plantation service` - creates and manages information about `Tea`, periodically makes a sync-up with `Inventory service` for tea warehousing
- `Inventory service` - service for Tea warehousing holds information about available tea amount for selling
- `Order service` - service that holds tea ordering logic, works as coordinator during order processing between `Plantation service` and `Inventory service`


### How to build
The codebase is organized as a Gradle multi-module project, each microservice registered as an "included build" which means each microservice is an independent build unit

#### Build
- `./gradlew tea-plantation:build`
- `./gradlew tea-inventory:build`
- `./gradlew tea-order:build`
- `./gradlew discovery:build`
- `./gradlew gateway:build`

#### Build docker images
- `./gradlew tea-plantation:dockerBuild`
- `./gradlew tea-inventory:dockerBuild`
- `./gradlew tea-order:dockerBuild`
- `./gradlew discovery:dockerBuild`
- `./gradlew gateway:dockerBuild`

### How to make a helm chart
1) `cd ./helm/tea-plantation`
2) `helm dependencies update`
3) `helm upgrade --install plantation -n plantation .`

After installation Open-api will be available at `http://${NODE_ADDRESS}/swagger-ui.html`

### NOTE
For helm chart you should provide host and port values for MongoDB, PostgreSQL and RabbitMQ
 - `global.postgres.host`
 - `global.postgres.port`
 - `global.rabbitmq.host`
 - `global.rabbitmq.port` 
 - `global.mongodb.host`
 - `global.mongodb.port`
