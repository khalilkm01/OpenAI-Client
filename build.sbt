ThisBuild / scalaVersion     := "3.2.0"
ThisBuild / version          := "1.0.0"
ThisBuild / organization     := "org.thirty7.openai"
ThisBuild / organizationName := "thirty7"
ThisBuild / name             := "OpenAI"

enablePlugins(
  JavaAppPackaging,
  DockerPlugin
)

Compile / mainClass  := Some("org.thirty7.openai.Main")
Docker / packageName := "khalilkm01/OpenAI-Microservice"

libraryDependencies ++= {
  val nscalaTimeVersion = "2.32.0"
  val zioVersion        = "2.0.6"
  val zioJsonVersion    = "0.4.2"
  val zioConfigVersion  = "3.0.7"
  val zioHttpVersion    = "0.0.4"
  val zioLoggingVersion = "2.1.8"

  Seq(
    "com.github.nscala-time" %% "nscala-time"              % nscalaTimeVersion,
    "dev.zio"                %% "zio"                      % zioVersion,
    "dev.zio"                %% "zio-json"                 % zioJsonVersion,
    "dev.zio"                %% "zio-config"               % zioConfigVersion,
    "dev.zio"                %% "zio-config-typesafe"      % zioConfigVersion,
    "dev.zio"                %% "zio-config-magnolia"      % zioConfigVersion,
    "dev.zio"                %% "zio-http"                 % zioHttpVersion,
    "dev.zio"                %% "zio-logging-slf4j-bridge" % zioLoggingVersion
    //      "io.getquill"            %% "quill-jdbc-zio"           % "4.6.0"
  )
}
