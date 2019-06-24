package realworld.ui.signup

import io.ktor.client.features.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.io.readUTF8Line
import kotlinx.coroutines.launch
import kt.mobius.Connection
import kt.mobius.functions.Consumer
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.erased.instance
import realworld.service.ConduitService
import kotlin.coroutines.CoroutineContext

class SignupEffectHandler(
  private val output: Consumer<Any>,
  override val kodein: Kodein
) : Connection<SignupEffect>,
  CoroutineScope,
  KodeinAware {

  private val job = SupervisorJob()

  override val coroutineContext: CoroutineContext
    get() = job + Dispatchers.Main

  private val conduitService by instance<ConduitService>()

  override fun accept(value: SignupEffect) {
    when (value) {
      is SignupEffect.Signup -> {
        launch {
          try {
            val response = conduitService.register(
              username = value.username,
              email = value.email,
              password = value.password
            )

            output.accept(SignupEvent.OnSignupComplete(user = response.user))
            println(response)
          } catch (e: ResponseException) {
            println(e.response.content.readUTF8Line())

            val res = e.response
            when (res.status) {
              HttpStatusCode.UnprocessableEntity -> {
                // TODO: Dissect error body
                output.accept(SignupEvent.OnSignupError("Please check your Username and Password."))
              }
            }
            e.printStackTrace(System.err)
          } catch (e: Exception) {
            e.printStackTrace(System.err)
          }
        }
      }
    }
  }

  override fun dispose() {
    job.cancel()
  }
}
