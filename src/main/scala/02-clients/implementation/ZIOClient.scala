package clients.implementation

import models.common.{ ServerError, ZIOResponse }
import zhttp.http.{ Headers, HttpData, Method, Status }
import zio.{ IO, ULayer, ZIO }
import zhttp.service.{ ChannelFactory, Client, EventLoopGroup }
import zio.json.*

trait ZIOClient(root: String, headers: Headers):
  import ZIOClient.ClientLayer

  private val api: String = root;

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
      res <- Client
        .request(
          s"${api}${endpoint}",
          content = if sendContent then HttpData.fromString(request.toJson) else HttpData.empty,
          headers = headers,
          method = method
        )
        .orElseFail(
          ServerError.InternalServerError(ServerError.InternalServerErrorMessage.ClientError(endpoint))
        )
        .provideSomeLayer(ClientLayer)
      data <- res.bodyAsString.mapError { error ⇒
        res.status match
          case Status.Ok      ⇒ ServerError.DecodeError(error.getMessage)
          case Status.Created ⇒ ServerError.DecodeError(error.getMessage)
          case _              ⇒ ServerError.ClientRequestError(res.status, res, endpoint)
      }

      decodedRes <- ZIO.fromEither(
        data.fromJson[Res].left.map(ServerError.DecodeError.apply)
      )
    } yield ZIOResponse(data, decodedRes)
    
  


object ZIOClient:
  private lazy val ClientLayer: ULayer[ChannelFactory with EventLoopGroup] =
    ChannelFactory.auto ++ EventLoopGroup.auto()
