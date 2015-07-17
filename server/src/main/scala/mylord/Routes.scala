package mylord

import com.typesafe.config.ConfigFactory
import argonaut._, Argonaut._
import scalaz._
import scalaz.concurrent.Task
import org.http4s._
import org.http4s.dsl._
import org.http4s.server.HttpService
import org.http4s.argonaut.jsonEncoderOf
import services._

class Routes {

  import Wandbox._

  private val config = ConfigFactory.load()
  private val baseUrl = config.getString("wandbox.url")
  private def url(path: String) = Uri.fromString(s"$baseUrl$path")
  private val listUri = url("list.json")

  def service = HttpService {
    case req @ GET -> Root / "list.json" =>
      Task.now(listUri).flatMap {
        case \/-(x) => new WandboxClient().list(x).flatMap {
          case \/-(res) => Ok(res)(jsonEncoderOf[List[Compiler]])
          // TODO: statusを適切なものに変更する
          case -\/(s) => InternalServerError(s.toString)
        }
        case -\/(e) => InternalServerError(e.toString)
      }

    case _ -> Root =>
      MethodNotAllowed()
  }
}
