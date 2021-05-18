package com.shuzau.geohash.domain.in

import com.shuzau.geohash.domain.in.ShortestPrefixAlgorithm.uniquePrefixes
import com.softwaremill.diffx.scalatest.DiffMatcher
import org.scalacheck.{Gen, Shrink}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.collection.SortedSet

class ShortestPrefixAlgorithmSpec extends AnyFlatSpec with Matchers with ScalaCheckPropertyChecks with DiffMatcher {

  behavior of "ShortestPrefixAlgorithm"

  val char: Gen[Char] = Gen.oneOf('a', 'b', 'c')

  val string: Gen[String] = for {
    chars <- Gen.listOfN(10, char)
  } yield chars.mkString


  it should "find unique prefix" in {
    forAll(Gen.containerOfN[SortedSet, String](10, string)) { strings =>
      val prefixes: List[String] = uniquePrefixes(strings)
      prefixes.foreach { prefix =>
        strings.filter(_.startsWith(prefix)).toList should have size 1
      }
    }
  }

  private implicit def noShrink[T]: Shrink[T] = Shrink.shrinkAny
}
