package org.thirty7.openai
package models.common

final case class ZIOResponse[Res](originalResAsString: String, res: Res)
