package models.clients

import models.common.JsonHelper

import org.joda.time.Instant
import zio.json.{ JsonCodec, jsonField }

object OpenAI:
  sealed trait Models extends Product with Serializable
  final case class Choice(
    text: String,
    index: Int,
    logprobs: Option[Int],
    finish_reason: Option[String]
  ) extends Models
  final case class Usage(
    prompt_tokens: Int,
    completion_tokens: Int,
    total_tokens: Int
  ) extends Models
  final case class URL(
    url: String
  ) extends Models
  final case class Embedding(
    @jsonField("object") object_type: String,
    embedding: List[Double],
    index: Int
  ) extends Models

  sealed trait Params      extends Product with Serializable
  sealed trait BodyParams  extends Params
  sealed trait PathParams  extends Params
  sealed trait QueryParams extends Params

  final case class RetrieveModelPathParams(model: String) extends PathParams
  final case class CreateCompletionBodyParams(
    model: String,
    prompt: String = "<|endoftext|>",
    suffix: String,
    maxTokens: Int = 16,
    temperature: Double = 0.3,
    n: Int = 1
  ) extends BodyParams
  final case class CreateEditBodyParams(
    model: String,
    input: String,
    instruction: String,
    maxTokens: Int = 16,
    temperature: Double = 0.3,
    n: Int = 1
  ) extends BodyParams
  final case class CreateImageBodyParams(
    prompt: String,
    n: Int = 1,
    size: String = "1024x1024",
    response_format: String = "url"
  ) extends BodyParams
  final case class CreateEmbeddingsBodyParams(
    model: String,
    input: String
  ) extends BodyParams

  sealed trait Requests                                            extends Product with Serializable
  sealed trait BodyRequest(body: BodyParams)                       extends Requests
  sealed trait PathRequest(path: PathParams)                       extends Requests
  sealed trait QueryRequest(query: QueryParams)                    extends Requests
  sealed trait PathBodyRequest(path: PathParams, body: BodyParams) extends Requests

  final case class ListModelsRequest()                                       extends Requests
  final case class RetrieveModelRequest(path: RetrieveModelPathParams)       extends PathRequest(path)
  final case class CreateCompletionRequest(body: CreateCompletionBodyParams) extends BodyRequest(body)
  final case class CreateEditRequest(body: CreateEditBodyParams)             extends BodyRequest(body)
  final case class CreateImageRequest(body: CreateImageBodyParams)           extends BodyRequest(body)
  final case class CreateEmbeddingsRequest(body: CreateEmbeddingsBodyParams) extends BodyRequest(body)

  sealed trait Responses                                                   extends Product with Serializable
  final case class ListModelsResponse(models: List[RetrieveModelResponse]) extends Responses
  final case class RetrieveModelResponse(
    id: String,
    @jsonField("object") object_type: String,
    owned_by: String
  ) extends Responses
  final case class CreateCompletionResponse(
    id: String,
    @jsonField("object") object_type: String,
    created: Instant,
    model: String,
    choices: List[Choice],
    usage: Usage
  ) extends Responses
  final case class CreateEditResponse(
    @jsonField("object") object_type: String,
    created: Instant,
    choices: List[Choice],
    usage: Usage
  ) extends Responses
  final case class CreateImageResponse(
    created: Instant,
    data: List[URL]
  ) extends Responses
  final case class CreateEmbeddingsResponse(
    @jsonField("object") object_type: String,
    data: List[Embedding]
  ) extends Responses

  object Json:
    import JsonHelper._
    import JsonHelper.given
    
    given JsonCodec[Choice] = deriveCodec
    given JsonCodec[Usage]  = deriveCodec
    given JsonCodec[URL]    = deriveCodec
    given JsonCodec[Embedding] = deriveCodec

    given JsonCodec[RetrieveModelPathParams]    = deriveCodec
    given JsonCodec[CreateCompletionBodyParams] = deriveCodec
    given JsonCodec[CreateEditBodyParams]       = deriveCodec
    given JsonCodec[CreateImageBodyParams]      = deriveCodec
    given JsonCodec[CreateEmbeddingsBodyParams] = deriveCodec

    given JsonCodec[ListModelsRequest]       = deriveCodec
    given JsonCodec[RetrieveModelRequest]    = deriveCodec
    given JsonCodec[CreateCompletionRequest] = deriveCodec
    given JsonCodec[CreateEditRequest]       = deriveCodec
    given JsonCodec[CreateImageRequest]      = deriveCodec
    given JsonCodec[CreateEmbeddingsRequest] = deriveCodec
    
    given JsonCodec[ListModelsResponse]       = deriveCodec
    given JsonCodec[RetrieveModelResponse]    = deriveCodec
    given JsonCodec[CreateCompletionResponse] = deriveCodec
    given JsonCodec[CreateEditResponse]       = deriveCodec
    given JsonCodec[CreateImageResponse]      = deriveCodec
    given JsonCodec[CreateEmbeddingsResponse] = deriveCodec
    
