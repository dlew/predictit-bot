package net.danlew.predictit.model

data class MarketWithPrices(
  val market: Market,
  val pricesAtTime: PricesAtTime
) {

  init {
    require(market.contracts.map { it.id }.toSet() == pricesAtTime.prices.keys) {
      "Price required for every contract"
    }
  }

}
