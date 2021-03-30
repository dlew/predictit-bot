package net.danlew.predictit.model

/**
 * Represents something we want to tell users.
 */
data class Notification(
  val market: Market,
  val type: Type
) {

  enum class Type {
    NEW_MARKET
  }

  fun format(): String {
    return "${type.name} for ${market.name}"
  }

}
