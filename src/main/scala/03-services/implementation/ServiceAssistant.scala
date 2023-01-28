package services.implementation

import models.common.ServerError
//import repositories.quill.QuillContext
import zio.*

import java.sql.SQLException
//import javax.sql.DataSource

trait ServiceAssistant:
//  import QuillContext._
//
//  protected val dataSource: ULayer[DataSource]                                               = dataSourceLayer
//  protected def transact[R <: DataSource, A](op: ZIO[R, Throwable, A]): ZIO[R, Throwable, A] = transaction[R, A](op)

  protected inline def handleError[E <: Throwable, A]: E ⇒ IO[ServerError, A] =
    (error: E) ⇒
      error match
        case error: ServerError ⇒ ZIO.fail(error)
        case error: SQLException ⇒
          ZIO.fail(
            ServerError.InternalServerError(
              ServerError.InternalServerErrorMessage.SQLError(error.getMessage)
            )
          )
        case error ⇒
          ZIO.fail(
            ServerError.InternalServerError(
              ServerError.InternalServerErrorMessage
                .UnknownError(error.getMessage)
            )
          )
