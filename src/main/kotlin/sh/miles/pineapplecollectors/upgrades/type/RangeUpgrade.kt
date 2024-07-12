package sh.miles.pineapplecollectors.upgrades.type

data class RangeUpgrade(val id: String) : Upgrade {
    override fun getKey(): String {
        return id
    }
}
