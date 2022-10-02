import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FreeSpec
import io.kotest.extensions.testcontainers.TestContainerExtension
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

abstract class BaseTest(body: BaseTest.() -> Unit = {}) : FreeSpec() {

    val mongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:3.6"))

    init {
        install(TestContainerExtension(mongoDBContainer))
        body()
    }
}
