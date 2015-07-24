package org.http4s.server

import org.http4s.server.middleware.URITranslation
import org.http4s.{Status, Response, Request}

import scalaz.concurrent.Task

// copy from https://github.com/http4s/http4s/blob/e4f1b89fa9664fb14ec20f0192aee8139567e4ab/server/src/main/scala/org/http4s/server/Router.scala
// TODO: http4s-server 0.9.0 がリリースされたら正式なものを使う

object Router {
  // TODO A more efficient implementation does not require much imagination
  def apply(mappings: (String, HttpService)*): HttpService = {
    val table = mappings.sortBy(_._1.length).reverse.map { case (prefix, service) =>
      val transformed =
        if (prefix.isEmpty || prefix == "/") service
        else URITranslation.translateRoot(prefix)(service)
      prefix -> transformed
    }

    Service.lift { req =>
      table.find { case (prefix, _) =>
        req.pathInfo.startsWith(prefix)
      }.fold(Service.empty[Request, Response])(_._2)(req)
    }
  }
}
