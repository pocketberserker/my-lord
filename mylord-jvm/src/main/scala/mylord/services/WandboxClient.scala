package mylord
package services

import argonaut._, Argonaut._
import scalaz._
import scalaz.concurrent.Task
import org.http4s._
import org.http4s.dsl._
import org.http4s.argonaut._
import org.http4s.client._
import org.http4s.client.blaze.defaultClient
import org.http4s.Status.ResponseClass.Successful

class WandboxClient() {

  import Wandbox._

  private val client = defaultClient

  private def response[T](res: Task[Response])(implicit D: EntityDecoder[T]) : Action[T] =
    EitherT(res.flatMap {
      case Successful(resp) => resp.as[T].map(\/-(_))
      case resp => Task.now(-\/(resp.status.toString))
    })

  def list: Action[List[Compiler]] = {
    implicit val compilersDecoder = jsonOf[List[Compiler]]
    Url.list.flatMap(uri => response[List[Compiler]](client(uri)))
  }

  def compile(c: Compile): Action[CompileResult] = {
    implicit val compileEncoder = jsonEncoderOf[Compile]
    implicit val compileResultDecoder = jsonOf[CompileResult]
    Url.compile.flatMap(uri => {
      val req = POST(uri, c)
      response[CompileResult](client(req))
    })
  }

  def permlink(link: Path): Action[PermanentLink] = {
    implicit val permLinkDecoder = jsonOf[PermanentLink]
    Url.permlink(link.toString)
      .flatMap(uri => response[PermanentLink](client(uri)))
  }
}

