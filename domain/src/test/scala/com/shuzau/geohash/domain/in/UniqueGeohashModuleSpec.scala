package com.shuzau.geohash.domain.in

import com.shuzau.geohash.domain.entity.{EntityGen, RichCoordinates}
import com.shuzau.geohash.domain.in.UniqueGeohashModule.UniqueGeohashService
import com.softwaremill.diffx.scalatest.DiffMatcher
import org.scalacheck.Gen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import zio.Runtime.default
import zio.{ZEnv, ZIO}

class UniqueGeohashModuleSpec extends AnyFlatSpec with Matchers with ScalaCheckPropertyChecks with DiffMatcher {

  behavior of "UniqueGeohashModule"

  it should "return empty result" in {
    unsafeRun(UniqueGeohashModule.get()) shouldBe empty
  }

  it should "return put and get coordinates" in {
    forAll(EntityGen.coordinates) { coordinates =>
      val scenario = for {
        _     <- UniqueGeohashModule.put(coordinates)
        saved <- UniqueGeohashModule.get()
      } yield saved

      unsafeRun(scenario) shouldBe Vector(
        RichCoordinates(coordinates, coordinates.geohash, coordinates.geohash.value.substring(0, 1))
      )
    }
  }

  it should "return put and get many coordinates" in {
    forAll(Gen.listOf(EntityGen.coordinates)) { list =>
      val scenario = for {
        _     <- ZIO.foreach_(list)(UniqueGeohashModule.put)
        saved <- UniqueGeohashModule.get()
      } yield saved

      unsafeRun(scenario).map(_.coordinates) should contain allElementsOf list
    }
  }

  def unsafeRun[T](scenario: ZIO[ZEnv with UniqueGeohashService, Nothing, T]): T =
    default.unsafeRun(scenario.provideCustomLayer(UniqueGeohashModule.layer))
}
