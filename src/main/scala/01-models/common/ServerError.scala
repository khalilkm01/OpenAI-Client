package models.common

import zhttp.http.{ Path, Response, Status }

import java.util.UUID

sealed trait ServerError extends Throwable

object ServerError:
  final case class DecodeError(str: String) extends ServerError

  final case class InternalServerError(error: InternalServerErrorMessage) extends ServerError

  final case class NotFoundError(path: Path) extends ServerError

  final case class ServiceError(error: ServiceErrorMessage) extends ServerError

  final case class ClientRequestError(status: Status, req: Response, endpoint: String) extends ServerError

  enum InternalServerErrorMessage(msg: String) extends ServerError:
    case IllegalArgumentMessage extends InternalServerErrorMessage("Illegal Argument Exception Occurred")
    case SQLError(msg: String)
        extends InternalServerErrorMessage(
          s"Internal Server Error Occurred with Exception: $msg"
        )
    case UnknownError(msg: String) extends InternalServerErrorMessage(msg)
    case ClientError(url: String)  extends InternalServerErrorMessage(s"Client could not be requested: $url")
    case ClientErrorWithMsg(url: String, msg: String)
        extends InternalServerErrorMessage(s"Client could not be requested: $url. Failed with message: $msg")

  enum ServiceErrorMessage(msg: String):
    case IdNotFound(id: UUID) extends ServiceErrorMessage(s"Model with ID: $id, does not exist")
    case UnauthorizedAccessError(id: UUID)
        extends ServiceErrorMessage(
          s"Unauthorised Access attempted on model with ID: $id"
        )
    case ExistingModelIdFound(id: UUID) extends ServiceErrorMessage(s"Model with ID: $id")
    case IllegalServiceCall             extends ServiceErrorMessage("Illegal Service Call Occurred")
