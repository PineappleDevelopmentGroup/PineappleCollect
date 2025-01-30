package sh.miles.collector.util

import org.bukkit.inventory.MenuType

fun rowsToMenuType(rows: Int): MenuType {
    check(rows in 0 .. 6) { "The given row amount is not valid" }
    return when (rows) {
        1 -> {
            MenuType.GENERIC_9X1
        }

        2 -> {
            MenuType.GENERIC_9X2
        }

        3 -> {
            MenuType.GENERIC_9X3
        }

        4 -> {
            MenuType.GENERIC_9X4
        }

        5 -> {
            MenuType.GENERIC_9X5
        }

        6 -> {
            MenuType.GENERIC_9X6
        }

        else -> throw IllegalArgumentException("The given int is not in range!")
    }
}
