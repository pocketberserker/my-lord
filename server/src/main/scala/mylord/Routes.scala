package mylord

import argonaut._, Argonaut._
import scalaz._
import scalaz.concurrent.Task._
import org.http4s._
import org.http4s.dsl._
import org.http4s.server.HttpService
import org.http4s.argonaut._
import services._

class Routes {

  import Wandbox._

  def service = HttpService {
    case GET -> Root / "list.json" =>
      Url.list.flatMap(new WandboxClient().list(_))
        .run
        .flatMap(_.fold(e => InternalServerError(e), res => Ok(res)(jsonEncoderOf[List[Compiler]])))
    case req @ POST -> Root / "compile.json" =>
      (for {
        u <- Url.compile
        c <- req.attemptAs[Compile](jsonOf[Compile]).leftMap(_.toString)
        res <- new WandboxClient().compile(u, c.copy(save = Some(true)))
      } yield res)
        .run
        .flatMap(_.fold(e => InternalServerError(e), res => Ok(res)(jsonEncoderOf[CompileResult])))
    case _ -> Root =>
      MethodNotAllowed()
  }
}
