package sh.miles.collector.upgrade.level

data class AutoSellLevel(override val level: Int, override val price: Double, val ticks: Int) : UpgradeLevel {

    companion object {
        val ZERO = AutoSellLevel(0, 0.0, 0)
    }

    override fun replacements(currentLevel: Int): MutableMap<String, Any> {
        return mutableMapOf(
            "level" to currentLevel,
            "price" to price,
            "ticks" to ticks
        )
    }
}
