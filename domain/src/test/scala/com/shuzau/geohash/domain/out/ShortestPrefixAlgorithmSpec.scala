package com.shuzau.geohash.domain.out

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

  def commonPrefixSizeLength(left: String, right: String): Int = {
    left.lazyZip(right).takeWhile { case (a, b) => a == b }.size
  }

  def uniquePrefixes(strings: SortedSet[String]): List[String] = {
    val origin = strings.toList
    origin
      .lazyZip(
        origin
          .prepended("")
          .appended("")
          .sliding(2)
          .map(list => commonPrefixSizeLength(list.head, list.last))
          .sliding(2)
          .map(_.max + 1)
          .toList
      )
      .map { case (string, length) => string.substring(0, Math.min(length, string.length)) }
  }

}
