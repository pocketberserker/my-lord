package mylord
package services

import argonaut._, Argonaut._
import com.github.nscala_time.time.Imports._
import json.Codec._

object Wandbox {

  final case class SwitchOption(name: String, displayFlags: String, displayName: String)

  sealed abstract class Switch
  final case class SingleSwitch(default: Boolean, option: SwitchOption) extends Switch
  case class MultipleSwitch(default: String, options: List[SwitchOption]) extends Switch

  implicit val SwitchOptionCodecJson: CodecJson[SwitchOption] =
    CodecJson((o: SwitchOption) =>
      ("name" := o.name) ->:
      ("display-flags" := o.displayFlags) ->:
      ("display-name" := o.displayName) ->:
      jEmptyObject,
      o => for {
        name <- (o --\ "name").as[String]
        displayFlags <- (o --\ "display-flags").as[String]
        displayName <- (o --\ "display-name").as[String]
      } yield SwitchOption(name, displayFlags, displayName))

  val SingleSwitchCodecJson: CodecJson[SingleSwitch] =
    CodecJson((s: SingleSwitch) =>
      ("default" := s.default) ->:
      ("name" := s.option.name) ->:
      ("display-flags" := s.option.displayFlags) ->:
      ("display-name" := s.option.displayName) ->:
      jEmptyObject,
      s => for {
        default <- (s --\ "default").as[Boolean]
        name <- (s --\ "name").as[String]
        displayFlags <- (s --\ "display-flags").as[String]
        displayName <- (s --\ "display-name").as[String]
      } yield SingleSwitch(default, SwitchOption(name, displayFlags, displayName)))

  val MultipleSwitchCodecJson: CodecJson[MultipleSwitch] =
    casecodec2(MultipleSwitch.apply _, MultipleSwitch.unapply _)("default", "options")

  implicit val SwitchEncodeJson: EncodeJson[Switch] =
    EncodeJson(_ match {
      case s@SingleSwitch(_, _) => SingleSwitchCodecJson.encode(s)
      case m@MultipleSwitch(_, _) => MultipleSwitchCodecJson.encode(m)
    })

  implicit val SwitchDecodeJson: DecodeJson[Switch] =
    SingleSwitchCodecJson.map(_.asInstanceOf[Switch]) ||| (MultipleSwitchCodecJson.map(_.asInstanceOf[Switch]))

  final case class Compiler(
    name: String,
    version: String,
    language: String,
    displayName: String,
    compilerOptionRaw: Boolean,
    runtimeOptionRaw: Boolean,
    displayCompileCommand: String,
    switches: List[Switch])

  implicit val CompilerCodecJson: CodecJson[Compiler] =
    CodecJson((c: Compiler) =>
      ("name" := c.name) ->:
      ("version" := c.version) ->:
      ("language" := c.language) ->:
      ("display-name" := c.displayName) ->:
      ("compiler-option-raw" := c.compilerOptionRaw) ->:
      ("runtime-option-raw" := c.runtimeOptionRaw) ->:
      ("display-compile-command" := c.displayCompileCommand) ->:
      ("switches" := c.switches) ->:
      jEmptyObject,
      c => for {
        name <- (c --\ "name").as[String]
        version <- (c --\ "version").as[String]
        language <- (c --\ "language").as[String]
        displayName <- (c --\ "display-name").as[String]
        compilerOptionRaw <- (c --\ "compiler-option-raw").as[Boolean]
        runtimeOptionRaw <- (c --\ "runtime-option-raw").as[Boolean]
        displayCompilerCommand <- (c --\ "display-compile-command").as[String]
        switches <- (c --\ "switches").as[List[Switch]]
      } yield Compiler(name, version, language, displayName, compilerOptionRaw, runtimeOptionRaw, displayCompilerCommand, switches))

  // https://github.com/argonaut-io/argonaut/commit/31c1becfa29f6f7
  // この修正を含むhttp4s-argonautがリリースされるまで使えない
  //implicit CompilersCodesJson: CodecJson[List[Compiler]] =
  //  CodecJson.derived[List[Compiler]]

  final case class Compile(
    compiler: String,
    code: String,
    codes: Option[List[String]],
    options: Option[String],
    stdin: Option[String],
    compilerOptionRaw: Option[String],
    runtimeOptionRaw: Option[String],
    createdAt: Option[DateTime],
    save: Option[Boolean])

  implicit val CompileCodecJson: CodecJson[Compile] =
    CodecJson((c: Compile) =>
      ("compiler" := c.compiler) ->:
      ("code" := c.code) ->:
      (c.codes.map("codes" := _)) ->?:
      (c.options.map("options" := _)) ->?:
      (c.stdin.map("stdin" := _)) ->?:
      (c.compilerOptionRaw.map("compiler-option-raw" := _)) ->?:
      (c.runtimeOptionRaw.map("runtime-option-raw" := _)) ->?:
      (c.createdAt.map("created-at" := _)) ->?:
      (c.save.map("save" := _)) ->?:
      jEmptyObject,
      c => for {
        compiler <- (c --\ "compiler").as[String]
        code <- (c --\ "code").as[String]
        codes <- (c --\ "codes").as[Option[List[String]]]
        options <- (c --\ "options").as[Option[String]]
        stdin <- (c --\ "stdin").as[Option[String]]
        compilerOptionRaw <- (c --\ "compiler-option-raw").as[Option[String]]
        runtimeOptionRaw <- (c --\ "runtime-option-raw").as[Option[String]]
        createdAt <- (c --\ "created-at").as[Option[DateTime]]
        save <- (c --\ "save").as[Option[Boolean]]
      } yield Compile(compiler, code, codes, options, stdin, compilerOptionRaw, runtimeOptionRaw, createdAt, save))

  final case class CompileResult(
    status: Int,
    signal: Option[String],
    compilerOutput: Option[String],
    compilerError: Option[String],
    compilerMessage: Option[String],
    programOutput: Option[String],
    programError: Option[String],
    programMessage: Option[String],
    permanentLink: Option[String],
    url: Option[String])

  implicit val CompileResultCodecJson: CodecJson[CompileResult] =
    casecodec10(CompileResult.apply, CompileResult.unapply)(
      "status",
      "signal",
      "compiler_output",
      "compiler_error",
      "compiler_message",
      "program_output",
      "program_error",
      "program_message",
      "permlink",
      "url")

  final case class PermanentLink(compile: Compile, result: CompileResult)

  implicit val PermanentLinkCodecJson: CodecJson[PermanentLink] =
    casecodec2(PermanentLink.apply, PermanentLink.unapply)("parameter", "result")

  object Url extends WandboxUrl
}
