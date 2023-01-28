package config

import zio.{ Layer, TaskLayer }
import zio.config.magnolia.descriptor
import zio.config.typesafe.TypesafeConfig
import zio.config.{ ConfigDescriptor, ReadError }
import zio.config.ConfigDescriptor._
import zio.config.syntax.*

object Config:

  case class AppConfig(
    openAIConfig: OpenAIConfig,
    serverConfig: ServerConfig
  )

  case class OpenAIConfig(
    root: String,
    apiKey: String
  )
  case class ServerConfig(host: String, port: Int)

  type AllConfig = AppConfig with OpenAIConfig with ServerConfig

  private final val Root = "application-conf"
  private final val Descriptor: ConfigDescriptor[AppConfig] =
    descriptor[AppConfig]

  val appConfig: Layer[ReadError[String], AppConfig] =
    TypesafeConfig.fromResourcePath(nested(Root)(Descriptor))

  val layer: TaskLayer[AllConfig] =
    appConfig >+>
      appConfig.narrow(_.openAIConfig)
      >+> appConfig.narrow(_.serverConfig)
