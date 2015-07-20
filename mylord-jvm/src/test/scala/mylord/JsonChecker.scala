package mylord

import argonaut._
import scalaprops._
import scalaz._

object JsonChecker {

  def law[A: DecodeJson: EncodeJson: Gen: Equal] =
    Property.forAll(CodecJson.derived[A].codecLaw.encodedecode _)
}
