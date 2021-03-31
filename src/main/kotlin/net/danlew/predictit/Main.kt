package net.danlew.predictit

import net.danlew.predictit.analyzer.StubMarketAnalyzer
import net.danlew.predictit.api.NetworkPredictItApi
import net.danlew.predictit.db.SqlDelightDatabase
import net.danlew.predictit.db.sql.SqlDelightUtils
import net.danlew.predictit.model.Notification
import net.danlew.predictit.notifier.LogNotifier
import net.danlew.predictit.util.Constants
import java.io.File

object Main {
  @JvmStatic
  fun main(args: Array<String>) {
    val predictItApi = NetworkPredictItApi()

    val driver = SqlDelightUtils.createDriver(File(".", Constants.DATA_DIR), Constants.DB_NAME)
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
