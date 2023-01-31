package clients.implementation

import models.common.{ ServerError, ZIOResponse }
import zio.http.{ Body, Client }
import zio.http.model.{ Headers, Method, Status }
import zio.{ IO, TaskLayer, ZIO }
import zio.http.service.{ ChannelFactory, EventLoopGroup }
import zio.json.*

trait HttpClient(root: String, headers: Headers):
  import HttpClient.ClientLayer

  private val api: String = root
  

  def performRequest[Req, Res, E >: ServerError](
    endpoint: String,
    request: Req,
    method: Method,
    headers: Headers = headers,
    sendContent: Boolean = true
  )(using
    encoder: JsonEncoder[Req],
    decoder: JsonDecoder[Res]
  ): IO[E, ZIOResponse[Res]] =
    for {
      body <- ZIO.succeed(request.toJson)
      _    <- ZIO.log(body)
      res <- Client
        .request(
          s"$api$endpoint",
          content = if sendContent then Body.fromString(body) else Body.empty,
          headers = headers,
          method = method
        )
        .orElseFail(
          ServerError.InternalServerError(ServerError.InternalServerErrorMessage.ClientError(endpoint))
        )
        .provideSomeLayer(ClientLayer)
        .mapError(_ ⇒ ServerError.InternalServerError(ServerError.InternalServerErrorMessage.ClientError(endpoint)))
      _ <- ZIO.log(res.body.toString)
      data <- res.body.asString
        .mapError { error ⇒
          res.status match
            case Status.Ok      ⇒ ServerError.DecodeError(error.getMessage)
            case Status.Created ⇒ ServerError.DecodeError(error.getMessage)
            case _              ⇒ ServerError.ClientRequestError(res.status, res, endpoint)
        }
        .tapError(e ⇒ ZIO.log(e.getMessage))
      _ <- ZIO.log(data)
      _ <- ZIO.log(data.fromJson.toString)
      decodedRes <- ZIO.fromEither(
        data.fromJson[Res].left.map(ServerError.DecodeError.apply)
      )
    } yield ZIOResponse(data, decodedRes)

object HttpClient:
  private lazy val ClientLayer: TaskLayer[Client] =
    Client.default
