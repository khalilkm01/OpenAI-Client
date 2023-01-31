package org.thirty7.openai
package clients

import models.clients.OpenAI._
import models.common.ServerError
import zio.IO

trait OpenAIClient:
  def listModels: IO[ServerError, ListModelsResponse]

  def retrieveModel(retrieveModelRequest: RetrieveModelRequest): IO[ServerError, RetrieveModelResponse]

  def createCompletion(createCompletionRequest: CreateCompletionRequest): IO[ServerError, CreateCompletionResponse]

  def createEdit(createEditRequest: CreateEditRequest): IO[ServerError, CreateEditResponse]

  def createImage(createImageRequest: CreateImageRequest): IO[ServerError, CreateImageResponse]

  def createEmbeddings(createEmbeddingsRequest: CreateEmbeddingsRequest): IO[ServerError, CreateEmbeddingsResponse]
