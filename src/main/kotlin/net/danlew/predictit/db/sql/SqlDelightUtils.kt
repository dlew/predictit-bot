package net.danlew.predictit.db.sql

import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import net.danlew.predictit.model.ContractId
import net.danlew.predictit.model.MarketId
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

internal object SqlDelightUtils {

  fun createSqlDatabase(driver: SqlDriver): SqlDatabase {
    return SqlDatabase(
      driver = driver,
      contractAdapter = Contract.Adapter(
        idAdapter = ContractIdColumnAdapter,
        marketIdAdapter = MarketIdColumnAdapter
      ),
      marketAdapter = Market.Adapter(
        idAdapter = MarketIdColumnAdapter,
        statusAdapter = EnumColumnAdapter()
      ),
      notificationAdapter = Notification.Adapter(
        marketIdAdapter = MarketIdColumnAdapter,
        typeAdapter = EnumColumnAdapter()
      ),
      priceAdapter = Price.Adapter(
        contractIdAdapter = ContractIdColumnAdapter,
        timeStampAdapter = ZonedDateTimeColumnAdapter,
        priceAdapter = PriceColumnAdapter
      )
    )
  }

  private object MarketIdColumnAdapter : ColumnAdapter<MarketId, Long> {
    override fun decode(databaseValue: Long) = MarketId(databaseValue)
    override fun encode(value: MarketId) = value.id
  }

  private object ContractIdColumnAdapter : ColumnAdapter<ContractId, Long> {
    override fun decode(databaseValue: Long) = ContractId(databaseValue)
    override fun encode(value: ContractId) = value.id
  }

  private object ZonedDateTimeColumnAdapter : ColumnAdapter<ZonedDateTime, Long> {
    private val UTC_ZONE = ZoneId.of("UTC")
    override fun decode(databaseValue: Long) = ZonedDateTime.ofInstant(Instant.ofEpochMilli(databaseValue), UTC_ZONE)
    override fun encode(value: ZonedDateTime) = value.toInstant().toEpochMilli()
  }

  private object PriceColumnAdapter : ColumnAdapter<net.danlew.predictit.model.Price, Long> {
    override fun decode(databaseValue: Long) = net.danlew.predictit.model.Price(databaseValue.toInt())
    override fun encode(value: net.danlew.predictit.model.Price) = value.value.toLong()
  }

}