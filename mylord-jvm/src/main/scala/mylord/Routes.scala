package mylord

import argonaut._, Argonaut._
import scalaz._
import scalaz.concurrent.Task
import scalaz.concurrent.Task._
import org.http4s._
import org.http4s.dsl._
import org.http4s.server._
import org.http4s.argonaut._
import org.log4s.getLogger
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

  private val logger = getLogger

  private def response[T](a:Action[T])(implicit E: EncodeJson[T]): Task[Response] =
    a.run
      .flatMap(_.fold(e => {
          logger.error(e)
          InternalServerError(e)
        }, res => Ok(res)(jsonEncoderOf[T])))

  def service: HttpService = Router(
    "api" -> apiService,
    "" -> rootService
  )

  def apiService = HttpService {
    case GET -> Root / "list.json" =>
      response[List[Compiler]](new WandboxClient().list)
    case req @ POST -> Root / "compile.json" =>
      response[CompileResult](for {
        c <- req.attemptAs[Compile](jsonOf[Compile]).leftMap(_.toString)
        res <- new WandboxClient().compile(c.copy(save = Some(true)))
      } yield res)
    case GET -> "permlink" /: link =>
      response[PermanentLink](new WandboxClient().permlink(link))
  }

  def rootService = HttpService {
    case req @ GET -> Root =>
      StaticFile.fromResource("/staticviews/index.html", Some(req))
        .map(Task.now _)
        .getOrElse(NotFound())
    case req if req.pathInfo.startsWith("/static") =>
      StaticFile.fromResource(req.pathInfo, Some(req)).fold(NotFound())(Task.now _)
  }
}
