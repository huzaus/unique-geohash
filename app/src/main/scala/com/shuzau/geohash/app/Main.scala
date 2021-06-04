package com.shuzau.geohash.app

import com.shuzau.geohash.app.Util.process
import com.shuzau.geohash.domain.entity.{Coordinates, Latitude, Longitude, RichCoordinates}
import com.shuzau.geohash.domain.in.UniqueGeohashModule
import zio.Runtime.default
import zio.ZIO

import java.io.File
import scala.io.Source
import scala.util.Using

object Main extends App {
  if (args.length == 0) {
    println("No file given to process.")
    System.exit(1)
  } else {
    val file = args(0)

    println(s"Reading $file file")
    val content = for {
      file    <- Util.checkFile(file)
      content <- Util.readFile(file)
    } yield content

    content.map { lines =>
      val input = lines
        .drop(1) // drop header
        .map(Util.parse)
        .flatMap(_.toOption)

      val output = process(input)
      println("lat,lng,geohash,uniq")
      output.foreach(coordinates =>
        println(
          s"${coordinates.coordinates.latitude},${coordinates.coordinates.longitude},${coordinates.geohash.value},${coordinates.prefix}"
        )
      )
    }

    if (content.isLeft) {
      content.left.map(println(_))
      System.exit(1)
    }
  }
}

object Util {
  def checkFile(path: String): Either[String, File] = {
    val file = new File(path)
    if (file.exists && file.isFile) {
      Right(file)
    } else {
      Left(s"$path is not a file or it doesn't exist.")
    }
  }

  def readFile(file: File): Either[String, List[String]] =
    Using(Source.fromFile(file))(_.getLines().toList).toEither.left.map(_.getMessage)

  def parse(line: String): Either[String, Coordinates] = {
    val entries = line.split(",")
    if (entries.size == 2) {
      for {
        latitude  <- parseDouble(entries.head)
        longitude <- parseDouble(entries.last)
      } yield Coordinates(Latitude(latitude), Longitude(longitude))
    } else {
      Left(s"Wrong $line format, expected 'latitude,longitude'")
    }
  }

  def parseDouble(double: String): Either[String, Double] =
    double.toDoubleOption.toRight(s"Couldn't parse $double")

  def process(input: List[Coordinates]): Vector[RichCoordinates] = {
    val task = for {
      _      <- ZIO.foreach_(input)(UniqueGeohashModule.put)
      output <- UniqueGeohashModule.get()
    } yield output

    default.unsafeRun(task.provideLayer(UniqueGeohashModule.layer))
  }
}
