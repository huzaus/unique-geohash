import sbt._

object Dependencies {

  lazy val scalaTest  = "org.scalatest"          %% "scalatest"       % "3.2.6"   % Test
  lazy val scalaCheck = "org.scalatestplus"      %% "scalacheck-1-14" % "3.1.0.0" % Test
  lazy val diffx      = "com.softwaremill.diffx" %% "diffx-scalatest" % "0.3.29"  % Test

  lazy val cats    = "org.typelevel" %% "cats-core" % "2.4.2"
  lazy val newType = "io.estatico"   %% "newtype"   % "0.4.4"
  lazy val zio     = "dev.zio"       %% "zio"       % "1.0.7"

  lazy val geohash = "ch.hsr" % "geohash" % "1.4.0"

  lazy val testDependencies: Seq[ModuleID] = Seq(scalaTest, scalaCheck, diffx)

  lazy val commonDependencies: Seq[ModuleID] = Seq(cats, zio, newType)
}
