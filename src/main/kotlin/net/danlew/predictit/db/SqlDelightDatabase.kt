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
          shortName = market.shortName,
          image = market.image,
          url = market.url,
          status = market.status
        )

        market.contracts.forEach { contract ->
          db.contractQueries.insertOrReplace(
            id = contract.id,
            marketId = market.id,
            name = contract.name,
            shortName = contract.shortName,
            image = contract.image,
            status = contract.status
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

  override fun getMarket(id: MarketId): Market? {
    return db.transactionWithResult {
      val market = db.marketQueries.getById(id).executeAsOneOrNull() ?: return@transactionWithResult null
      val contracts = db.contractQueries.getForMarket(id).executeAsList().map { it.toContract() }
      return@transactionWithResult market.toMarket(contracts)
    }
  }

  override fun getContract(id: ContractId): Contract? {
    return db.contractQueries.getById(id).executeAsOneOrNull()?.toContract()
  }

  override fun allMarkets(status: MarketStatus): Set<Market> {
    return db.transactionWithResult {
      val allMarkets = db.marketQueries.allMarkets(status).executeAsList()
      val allContracts = db.contractQueries.selectAll().executeAsList()

      val contractsByMarketId: Map<MarketId, List<Contract>> = allContracts
        .groupBy { it.marketId }
        .mapValues { (_, values) ->
          values.map { it.toContract() }
        }

      return@transactionWithResult allMarkets.map {
        it.toMarket(contractsByMarketId[it.id]!!)
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

  private fun net.danlew.predictit.db.sql.Contract.toContract() = Contract(
    id = id,
    name = name,
    shortName = shortName,
    image = image,
    status = status
  )

  private fun net.danlew.predictit.db.sql.Market.toMarket(contracts: List<Contract>) = Market(
    id = id,
    name = name,
    shortName = shortName,
    image = image,
    url = url,
    status = status,
    contracts = contracts
  )

  companion object {
    fun createDb(driver: SqlDriver): SqlDelightDatabase {
      return SqlDelightDatabase(SqlDelightUtils.createSqlDatabase(driver))
    }
  }
}