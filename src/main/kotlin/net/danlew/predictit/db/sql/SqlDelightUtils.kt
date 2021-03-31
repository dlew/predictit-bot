package net.danlew.predictit.db.sql

import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import net.danlew.predictit.db.SqlDelightDatabase
import net.danlew.predictit.model.ContractId
import net.danlew.predictit.model.MarketId
import net.danlew.predictit.util.Constants
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

internal object SqlDelightUtils {

  /**
   * Creates a [SqlDriver] with the correct Schema in place
   */
  fun createDriver(dataDir: File, dbName: String): SqlDriver {
    check(!dataDir.mkdirs()) { "Could not create data directory $dataDir" }

    // Note: We don't support upgrades because we assume PredictIt's API rarely changes
    // If you change schema, just delete the old DB and recreate
    val dbFile = File(dataDir, dbName)
    val needsCreate = !dbFile.exists()

    val driver = JdbcSqliteDriver("jdbc:sqlite:$dataDir/$dbName")

    if (needsCreate) {
      SqlDatabase.Schema.create(driver)
    }

    return driver
  }

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