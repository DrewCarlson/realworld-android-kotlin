package realworld.ui.signin

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

class SigninEffectHandler(
  private val output: Consumer<Any>,
  override val kodein: Kodein
) : Connection<SigninEffect>,
  CoroutineScope,
  KodeinAware {

  private val job = SupervisorJob()

  override val coroutineContext: CoroutineContext
    get() = job + Dispatchers.Main

  private val conduitService by instance<ConduitService>()

  override fun accept(value: SigninEffect) {
    when (value) {
      is SigninEffect.SignIn -> {
        launch {
          try {
            val response = conduitService.login(value.email, value.password)

            output.accept(SigninEvent.OnSigninComplete(user = response.user))
            println(response)
          } catch (e: ResponseException) {
            println(e.response.content.readUTF8Line())

            val res = e.response
            when (res.status) {
              HttpStatusCode.UnprocessableEntity -> {
                // TODO: Dissect error body
                output.accept(SigninEvent.OnSigninError("Please check your Username and Password."))
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
