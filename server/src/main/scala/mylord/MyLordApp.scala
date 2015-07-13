package mylord

import com.typesafe.config.ConfigFactory
import org.http4s.server.blaze.BlazeBuilder

object SideburnsApp {

  def main(args: Array[String]): Unit = {
    val routes = new Routes().service
    val config = ConfigFactory.load()
    BlazeBuilder.bindHttp(config.getInt("app.port"))
      .mountService(routes, config.getString("app.headPath"))
      .run
      .awaitShutdown()
  }
}
