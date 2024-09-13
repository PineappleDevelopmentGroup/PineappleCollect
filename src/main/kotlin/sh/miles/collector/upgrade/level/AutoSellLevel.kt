package sh.miles.collector.upgrade.level

data class AutoSellLevel(override val level: Int, override val price: Int, val ticks: Int) : UpgradeLevel
