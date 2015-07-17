package mylord
package services

import argonaut._, Argonaut._
import scalaz._
import scalaz.concurrent.Task
import org.http4s._
import org.http4s.argonaut.jsonOf
import org.http4s.client.blaze.defaultClient
import org.http4s.Status.ResponseClass.Successful

class WandboxClient() {

  import Wandbox._

  val client = defaultClient

  def list(uri: Uri): Task[Status \/ List[Compiler]] = {
    implicit val compilesDecoder = jsonOf[List[Compiler]]
    client(uri).flatMap {
      case Successful(resp) => resp.as[List[Compiler]].map(\/-(_))
      case resp => Task.now(-\/(resp.status))
    }
  }
}

