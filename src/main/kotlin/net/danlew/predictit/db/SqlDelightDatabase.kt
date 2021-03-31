package net.danlew.predictit.db

import com.squareup.sqldelight.db.SqlDriver
import net.danlew.predictit.db.sql.SqlDatabase
import net.danlew.predictit.db.sql.SqlDelightUtils
import net.danlew.predictit.model.*

class SqlDelightDatabase private constructor(private val db: SqlDatabase) : Database {

  override fun insertOrUpdate(markets: Set<MarketWithPrices>) {
    db.transaction {
      markets.forEach { (market, lastTradePrices) ->
        db.marketQueries.insertOrReplace(
          id = market.id,
          name = market.name,
          image = market.image,
          url = market.url,
          status = market.status
        )

        market.contracts.forEach { contract ->
          db.contractQueries.insertOrReplace(
            id = contract.id,
            marketId = market.id,
            name = contract.name,
            image = contract.image
          )
        }

        lastTradePrices.forEach { (contractId, priceAtTime) ->
          db.priceQueries.insert(
            contractId = contractId,
            timeStamp = priceAtTime.timeStamp,
            price = priceAtTime.price
          )
        }
      }
    }
  }

  override fun insertOrUpdateNotifications(notifications: Set<Notification>) {
    db.transaction {
      notifications.forEach { notification ->
        db.notificationQueries.insert(
          marketId = notification.marketId,
          contractId = notification.contractId,
          type = notification.type
        )
      }
    }
  }

  override fun allMarkets(): Set<Market> {
    return db.transactionWithResult {
      val allMarkets = db.marketQueries.selectAll().executeAsList()
      val allContracts = db.contractQueries.selectAll().executeAsList()

      val contractsByMarketId: Map<MarketId, List<Contract>> = allContracts
        .groupBy { it.marketId }
        .mapValues { (_, value) -> value.map { Contract(it.id, it.name, it.image) } }

      return@transactionWithResult allMarkets.map {
        Market(
          id = it.id,
          name = it.name,
          image = it.image,
          url = it.url,
          status = it.status,
          contracts = contractsByMarketId[it.id]!!
        )
      }.toSet()
    }
  }

  override fun allNotifications(): Set<Notification> {
    return db.transactionWithResult {
      return@transactionWithResult db.notificationQueries.selectAll().executeAsList()
        .map {
          Notification(
            marketId = it.marketId,
            contractId = it.contractId,
            type = it.type
          )
        }
        .toSet()
    }
  }

  override fun priceHistory(marketId: MarketId): Map<ContractId, List<PriceAtTime>> {
    return db.transactionWithResult {
      return@transactionWithResult db.priceQueries.selectAllForMarket(marketId).executeAsList()
        .groupBy { it.contractId }
        .mapValues { (_, value) -> value.map { PriceAtTime(it.timeStamp, it.price) } }
    }
  }

  companion object {
    fun createDb(driver: SqlDriver): SqlDelightDatabase {
      return SqlDelightDatabase(SqlDelightUtils.createSqlDatabase(driver))
    }
  }
}