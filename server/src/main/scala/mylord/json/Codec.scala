package mylord
package json

import argonaut._, Argonaut._
import com.github.nscala_time.time.Imports._

object Codec {

  implicit def DateTimeDecodeJson: DecodeJson[DateTime] =
    DecodeJson.optionDecoder(_.string.flatMap(_.toDateTimeOption), "org.joda.time.DateTime")

  implicit def DateTimeEncodeJson: EncodeJson[DateTime] =
    EncodeJson(a => jString(a.toString))
}
