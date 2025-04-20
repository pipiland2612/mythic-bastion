ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .settings(
    name := "mythic-bastion",
    Compile / run / mainClass := Some("game.GameApp"),
    run / fork := true,
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.16" % Test
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.15.1"
libraryDependencies += "org.scalatestplus" %% "mockito-4-11" % "3.2.16.0" % Test

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "substrate", "config", _*) => MergeStrategy.discard
  case PathList("module-info.class") => MergeStrategy.first
  case PathList("META-INF", "versions", "9", "module-info.class") => MergeStrategy.discard
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}