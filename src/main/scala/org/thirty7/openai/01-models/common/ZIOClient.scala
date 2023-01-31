package org.thirty7.openai
package models.common

object ZIOClient:
  final case class ZIOResponse[Res](originalResAsString: String, res: Res)
  trait Models                                              extends Product with Serializable
  trait Params                                              extends Product with Serializable
  trait BodyParams                                          extends Params
  trait PathParams                                          extends Params
  trait QueryParams                                         extends Params
  trait Requests                                            extends Product with Serializable
  trait BodyRequest(body: BodyParams)                       extends Requests
  trait PathRequest(path: PathParams)                       extends Requests
  trait QueryRequest(query: QueryParams)                    extends Requests
  trait PathBodyRequest(path: PathParams, body: BodyParams) extends Requests
  trait Responses                                           extends Product with Serializable
