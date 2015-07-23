package mylord

import scala.scalajs.js
import japgolly.scalajs.react._
import com.scalawarrior.scalajs.ace._
import japgolly.scalajs.react.vdom.prefix_<^._


class Backend($: BackendScope[_, ReactAceEditor]) {
  var editor: js.UndefOr[Editor] = js.undefined

  def start() = {
    val s = $.state
    val e = ace.edit(s.name)
    s.showPrintMargin.foreach(v => e.setShowPrintMargin(v))
    s.readOnly.foreach(v => e.setReadOnly(v))
    s.showGutter.foreach(v => e.renderer.setShowGutter(v))
    s.highlightActiveLine.foreach(v => e.setHighlightActiveLine(v))
    s.fontSize.foreach(v => e.setFontSize(v))
    s.mode.foreach(v => e.getSession().setMode(s"ace/mode/$v"))
    s.theme.foreach(v => e.setTheme(s"ace/theme/$v"))
    s.value.foreach(v => e.setValue(v, 1))

    editor = e
  }

  def next(n: ReactAceEditor) = {
    val e = ace.edit(n.name)
    n.showPrintMargin.foreach(v => e.setShowPrintMargin(v))
    n.readOnly.foreach(v => e.setReadOnly(v))
    n.showGutter.foreach(v => e.renderer.setShowGutter(v))
    n.highlightActiveLine.foreach(v => e.setHighlightActiveLine(v))
    n.fontSize.foreach(v => e.setFontSize(v))
    n.mode.foreach(v => e.getSession().setMode(s"ace/mode/$v"))
    n.theme.foreach(v => e.setTheme(s"ace/theme/$v"))
    n.value.foreach(v => {
      if(e.getValue() != n.value) {
        e.setValue(v, 1)
      }
    })

    editor = e
  }
}

case class ReactAceEditor(
  showPrintMargin: js.UndefOr[Boolean]= true,
  name: String = "editor",
  height: js.UndefOr[Int] = 400,
  width: js.UndefOr[Int] = 600,
  readOnly: js.UndefOr[Boolean] = false,
  showGutter: js.UndefOr[Boolean] = true,
  highlightActiveLine: js.UndefOr[Boolean] = true,
  fontSize: js.UndefOr[String] = "12",
  mode: js.UndefOr[String] = js.undefined,
  theme: js.UndefOr[String] = js.undefined,
  value: js.UndefOr[String] = "") {

  def component = ReactComponentB[ReactAceEditor]("Ace Editor")
    .initialState(ReactAceEditor())
    .backend(new Backend(_))
    .render($ => <.div(
      ^.id := $.state.name,
      ^.width := $.state.width,
      ^.height := $.state.height))
    .componentDidMount(_.backend.start())
    .componentWillReceiveProps(($, n) => $.backend.next(n))
    .build(this)
}
