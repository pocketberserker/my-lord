package mylord

import com.typesafe.config.ConfigFactory
import scalaz.EitherT
import scalaz.concurrent.Task
import org.http4s.Uri

abstract class Url(serviceName: String) {

  private val config = ConfigFactory.load()

  // TODO: Action[String] にする
  private val baseUrl = config.getString(s"$serviceName.url")

  def gen(path: String): Action[Uri] =
    EitherT(Task.now(Uri.fromString(s"$baseUrl$path"))).leftMap(_.toString)
}
