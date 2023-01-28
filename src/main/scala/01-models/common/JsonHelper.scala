package models.common

import org.joda.time.{ DateTime, Instant }
import zio.json.{ DeriveJsonCodec, JsonCodec, JsonDecoder, JsonEncoder }

import scala.deriving.Mirror

object JsonHelper:
  inline def deriveCodec[A](using m: Mirror.Of[A]): JsonCodec[A] = DeriveJsonCodec.gen[A]
  given JsonDecoder[DateTime]                                    = JsonDecoder[String].map(DateTime(_))
  given JsonEncoder[DateTime]                                    = JsonEncoder[String].contramap(_.toString)
  given JsonDecoder[Instant]                                     = JsonDecoder[String].map(Instant.parse)
  given JsonEncoder[Instant]                                     = JsonEncoder[String].contramap(_.toString)
