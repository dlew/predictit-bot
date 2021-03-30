package net.danlew.predictit

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import net.danlew.predictit.api.NetworkPredictItApi
import net.danlew.predictit.db.SqlDelightDatabase

object Main {
  @JvmStatic
  fun main(args: Array<String>) {
    val predictItApi = NetworkPredictItApi()

    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    val db = SqlDelightDatabase.createDb(driver)

    val controller = Controller(
      api = predictItApi,
      db = db,
      analyzers = emptySet(),
      notifiers = emptySet()
    )

    controller.run()
  }
}
