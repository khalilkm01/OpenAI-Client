package models.common

import zhttp.http.Response

final case class ZIOResponse[Res](originalResAsString: String, res: Res)
