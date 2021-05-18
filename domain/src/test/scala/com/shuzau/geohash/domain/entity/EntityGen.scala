package com.shuzau.geohash.domain.entity

import org.scalacheck.Gen

trait EntityGen {
  val latitude: Gen[Latitude] = Gen.chooseNum(-90, 90).map(Latitude(_))

  val longitude: Gen[Longitude] = Gen.chooseNum(-180, 180).map(Longitude(_))

  val coordinates: Gen[Coordinates] = for {
    latitude  <- latitude
    longitude <- longitude
  } yield Coordinates(latitude, longitude)
}
object EntityGen extends EntityGen
