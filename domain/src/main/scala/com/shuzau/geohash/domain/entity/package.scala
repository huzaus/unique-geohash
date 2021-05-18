package com.shuzau.geohash.domain

import cats.Order
import io.estatico.newtype.macros.newtype

import scala.language.implicitConversions

package object entity {

  @newtype case class Latitude(value: Double)
  @newtype case class Longitude(value: Double)

  @newtype case class Geohash(value: String)

  object Geohash {
    implicit val geohashOrder: Order[Geohash] = Order.by[Geohash, String](_.value)
    implicit val geohashOrdering: Ordering[Geohash] = geohashOrder.toOrdering
  }
}
