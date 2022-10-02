import mu.KLogging
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

class ChangeStreamsTest : BaseTest({
    val client = KMongo.createClient(mongoDBContainer.connectionString)

    "should capture changes from the database" {
        val database = client.getDatabase("test")
        database.createCollection("person")
        val collection = database.getCollection<Person>("person")
        val iterator = collection.watch().iterator()
        // collection.watch()
        //     .listen { change ->
        //         logger.info { "Change: ${change.fullDocument}" }
        //     }
        collection.insertOne(Person("Jan"))
        collection.insertOne(Person("Marian"))
        collection.insertOne(Person("Marek"))
        iterator.use {
            logger.info { "changes: ${it.next()}" }
        }
        Thread.sleep(1000)
    }
}) {
    private data class Person(val name: String)
    private companion object : KLogging()
}
