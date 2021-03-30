package net.danlew.predictit

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import net.danlew.predictit.analyzer.StubMarketAnalyzer
import net.danlew.predictit.api.NetworkPredictItApi
import net.danlew.predictit.db.SqlDelightDatabase
import net.danlew.predictit.model.Notification
import net.danlew.predictit.notifier.LogNotifier

object Main {
  @JvmStatic
  fun main(args: Array<String>) {
    val predictItApi = NetworkPredictItApi()

    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    val db = SqlDelightDatabase.createDb(driver)

    val controller = Controller(
      api = predictItApi,
      db = db,
      analyzers = setOf(StubMarketAnalyzer(Notification.Type.NEW_MARKET)),
      notifiers = setOf(LogNotifier)
    )

    controller.run()
  }
}
