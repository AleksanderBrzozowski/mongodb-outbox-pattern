import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import mu.KLogging
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

class ChangeStreamsTest : BaseTest({
    val client = KMongo.createClient(mongoDBContainer.connectionString)

    "should capture changes from the database" {
        val database = client.getDatabase("test")
        database.createCollection("person")
        val collection = database.getCollection<Person>("person")
        val filters = Filters.`in`("operationType", listOf("insert"))
        val aggregates = Aggregates.match(filters)
        val iterator = client.watch().iterator()
        // val iterator = collection.watch(listOf(aggregates)).iterator()
        // collection.watch()
        //     .listen { change ->
        //         logger.info { "Change: ${change.fullDocument}" }
        //     }
        collection.insertOne(Person(name = "John"))
        collection.insertOne(Person(name = "Brad"))
        collection.insertOne(Person(name = "Anthony"))
        iterator.use {
            logger.info { "changes: ${it.next()}" }
        }
        Thread.sleep(1000)
    }
}) {
    private data class Person(val name: String)
    private companion object : KLogging()
}
