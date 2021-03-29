package net.danlew.predictit.model

/**
 * Represents something we want to tell users.
 */
data class Notification(
  val marketId: MarketId,
  val type: Type
) {

  enum class Type {
    NEW_MARKET
  }

}
