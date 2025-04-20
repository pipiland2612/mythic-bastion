ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.5"
ThisBuild / assembly / assemblyJarName := s"${name.value}-${version.value}.jar"

lazy val root = (project in file("."))
  .settings(
    name := "mythic-bastion",
    Compile / run / mainClass := Some("game.GameApp"),
    run / fork := true,
    assembly / mainClass := Some("game.GameApp"),  // Explicit main class for assembly
    assembly / test := {},  // Skip tests during assembly
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.16" % Test
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.15.1"
libraryDependencies += "org.scalatestplus" %% "mockito-4-11" % "3.2.16.0" % Test

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case PathList("module-info.class") => MergeStrategy.discard
  case x => MergeStrategy.first
}