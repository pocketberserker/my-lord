package mylord

import com.typesafe.config.ConfigFactory
import argonaut._, Argonaut._
import scalaz._
import scalaz.concurrent.Task
import scalaz.concurrent.Task._
import org.http4s._
import org.http4s.dsl._
import org.http4s.server.HttpService
import org.http4s.argonaut._
import services._

class Routes {

  import Wandbox._

  private val config = ConfigFactory.load()
  private val baseUrl = config.getString("wandbox.url")
  private def url(path: String): Action[Uri] =
    EitherT(Task.now(Uri.fromString(s"$baseUrl$path"))).leftMap(_.toString)
  private val listUri = url("list.json")
  private val compileUri = url("compile.json")

  def service = HttpService {
    case GET -> Root / "list.json" =>
      listUri.flatMap(new WandboxClient().list(_))
        .run
        .flatMap(_.fold(e => InternalServerError(e), res => Ok(res)(jsonEncoderOf[List[Compiler]])))
    case req @ POST -> Root / "compile.json" =>
      (for {
        u <- compileUri
        c <- req.attemptAs[Compile](jsonOf[Compile]).leftMap(_.toString)
        res <- new WandboxClient().compile(u, c.copy(save = Some(true)))
      } yield res)
        .run
        .flatMap(_.fold(e => InternalServerError(e), res => Ok(res)(jsonEncoderOf[CompileResult])))
    case _ -> Root =>
      MethodNotAllowed()
  }
}
