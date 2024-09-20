package sh.miles.collector.upgrade.level

class SellMultiplierLevel(override val level: Int, override val price: Double, val multiplier: Double) : UpgradeLevel {

    companion object {
        val ZERO = SellMultiplierLevel(0, 0.0, 1.0)
    }

    override fun replacements(currentLevel: Int): MutableMap<String, Any> {
        return mutableMapOf(
            "level" to currentLevel,
            "price" to price,
            "multiplier" to multiplier
        )
    }
}
