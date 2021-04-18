package net.danlew.predictit.db

import net.danlew.predictit.model.*
import java.time.Instant

interface Database {

  fun insertOrUpdate(markets: Set<MarketWithPrices>)

  fun insertOrUpdateNotifications(notifications: Set<Notification>)

  fun getMarket(id: MarketId): Market?

  fun getContract(id: ContractId): Contract?

  fun allMarkets(status: MarketStatus): Set<Market>

  fun allNotifications(): Set<Notification>

  fun pricesSince(id: MarketId, since: Instant): List<PricesAtTime>

  fun deleteExpiredNotifications()

}