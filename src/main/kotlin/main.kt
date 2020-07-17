import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.request.receive
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.lang.Exception

fun main() {
    embeddedServer(Netty, 37105) {
        routing {
            post("compile") {
                val code = call.receive<String>()

                try {
                    val e = Parser(Lexer(code)).parseExpression()
                    val typechecker = Typechecker()
                    typechecker.infer(typechecker.initialContext, e)

                    typechecker.dataRecorder.applySolutionToRecords(typechecker.solution)
                    val result = eval(initialEnv, e)
                    println(result)
//                    val json = Gson().toJson(typechecker.dataRecorder.getRecords())

                    val jsonMap = mapOf("typecheckData" to typechecker.dataRecorder.getRecords(), "result" to result)
                    println(Gson().toJson(jsonMap))
                    call.respond(Gson().toJson(jsonMap))
                }
                catch (e : Exception) {
                    call.respond("{\"error\": \"${e.message}\"}")
                }


            }
        }
    }.start(wait = true)
}