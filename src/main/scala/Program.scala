import config.Config
import zio._
import zio.logging.slf4j.bridge.Slf4jBridge

object Program:
  private lazy val openAIConfiguration: TaskLayer[Config.OpenAIConfig] =
    ZLayer.scoped {
      for {
        openAIConfig <- ZIO
          .serviceWith[Config.AppConfig](_.openAIConfig)
          .provide(Config.layer)
          .orDie
      } yield openAIConfig
    }

  private lazy val serverConfiguration: TaskLayer[Config.ServerConfig] =
    ZLayer.scoped {
      for {
        serverConfig <- ZIO
          .serviceWith[Config.AppConfig](_.serverConfig)
          .provide(Config.layer)
          .orDie
      } yield serverConfig
    }

  private lazy val logger: ULayer[Unit] =
    Slf4jBridge.initialize

  private lazy val Layers = openAIConfiguration ++ logger

  lazy val make: ZIO[ZIOAppArgs with Scope, Any, Unit] = {
    for _ <- ZIO.succeed(())
    yield ()
  }.provide(Layers)
