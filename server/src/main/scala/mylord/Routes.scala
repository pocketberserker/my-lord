package mylord

import argonaut._, Argonaut._
import scalaz._
import scalaz.concurrent.Task._
import org.http4s._
import org.http4s.dsl._
import org.http4s.server.HttpService
import org.http4s.argonaut._
import services._

// copy from https://github.com/http4s/http4s/commit/c64a0df2148351d955387dab9b407a9a71e5365e
object /: {
  def unapply(path: Path): Option[(String, Path)] = {
    path.toList match {
      case Nil => None
      case head :: tail => Some((head, Path(tail)))
    }
  }
}

class Routes {

  import Wandbox._

  def service = HttpService {
    case GET -> Root / "list.json" =>
      new WandboxClient().list
        .run
        .flatMap(_.fold(e => InternalServerError(e), res => Ok(res)(jsonEncoderOf[List[Compiler]])))
    case req @ POST -> Root / "compile.json" =>
      (for {
        c <- req.attemptAs[Compile](jsonOf[Compile]).leftMap(_.toString)
        res <- new WandboxClient().compile(c.copy(save = Some(true)))
      } yield res)
        .run
        .flatMap(_.fold(e => InternalServerError(e), res => Ok(res)(jsonEncoderOf[CompileResult])))
    case GET -> "permlink" /: link =>
      new WandboxClient().permlink(link)
        .run
        .flatMap(_.fold(e => InternalServerError(e), res => Ok(res)(jsonEncoderOf[PermanentLink])))
    case _ -> Root =>
      MethodNotAllowed()
  }
}
