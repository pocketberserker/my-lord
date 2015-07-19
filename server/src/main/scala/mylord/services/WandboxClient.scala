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

  def list(uri: Uri): Action[List[Compiler]] = {
    implicit val compilersDecoder = jsonOf[List[Compiler]]
    response[List[Compiler]](client(uri))
  }

  def compile(uri: Uri, data: Compile): Action[CompileResult] = {
    implicit val compileEncoder = jsonEncoderOf[Compile]
    implicit val compileResultDecoder = jsonOf[CompileResult]
    val req = POST(uri, data)
    response[CompileResult](client(req))
  }
}

