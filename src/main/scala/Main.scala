import zio.{ Scope, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer }

object Main extends ZIOAppDefault:

  def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Unit] = Program.make
