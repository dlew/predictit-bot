package net.danlew.predictit.db

import com.squareup.sqldelight.db.SqlDriver
import net.danlew.predictit.db.sql.SqlDatabase
import net.danlew.predictit.db.sql.SqlDelightUtils
import net.danlew.predictit.model.*
import java.time.Clock
import java.time.Duration
import java.time.Instant

class SqlDelightDatabase private constructor(
  private val db: SqlDatabase,
  private val expirationDuration: Duration,
  private val clock: Clock
) : Database {

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

        lastTradePrices.prices.forEach { (contractId, price) ->
          db.priceQueries.insert(
            contractId = contractId,
            timeStamp = lastTradePrices.timeStamp,
            price = price
          )
        }
      }
    }
  }

  override fun insertOrUpdateNotifications(notifications: Set<Notification>) {
    db.transaction {
      val currentNotifications = allNotifications()

      val expiration = Instant.now(clock).plus(expirationDuration)
      notifications.forEach { notification ->
        // We use update or insert because contractId can be null (thus UNIQUE does not work for conflict detection)
        if (notification in currentNotifications) {
          db.notificationQueries.updateExpiration(
            marketId = notification.marketId,
            contractId = notification.contractId,
            type = notification.type,
            expiration = expiration
          )
        } else {
          db.notificationQueries.insert(
            marketId = notification.marketId,
            contractId = notification.contractId,
            type = notification.type,
            expiration = expiration
          )
        }
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

  override fun pricesSince(id: MarketId, since: Instant): List<PricesAtTime> {
    return db.priceQueries.selectPricesSince(marketId = id, timeStamp = since).executeAsList()
      .groupBy { it.timeStamp }
      .map { (timeStamp, values) ->
        PricesAtTime(
          timeStamp = timeStamp,
          prices = values.associate { it.contractId to it.price }
        )
      }
  }

  override fun deleteExpiredNotifications() {
    db.transaction {
      db.notificationQueries.deleteExpired(Instant.now(clock))
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
    fun createDb(driver: SqlDriver, expirationDuration: Duration, clock: Clock): SqlDelightDatabase {
      return SqlDelightDatabase(SqlDelightUtils.createSqlDatabase(driver), expirationDuration, clock)
    }
  }
}