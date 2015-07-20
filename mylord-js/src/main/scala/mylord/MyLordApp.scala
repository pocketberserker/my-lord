package mylord

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import dom.document
import japgolly.scalajs.react.{React, ReactComponentB}
import japgolly.scalajs.react.vdom.prefix_<^._
import com.scalawarrior.scalajs.ace._

object MyLordApp extends JSApp {
  @JSExport
  def main(): Unit = {
    val editor = ReactAceEditor(name = "editor", theme = "github", mode = "scala")
      .component

    React.render(editor, document.body)
  }
}
