package com.shuzau.geohash.domain.out

import com.shuzau.geohash.domain.entity.{Coordinates, Geohash}
import zio.{Has, Ref, UIO, ULayer, ZIO, ZLayer}

import scala.collection.immutable.SortedMap

object GeohashStorage {

  type StorageService = Has[Service]

  trait Service {
    def put(coordinates: Coordinates): UIO[Unit]
    def get(): UIO[SortedMap[Geohash, Coordinates]]
  }

  def put(coordinates: Coordinates): ZIO[StorageService, Nothing, Unit] =
    ZIO.accessM[StorageService](_.get[Service].put(coordinates))

  def get(): ZIO[StorageService, Nothing, SortedMap[Geohash, Coordinates]] =
    ZIO.accessM[StorageService](_.get[Service].get())

  val layer: ULayer[StorageService] =
    Ref.make(SortedMap[Geohash, Coordinates]()).toLayer >>> ZLayer.fromFunction(ref =>
      new Service {
        override def put(coordinates: Coordinates): UIO[Unit]    =
          ref.get.update(map => map + (coordinates.geohash -> coordinates))

        override def get(): UIO[SortedMap[Geohash, Coordinates]] =
          ref.get.get
      }
    )

}
