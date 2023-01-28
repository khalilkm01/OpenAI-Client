package gateway.implementation

import config.Config
import services.OpenAIService
import gateway.Gateway
import zio.{ Console, RIO }

final case class ConsoleGateway() extends Gateway[OpenAIService with Console, Config.OpenAIConfig]:

  override val startingMessage: String = ???

  override def start: RIO[Environment, Unit] = ???

object ConsoleGateway
