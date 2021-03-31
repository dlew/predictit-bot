package net.danlew.predictit.model

import java.time.Instant

/**
 * Represents the price of a [Contract] at a particular time.
 */
data class PriceAtTime(
  val timeStamp: Instant,
  val price: Price
) : Comparable<PriceAtTime> {

  override fun compareTo(other: PriceAtTime) = timeStamp.compareTo(other.timeStamp)

}
