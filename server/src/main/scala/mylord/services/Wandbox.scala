package mylord
package services

import argonaut._, Argonaut._
import com.github.nscala_time.time.Imports._
import json.Codec._

object Wandbox {

  val name = "wandbox"

  final case class SwitchOption(name: String, displayFlags: String, displayName: String)

  sealed abstract class Switch
  final case class SingleSwitch(default: Boolean, option: SwitchOption) extends Switch
  case class MultipleSwitch(default: String, options: List[SwitchOption]) extends Switch

  final case class Compiler(
    name: String,
    version: String,
    language: String,
    displayName: String,
    compilerOptionRaw: Boolean,
    runtimeOptionRaw: Boolean,
    displayCompileCommand: String,
    switches: List[Switch])

  implicit val SwitchOptionDecodeJson: DecodeJson[SwitchOption] =
    jdecode3L(SwitchOption.apply)("name", "display-flags", "display-name")

  implicit val SwitchDecodeJson: DecodeJson[Switch] =
    DecodeJson(c => (for {
      default <- (c --\ "default").as[Boolean]
      name <- (c --\ "name").as[String]
      displayFlags <- (c --\ "display-flags").as[String]
      displayName <- (c --\ "display-name").as[String]
    } yield SingleSwitch(default, SwitchOption(name, displayFlags, displayName)))
    ||| (for {
      default <- (c --\ "default").as[String]
      options <- (c --\ "options").as[List[SwitchOption]]
    } yield MultipleSwitch(default, options)))

  implicit val CompilerDecodeJson: DecodeJson[Compiler] =
    jdecode8L(Compiler.apply)("name",
      "version",
      "language",
      "display-name",
      "compiler-option-raw",
      "runtime-option-raw",
      "display-compile-command",
      "switches")

  final case class Compile(
    compiler: String,
    code: String,
    codes: List[String],
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
      ("codes" := c.codes) ->:
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
        codes <- (c --\ "codes").as[List[String]]
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

  implicit val PermanentLinkDecodeJson: DecodeJson[PermanentLink] =
    jdecode2L(PermanentLink.apply)("parameter", "result")
}
