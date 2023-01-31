package org.thirty7.openai
package gateway

import config.Config
import zio.{ RIO, ULayer }

trait Gateway[ServicesLayer, Configuration]:
  type Services    = ServicesLayer
  type Conf        = Configuration
  type Environment = Services with Conf

  val startingMessage: String

  def start: RIO[Environment, Unit]

object Gateway
