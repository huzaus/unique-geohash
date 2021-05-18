package com.shuzau.geohash.domain.in

object UniqueGeohashModule {

  trait Service {
    def put(): Unit
    def getAll(): Unit
  }
}
