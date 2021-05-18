package com.shuzau.geohash.domain.entity

import ch.hsr.geohash.GeoHash

final case class Coordinates(latitude: Latitude, longitude: Longitude) {
  def geohash: Geohash = Geohash(GeoHash.withCharacterPrecision(latitude.value, longitude.value,12).toBase32)
}
