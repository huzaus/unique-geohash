name := "Unique Geohash"

ThisBuild / scalaVersion := "2.13.5"
ThisBuild / version := "0.0.1"
ThisBuild / organization := "com.shuzau.geohash"
ThisBuild / organizationName := "Siarhei Huzau"

ThisBuild / scalacOptions := Seq(
  "-unchecked",
  "-deprecation",
  "-encoding",
  "utf8",
  "-feature",
  "literal-types",
  "-Xfatal-warnings",
  "-Ymacro-annotations"
)

ThisBuild / libraryDependencies += compilerPlugin(
  "org.typelevel" %% "kind-projector" % "0.11.3" cross CrossVersion.full
)

lazy val `reality-venue` = (project in file("."))
  .settings(Compile / discoveredMainClasses ++= (`app` / Compile / discoveredMainClasses).value)
  .dependsOn(`app`)
  .aggregate(`domain`, `app`)

lazy val `domain` = project in file("domain")

lazy val `app` = (project in file("app"))
  .settings()
  .dependsOn(`domain` % "compile->compile;test->test")
