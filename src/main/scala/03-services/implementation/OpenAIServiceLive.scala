package services.implementation

import clients.OpenAIClient
import models.common.ServerError
import models.clients.OpenAI
import services.OpenAIService

import zio.{ IO, RLayer, Task, ZIO, ZLayer }

final case class OpenAIServiceLive(client: OpenAIClient) extends OpenAIService with ServiceAssistant:

  override def createCompletionWithAda(
    createCompletionWithAdaDTO: OpenAIService.CreateCompletionWithAdaDTO
  ): IO[ServerError, OpenAIService.CreateCompletionWithAdaResponse] =
    for {
      models <- client.listModels
      ada <- ZIO
        .fromOption(models.models.find(_.id.contains("ada")))
        .mapError(_ ⇒
          ServerError.InternalServerError(
            ServerError.InternalServerErrorMessage.UnknownError(
              "Ada model not found"
            )
          )
        )
      requestBody <- ZIO.succeed(
        OpenAI.CreateCompletionBodyParams(
          model = ada.id,
          prompt = createCompletionWithAdaDTO.prompt,
          suffix = ""
        )
      )
      request <- ZIO.succeed(
        OpenAI.CreateCompletionRequest(
          requestBody
        )
      )
      response <- client.createCompletion(request)
      choices = response.choices
    } yield OpenAIService.CreateCompletionWithAdaResponse(choices.map(_.text))

  override def createImageWithAda(
    createImageWithAdaDTO: OpenAIService.CreateImageWithAdaDTO
  ): IO[ServerError, OpenAIService.CreateImageWithAdaResponse] =
    for {
      requestBody <- ZIO.succeed(
        OpenAI.CreateImageBodyParams(
          prompt = createImageWithAdaDTO.prompt
        )
      )
      request <- ZIO.succeed(
        OpenAI.CreateImageRequest(
          requestBody
        )
      )
      response <- client
        .createImage(request)
      data = response.data
    } yield OpenAIService.CreateImageWithAdaResponse(data.map(_.url))

object OpenAIServiceLive:
  lazy val layer: RLayer[OpenAIClient, OpenAIService] =
    ZLayer.fromFunction(OpenAIServiceLive(_))
