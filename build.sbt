ThisBuild / scalaVersion     := "3.1.3"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "org.thirty7.openAI"
ThisBuild / organizationName := "thirty7"

lazy val root = (project in file("."))
  .settings(
    name := "OpenAI",
    libraryDependencies ++= Seq(
      "dev.zio"                %% "zio"                      % "2.0.2",
      "dev.zio"                %% "zio-json"                 % "0.3.0-RC10",
      "dev.zio"                %% "zio-config"               % "3.0.2",
      "dev.zio"                %% "zio-config-typesafe"      % "3.0.2",
      "dev.zio"                %% "zio-config-magnolia"      % "3.0.2",
      "io.d11"                 %% "zhttp"                    % "2.0.0-RC10",
      "com.github.nscala-time" %% "nscala-time"              % "2.32.0",
      "dev.zio"                %% "zio-logging-slf4j-bridge" % "2.1.0"
//      "io.getquill"            %% "quill-jdbc-zio"           % "4.6.0"
    )
  )
