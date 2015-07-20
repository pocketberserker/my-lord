package mylord
package json

import com.github.nscala_time.time.Imports._
import argonaut._
import Codec._
import scalaz._
import scalaprops._

object CodecTest extends Scalaprops {

  implicit val equalDateTime: Equal[DateTime] = Equal.equalA

  implicit def arbDateTime: Gen[DateTime] = Gen.value(DateTime.now)

  val encodeAndDecodeUtcDateTime = JsonChecker.law[DateTime]
}
