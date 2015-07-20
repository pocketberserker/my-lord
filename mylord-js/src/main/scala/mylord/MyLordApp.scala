package mylord

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import dom.document
import japgolly.scalajs.react.{React, ReactComponentB}
import japgolly.scalajs.react.vdom.prefix_<^._

object MyLordApp extends JSApp {
  @JSExport
  def main(): Unit = {
    val hello =
      ReactComponentB[Unit]("Hello world")
        .render(_ => <.div("Hello world"))
        .buildU

    React.render(hello(), document.body)
  }
}
