package mylord
package services

import Wandbox._
import argonaut._, Argonaut._
import scalaz._
import org.scalatest.FlatSpec
import scalaprops._
import JsonChecker._

class WandboxTest extends FlatSpec {

  "compiler json" should "decode object" in {
    val input = """{
  "compiler-option-raw":true,
  "runtime-option-raw":false,
  "display-compile-command":"g++ prog.cc",
  "switches":[{
    "default":true,
    "name":"warning",
    "display-flags":"-Wall",
    "display-name":"Warnings"
  },{
    "default":"boost-nothing",
    "options":[{
      "name":"boost-nothing",
      "display-flags":"",
      "display-name":"Don't Use Boost"
    }]
  }],
  "name":"gcc-head",
  "version":"4.9.0 20131031 (experimental)",
  "language":"C++",
  "display-name":"gcc HEAD"
}
"""

    val expected = Compiler(
      "gcc-head",
      "4.9.0 20131031 (experimental)",
      "C++",
      "gcc HEAD",
      true,
      false,
      "g++ prog.cc",
      List(
        SingleSwitch(true, SwitchOption("warning", "-Wall", "Warnings")),
        MultipleSwitch("boost-nothing", List(SwitchOption("boost-nothing", "", "Don't Use Boost")))))

    assert(Parse.decodeOption[Compiler](input) === Some(expected))
  }
}

object WandbodCheck extends Scalaprops {

  implicit val genString = Gen.alphaLowerString

  implicit val arbCompile: Gen[Compile] = for {
      compiler <- Gen[String]
      code <- Gen[String]
      codes <- Gen[List[String]]
      options <- Gen[Option[String]]
      stdin <- Gen[Option[String]]
      compilerOptionRaw <- Gen[Option[String]]
      runtimeOptionRaw <- Gen[Option[String]]
      save <- Gen[Option[Boolean]]
    } yield Compile(compiler, code, codes, options, stdin, compilerOptionRaw, runtimeOptionRaw, None, save)

  implicit val equalCompile: Equal[Compile] = Equal.equalA

  val encodeAndDecodeCompile = JsonChecker.law[Compile]

  implicit val arbCompileResult: Gen[CompileResult] = Gen(for {
      status <- Gen[Int]
      signal <- Gen[Option[String]]
      compilerOutput <- Gen[Option[String]]
      compilerError <- Gen[Option[String]]
      compilerMessage <- Gen[Option[String]]
      programOutput <- Gen[Option[String]]
      programError <- Gen[Option[String]]
      programMessage <- Gen[Option[String]]
      permanentLink <- Gen[Option[String]]
      url <- Gen[Option[String]]
    } yield CompileResult(
      status,
      signal,
      compilerOutput,
      compilerError,
      compilerMessage,
      programOutput,
      programError,
      programMessage,
      permanentLink,
      url))

  implicit val equalCompileResult: Equal[CompileResult] = Equal.equalA

  val encodeAndDecodeCompileResult = JsonChecker.law[CompileResult]
}