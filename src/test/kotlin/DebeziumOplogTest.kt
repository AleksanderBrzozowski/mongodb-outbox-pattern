import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.debezium.engine.DebeziumEngine
import io.debezium.engine.format.Json
import io.kotest.assertions.timing.eventually
import io.kotest.matchers.shouldBe
import mu.KLogging
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.milliseconds
import io.debezium.config.Configuration as DebeziumConfiguration

class DebeziumOplogTest : BaseTest({
    val client = KMongo.createClient(mongoDBContainer.connectionString)
    val objectMapper = jacksonObjectMapper()
    lateinit var executor: ExecutorService

    val debeziumProps = DebeziumConfiguration.create()
        .with("name", "mongo-outbox-connector")
        .with("offset.storage", "org.apache.kafka.connect.storage.MemoryOffsetBackingStore")
        .with("connector.class", "io.debezium.connector.mongodb.MongoDbConnector")
        .with("tasks.max", "1")
        .with("mongodb.name", "mongo")
        .with("mongodb.hosts", "127.0.0.1:${mongoDBContainer.getMappedPort(27017)}")
        .with("collection.include.list", "test.person")
        .with("capture.mode", "oplog")
        .build()
        .asProperties()

    beforeEach {
        executor = Executors.newSingleThreadExecutor()
    }

    afterEach {
        executor.shutdown()
    }

    fun insertPersons(persons: List<Person>) {
        val database = client.getDatabase("test")
        val collection = database.getCollection<Person>("person")
        collection.insertMany(persons)
    }

    "should capture changes from the database" {
        val personsToInsert = listOf(
            Person(name = "John"),
            Person(name = "Brad"),
            Person(name = "Anthony")
        )
        insertPersons(personsToInsert)

        val capturedPersons = mutableListOf<Person>()
        val engine = DebeziumEngine.create(Json::class.java)
            .using(debeziumProps)
            .notifying { record ->
                logger.info { "Received record: ${record.value()}" }
                val insertedPayload = objectMapper.readTree(record.value())
                    .path("payload")
                    .path("after")
                val insertedPerson = objectMapper.readValue<Person>(insertedPayload.textValue())
                capturedPersons += insertedPerson
            }
            .build()

        executor.submit { engine.run() }
        afterEach { engine.close() }

        eventually(1000.milliseconds) {
            capturedPersons shouldBe personsToInsert
        }
    }
}) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class Person(val name: String)
    private companion object : KLogging()
}
