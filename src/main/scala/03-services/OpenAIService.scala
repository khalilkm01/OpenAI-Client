package services

import models.common.ServerError
import zio.IO

trait OpenAIService:
  import OpenAIService._

  def createCompletionWithAda(
    createCompletionWithAdaDTO: CreateCompletionWithAdaDTO
  ): IO[ServerError, CreateCompletionWithAdaResponse]

  def createImageWithAda(
    createImageWithAdaDTO: CreateImageWithAdaDTO
  ): IO[ServerError, CreateImageWithAdaResponse]

object OpenAIService:
  sealed trait Request
  final case class CreateCompletionWithAdaDTO(prompt: String) extends Request
  final case class CreateImageWithAdaDTO(prompt: String)      extends Request

  sealed trait Response
  final case class CreateCompletionWithAdaResponse(completions: List[String]) extends Response
  final case class CreateImageWithAdaResponse(urls: List[String])             extends Response
