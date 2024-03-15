package sh.miles.collect.pdc

import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType
import sh.miles.collect.util.Position
import java.util.StringJoiner
import java.util.UUID

class PositionDataType: PersistentDataType<String, Position> {

    override fun getPrimitiveType(): Class<String> {
        return String::class.java
    }

    override fun getComplexType(): Class<Position> {
        return Position::class.java
    }

    override fun fromPrimitive(primitive: String, context: PersistentDataAdapterContext): Position {
        val split = primitive.split(",")
        return Position(UUID.fromString(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]))
    }

    override fun toPrimitive(position: Position, context: PersistentDataAdapterContext): String {
        val builder = StringJoiner(",")
        builder.add(position.uuid.toString())
        builder.add(position.x.toString())
        builder.add(position.y.toString())
        builder.add(position.z.toString())
        return builder.toString()
    }
}
