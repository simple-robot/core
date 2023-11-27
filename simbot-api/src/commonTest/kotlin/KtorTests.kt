import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class KtorTests {


    @Test
    fun ktorUnloadTest() = runTest {

        val client = HttpClient()
        client.post {
            setBody(MultiPartFormDataContent(formData {
                append("description", "Ktor logo")
                // append()
                // append("image", File("ktor_logo.png").readBytes(), Headers.build {
                //     append(HttpHeaders.ContentType, "image/png")
                //     append(HttpHeaders.ContentDisposition, "filename=\"ktor_logo.png\"")
                // })
            }))

            // formData = formData {
            //     append("description", "Ktor logo")
            //     append("image", File("ktor_logo.png").readBytes(), Headers.build {
            //         append(HttpHeaders.ContentType, "image/png")
            //         append(HttpHeaders.ContentDisposition, "filename=\"ktor_logo.png\"")
            //     })
            // }

        }

    }


}
