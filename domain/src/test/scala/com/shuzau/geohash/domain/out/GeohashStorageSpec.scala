package com.shuzau.geohash.domain.out

import com.shuzau.geohash.domain.entity.EntityGen
import com.shuzau.geohash.domain.entity.Geohash.geohashOrdering
import com.shuzau.geohash.domain.out.GeohashStorage.StorageService
import com.softwaremill.diffx.scalatest.DiffMatcher
import org.scalacheck.Gen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import zio.Runtime.default
import zio.{ZEnv, ZIO}

import scala.collection.SortedMap

class GeohashStorageSpec extends AnyFlatSpec with Matchers with ScalaCheckPropertyChecks with DiffMatcher {

  behavior of "GeohashStorage"

  it should "return empty set for empty storage" in {
    unsafeRun(GeohashStorage.get()) shouldBe empty
  }

  it should "return put and get coordinates" in {
    forAll(EntityGen.coordinates) { coordinates =>
      val scenario = for {
        _ <- GeohashStorage.put(coordinates)
        saved <- GeohashStorage.get()
      } yield saved

      unsafeRun(scenario) shouldBe SortedMap(coordinates.geohash -> coordinates)
    }
  }

  it should "return put and get many coordinates" in {
    forAll(Gen.listOf(EntityGen.coordinates)){ list =>
      val scenario = for {
        _ <- ZIO.foreach_(list)(GeohashStorage.put)
        saved <- GeohashStorage.get()
      } yield saved

      unsafeRun(scenario).values should contain allElementsOf list
    }
  }

  def unsafeRun[T](scenario: ZIO[ZEnv with StorageService, Nothing, T]): T = {
    default.unsafeRun(scenario.provideCustomLayer(GeohashStorage.layer))
  }
}
