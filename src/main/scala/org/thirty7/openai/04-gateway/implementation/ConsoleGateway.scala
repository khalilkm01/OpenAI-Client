package org.thirty7.openai
package gateway.implementation

import config.Config
import services.OpenAIService
import gateway.Gateway
import zio.{ Console, RIO, URLayer, ZIO, ZLayer }

final case class ConsoleGateway(openAIService: OpenAIService) extends Gateway[OpenAIService, Config.OpenAIConfig]:

  override val startingMessage: String = "Starting Console Gateway..."

  override def start: RIO[Environment, Unit] = {
    for {
      _        <- ZIO.log(startingMessage)
      _        <- Console.printLine("Enter Prompt: ")
      prompt   <- Console.readLine
      response <- openAIService.createCompletionWithAda(OpenAIService.CreateCompletionWithAdaDTO(prompt))
      _        <- ZIO.log("Response Received")
      _        <- ZIO.foreach(response.completions)(completion ⇒ Console.printLine(s"$completion"))
    } yield ()
  }.catchAll(error ⇒ Console.printLine(s"Error: ${error.getMessage}"))

object ConsoleGateway:
  type GatewayOut = Gateway[OpenAIService, Config.OpenAIConfig]

  lazy val layer: URLayer[OpenAIService, Gateway[OpenAIService, Config.OpenAIConfig]] =
    ZLayer.fromFunction(ConsoleGateway(_))
