package org.thirty7.openai
package clients.implementation

import config.Config.OpenAIConfig
import clients.OpenAIClient
import models.common.ServerError
import models.clients.OpenAI
import models.common.ServerError
import zio.{ IO, RLayer, ZLayer }
import zio.json.{ JsonDecoder, JsonEncoder }
import zio.http.model.{ Header, Headers, MediaType, Method }

final case class OpenAIClientLive(config: OpenAIConfig)
    extends OpenAIClient
    with HttpClient(
      config.root,
      Headers
        .contentType("application/json")
        .withAuthorization(s"Bearer ${config.apiKey}")
    ):
  import OpenAI.Json.given

  private def performAndPerformRequest[Request, Response, Error >: ServerError](
    endpoint: String,
    request: Request,
    method: Method,
    sendContent: Boolean
  )(using
    reqEncoder: JsonEncoder[Request],
    resDecoder: JsonDecoder[Response]
  ): IO[Error, Response] =
    performRequest[Request, Response, Error](
      endpoint,
      request,
      method,
      sendContent = sendContent
    ).map(_.res)

  override def listModels: IO[ServerError, OpenAI.ListModelsResponse] =
    performAndPerformRequest[OpenAI.ListModelsRequest, OpenAI.ListModelsResponse, ServerError](
      endpoint = "/v1/models",
      request = OpenAI.ListModelsRequest(),
      method = Method.GET,
      sendContent = false
    )

  override def retrieveModel(
    retrieveModelRequest: OpenAI.RetrieveModelRequest
  ): IO[ServerError, OpenAI.RetrieveModelResponse] =
    performAndPerformRequest[OpenAI.RetrieveModelRequest, OpenAI.RetrieveModelResponse, ServerError](
      endpoint = s"/v1/models/${retrieveModelRequest.path.model}",
      request = retrieveModelRequest,
      method = Method.GET,
      sendContent = false
    )

  override def createCompletion(
    createCompletionRequest: OpenAI.CreateCompletionRequest
  ): IO[ServerError, OpenAI.CreateCompletionResponse] =
    performAndPerformRequest[OpenAI.CreateCompletionBodyParams, OpenAI.CreateCompletionResponse, ServerError](
      endpoint = "/v1/completions",
      request = createCompletionRequest.body,
      method = Method.POST,
      sendContent = true
    )

  override def createEdit(
    createEditRequest: OpenAI.CreateEditRequest
  ): IO[ServerError, OpenAI.CreateEditResponse] =
    performAndPerformRequest[OpenAI.CreateEditBodyParams, OpenAI.CreateEditResponse, ServerError](
      endpoint = "/v1/edits",
      request = createEditRequest.body,
      method = Method.POST,
      sendContent = true
    )

  override def createEmbeddings(
    createEmbeddingsRequest: OpenAI.CreateEmbeddingsRequest
  ): IO[ServerError, OpenAI.CreateEmbeddingsResponse] =
    performAndPerformRequest[OpenAI.CreateEmbeddingsBodyParams, OpenAI.CreateEmbeddingsResponse, ServerError](
      endpoint = "/v1/embeddings",
      request = createEmbeddingsRequest.body,
      method = Method.POST,
      sendContent = true
    )

  override def createImage(
    createImageRequest: OpenAI.CreateImageRequest
  ): IO[ServerError, OpenAI.CreateImageResponse] =
    performAndPerformRequest[OpenAI.CreateImageBodyParams, OpenAI.CreateImageResponse, ServerError](
      endpoint = "/v1/images/generations",
      request = createImageRequest.body,
      method = Method.POST,
      sendContent = true
    )

object OpenAIClientLive:
  lazy val layer: RLayer[OpenAIConfig, OpenAIClient] =
    ZLayer.fromFunction(OpenAIClientLive(_))
