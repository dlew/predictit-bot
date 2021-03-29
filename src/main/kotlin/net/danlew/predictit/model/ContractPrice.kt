package net.danlew.predictit.model

import java.math.BigDecimal
import java.time.ZonedDateTime

/**
 * Represents the price of a [Contract] at a particular time.
 */
data class ContractPrice(
  val timeStamp: ZonedDateTime,
  val price: BigDecimal
)
