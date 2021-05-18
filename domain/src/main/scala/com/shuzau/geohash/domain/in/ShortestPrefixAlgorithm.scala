package com.shuzau.geohash.domain.in

import scala.collection.SortedSet

private[domain] object ShortestPrefixAlgorithm {
  def commonPrefixSizeLength(left: String, right: String): Int =
    left.lazyZip(right).takeWhile { case (a, b) => a == b }.size

  def uniquePrefixes(strings: SortedSet[String]): List[String] = {
    val origin = strings.toList                                      // "aaa" , "abb", "abc"
    origin
      .lazyZip(
        origin
          .prepended("")                                             // "", "aaa" , "abb", "abc"
          .appended("")                                              // "", "aaa" , "abb", "abc"
          .sliding(2)                                                // ("", "aaa"), ("aaa", "abb"), ("abb", "abc"), ("abc", "")
          .map(list => commonPrefixSizeLength(list.head, list.last)) // "", "a", "ab", ""
          .sliding(2)                                                // ("", "a"), ("a", "ab"), ("ab", "")
          .map(_.max + 1)                                            // ("a",  "aa") -> "aa", ("ab", "abb") -> "abb", ("abc", "a")  -> "abc"
          .toList
      )
      .map { case (string, length) => string.substring(0, Math.min(length, string.length)) }
  }
}
