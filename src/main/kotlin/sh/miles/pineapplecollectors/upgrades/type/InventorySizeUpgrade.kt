package sh.miles.pineapplecollectors.upgrades.type

data class InventorySizeUpgrade(val id: String) : Upgrade {
    override fun getKey(): String {
        return id
    }
}
