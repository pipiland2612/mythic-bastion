ThisBuild / scalaVersion := "3.3.5"
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / resolvers ++= Seq(
  "Maven Central" at "https://repo1.maven.org/maven2/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

// Explicit assembly settings
lazy val assemblySettings = Seq(
  assembly / assemblyJarName := s"${name.value}-${version.value}.jar",
  assembly / mainClass := Some("game.GameApp"),
  assembly / test := {},  // Skip tests during assembly
  assembly / assemblyMergeStrategy := {
    case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
    case PathList("META-INF", "versions", "9", _*) => MergeStrategy.discard
    case PathList("module-info.class") => MergeStrategy.discard
    case x => MergeStrategy.first
  }
)

Compile / resourceDirectories += baseDirectory.value / "src/main/resources"

lazy val root = (project in file("."))
  .settings(
    name := "mythic-bastion",

    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.16" % Test,
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.15.1",
      "org.scalatestplus" %% "mockito-4-11" % "3.2.16.0" % Test
    ),

    javacOptions ++= Seq("-source", "17", "-target", "17"),
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xfatal-warnings"
    )
  )
  .settings(assemblySettings)