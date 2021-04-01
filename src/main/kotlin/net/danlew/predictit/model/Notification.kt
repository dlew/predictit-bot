package net.danlew.predictit.model

/**
 * Represents something we want to tell users.
 */
data class Notification(
  val marketId: MarketId,
  val contractId: ContractId? = null,
  val type: Type
) {

  enum class Type {
    MARKET_OPENED,
    MARKET_CLOSED,
    CONTRACT_ADDED,
    HIGH_VOLATILITY,
    NEGATIVE_RISK,
  }

}
