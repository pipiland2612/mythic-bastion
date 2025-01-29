ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .settings(
    name := "mythic-bastion"
  )

libraryDependencies += "org.scalafx" % "scalafx_3" % "22.0.0-R33"