## Outbox pattern using MongoDB

This project contains code that showcases how to use mongodb database to implement outbox pattern. To understand what outbox pattern is, I encourage you to read the article - [transactional-outbox](https://microservices.io/patterns/data/transactional-outbox.html).

Versions of MongoDB earlier than 4.0 doesn't support transactions, hence it is not possible to implement transactional outbox using a version prior to 4.0.

However, instead of using transactions, you can tail MongoDB commit log to observe changes that were made. This concept is well described in the article [transaction-log-tailing](https://microservices.io/patterns/data/transaction-log-tailing.html).

## How to implement transaction log tailing?
We will use MongoDB 3.6 as a database to store the data and Debezium to observe changes made in the database. [Debezium](https://debezium.io/) is a great tool that allows streaming changes from the database to kafka broker, but it is also possible to stream changes to your standalone application. In this project, the second approach is used - [application reads database's oplog](https://debezium.io/documentation/reference/stable/connectors/mongodb.html#_oplog_capture_mode_legacy) that contains all operations performed in the database.

## How to use built-in MongoDB change streams?
