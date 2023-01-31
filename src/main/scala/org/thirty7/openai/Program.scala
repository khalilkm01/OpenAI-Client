package org.thirty7.openai

import config.Config
import clients.implementation.OpenAIClientLive
import services.OpenAIService
import services.implementation.OpenAIServiceLive
import gateway.Gateway
import gateway.implementation.ConsoleGateway

import zio.*
import zio.logging.slf4j.bridge.Slf4jBridge

object Program:
  private type Services = OpenAIService

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

  private lazy val configuration = openAIConfiguration ++ serverConfiguration

  private lazy val services: TaskLayer[Services] =
    ZLayer.make[OpenAIService](
      OpenAIServiceLive.layer,
      OpenAIClientLive.layer,
      openAIConfiguration
    )

  private lazy val gatewayLayer: TaskLayer[ConsoleGateway.GatewayOut] =
    ZLayer.make[ConsoleGateway.GatewayOut](ConsoleGateway.layer, services)

  private lazy val logger: ULayer[Unit] =
    Slf4jBridge.initialize

  private lazy val AppLayers = gatewayLayer ++ logger ++ services ++ configuration

  lazy val make: ZIO[ZIOAppArgs with Scope, Any, Unit] = {
    for {
      _       <- ZIO.log("Starting app")
      gateway <- ZIO.serviceWith[ConsoleGateway.GatewayOut](_.start)
      _       <- gateway.raceAwait(ZIO.never)
    } yield ()
  }.provide(AppLayers)
