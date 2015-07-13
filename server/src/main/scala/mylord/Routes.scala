package mylord

import org.http4s.dsl._
import org.http4s.server.HttpService

class Routes {
  def service = HttpService {
    case req @ GET -> Root =>
      Ok("It works!")

    case _ -> Root =>
      MethodNotAllowed()
  }
}
