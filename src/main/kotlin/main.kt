import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.receive
import io.ktor.request.receiveParameters
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, 8080) {
        routing {
            post("compile") {
                val code = call.receive<String>()
                println(code)
                call.respond(code)
            }
        }
    }.start(wait = true)
}