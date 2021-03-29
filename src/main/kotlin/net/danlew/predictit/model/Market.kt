package net.danlew.predictit.model

/**
 * A single market, with multiple [Contract]s within.
 */
data class Market(
  val id: MarketId,
  val name: String,
  val url: String,
  val status: MarketStatus,
  val contracts: List<Contract>
)
