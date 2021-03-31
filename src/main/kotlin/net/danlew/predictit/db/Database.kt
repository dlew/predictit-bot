package net.danlew.predictit.db

import net.danlew.predictit.model.MarketWithPrices
import net.danlew.predictit.model.*

interface Database {

  fun insertOrUpdate(markets: Set<MarketWithPrices>)

  fun insertOrUpdateNotifications(notifications: Set<Notification>)

  fun allMarkets(status: MarketStatus): Set<Market>

  fun allNotifications(): Set<Notification>

  fun priceHistory(marketId: MarketId): Map<ContractId, List<PriceAtTime>>

}