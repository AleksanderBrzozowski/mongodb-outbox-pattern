import io.debezium.engine.DebeziumEngine
import io.debezium.engine.format.Json
import mu.KLogging
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import io.debezium.config.Configuration as DebeziumConfiguration

class DebeziumChangeStreamsTest : BaseTest({
    val client = KMongo.createClient(mongoDBContainer.connectionString)

    val debeziumProps = DebeziumConfiguration.create()
        .with("name", "mongo-outbox-connector")
        .with("offset.storage", "org.apache.kafka.connect.storage.MemoryOffsetBackingStore")
        .with("connector.class", "io.debezium.connector.mongodb.MongoDbConnector")
        .with("tasks.max", "1")
        .with("mongodb.name", "mongo")
        .with("mongodb.hosts", "127.0.0.1:${mongoDBContainer.getMappedPort(27017)}")
        .with("collection.include.list", "test.person")
        .with("capture.mode", "change_streams")
        .build()
        .asProperties()


    "should capture changes from the database" {
        logger.info { "Connection string: ${mongoDBContainer.connectionString}" }
        val database = client.getDatabase("test")
        val collection = database.getCollection<Person>("person")
        collection.insertOne(Person(name = "Marek"))
        collection.insertOne(Person(name = "Jarek"))
        collection.insertOne(Person(name = "Jan"))

        val engine = DebeziumEngine.create(Json::class.java)
            .using(debeziumProps)
            .notifying { record ->
                logger.info { "Received record: ${record.value()}" }
            }
            .build()

        engine.run()
    }
}) {
    private data class Person(val name: String)
    private companion object : KLogging()
}
