package sh.miles.collect.upgrades.type

data class StackSizeUpgrade(val id: String) : Upgrade {
    override fun getKey(): String {
        return id
    }
}
