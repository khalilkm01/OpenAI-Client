package org.thirty7.openai

import zio.*

object Main extends ZIOAppDefault:

  def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Unit] = Program.make
