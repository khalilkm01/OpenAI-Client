package gateway.implementation

import config.Config
import models.common.ServerError
import services.OpenAIService
import gateway.Gateway
import zio.{ RIO, URIO, ZIO, ZLayer }
import zio._
import zio.json.JsonEncoder
import zhttp.http.Method.{ GET, POST }
import zhttp.http.{ Http, HttpError, RHttpApp, Response }
import zhttp.service.Server

final case class ApiGateway(openAIService: OpenAIService)
    extends Gateway[OpenAIService, Config.OpenAIConfig with Config.ServerConfig]:

  private val root: String = ???

  private val apiRoute: RHttpApp[Services] = ???

  override val startingMessage: String = ???

  override def start: RIO[Environment, Unit] =
    for {
      serverConfig <- ZIO.service[Config.ServerConfig]
      _ <- ZIO.log(message = startingMessage) *> Server
        .start(
          serverConfig.port,
          apiRoute.withAccessControlAllowOrigin("*") <> Http.notFound
        )
    } yield ()

  private def httpResponseHandler[R, A](
    rio: RIO[R, A],
    onSuccess: A ⇒ Response
  )(implicit jsonEncoder: JsonEncoder[A]): URIO[R, Response] =
    rio.fold(
      {
        case ServerError.DecodeError(msg) ⇒
          Response.fromHttpError(HttpError.BadRequest(msg))
        case ServerError.NotFoundError(msg) ⇒
          Response.fromHttpError(HttpError.NotFound(msg))
        case e ⇒
          Response.fromHttpError(HttpError.InternalServerError(e.getMessage))
      },
      onSuccess
    )

object ApiGateway:
  lazy val layer: URLayer[OpenAIService, Gateway[OpenAIService, Config.OpenAIConfig with Config.ServerConfig]] =
    ZLayer.fromFunction(ApiGateway(_))
