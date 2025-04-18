ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .settings(
    name := "mythic-bastion",
    run / fork := true
  )

libraryDependencies += "org.scalafx" % "scalafx_3" % "22.0.0-R33"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.16" % Test
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.15.1"
libraryDependencies += "org.scalatestplus" %% "mockito-4-11" % "3.2.16.0" % Test