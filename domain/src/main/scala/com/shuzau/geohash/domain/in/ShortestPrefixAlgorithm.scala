package com.shuzau.geohash.domain.in

import scala.collection.SortedSet

private[domain] object ShortestPrefixAlgorithm {
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
