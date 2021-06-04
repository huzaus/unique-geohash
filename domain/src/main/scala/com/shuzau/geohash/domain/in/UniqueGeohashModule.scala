package com.shuzau.geohash.domain.in

import com.shuzau.geohash.domain.entity.{Coordinates, RichCoordinates}
import com.shuzau.geohash.domain.out.GeohashStorage
import com.shuzau.geohash.domain.out.GeohashStorage.StorageService
import zio.{Has, UIO, ULayer, ZIO, ZLayer}

object UniqueGeohashModule {

  type UniqueGeohashService = Has[Service]

  trait Service {
    def put(coordinates: Coordinates): UIO[Unit]
    def get(): UIO[Vector[RichCoordinates]]
  }

  def put(coordinates: Coordinates): ZIO[UniqueGeohashService, Nothing, Unit] =
    ZIO.accessM[UniqueGeohashService](_.get[Service].put(coordinates))

  def get(): ZIO[UniqueGeohashService, Nothing, Vector[RichCoordinates]] =
    ZIO.accessM[UniqueGeohashService](_.get[Service].get())

  val layer: ZLayer[Any, Nothing, Has[Service]] = layer(GeohashStorage.layer)

  def layer(layer: ULayer[StorageService]): ZLayer[Any, Nothing, Has[Service]] =
    layer >>> ZLayer.fromFunction(storage =>
      new Service {
        override def put(coordinates: Coordinates): UIO[Unit] =
          storage.get.put(coordinates)

        override def get(): UIO[Vector[RichCoordinates]] =
          storage.get.get().map { map =>
            val prefixes = ShortestPrefixAlgorithm.uniquePrefixes(map.keySet.map(_.value))
            map.toList
              .lazyZip(prefixes)
              .map { case ((hash, coordinates), prefix) =>
                RichCoordinates(coordinates, hash, prefix)
              }
              .toVector
          }
      }
    )

  val newLayer: ZLayer[StorageService, Nothing, UniqueGeohashService] = ZLayer.fromFunction(storage =>
    new Service {
      override def put(coordinates: Coordinates): UIO[Unit] =
        storage.get.put(coordinates)

      override def get(): UIO[Vector[RichCoordinates]] =
        storage.get.get().map { map =>
          val prefixes = ShortestPrefixAlgorithm.uniquePrefixes(map.keySet.map(_.value))
          map.toList
            .lazyZip(prefixes)
            .map { case ((hash, coordinates), prefix) =>
              RichCoordinates(coordinates, hash, prefix)
            }
            .toVector
        }
    }
  )
}
