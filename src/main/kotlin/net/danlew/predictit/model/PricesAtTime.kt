package net.danlew.predictit.model

import java.time.Instant

data class PricesAtTime(
  val timeStamp: Instant,
  val prices: Map<ContractId, Price>
) : Comparable<PricesAtTime> {

  override fun compareTo(other: PricesAtTime) = timeStamp.compareTo(other.timeStamp)

}
