package dev.evoloxi.noiwa.foundation.handler.packet

import dev.evoloxi.noiwa.calculation.Stuff
import dev.evoloxi.noiwa.foundation.Extensions.toVec3f
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.Vec3d
import org.quiltmc.qsl.networking.api.PacketSender

object ElixirHitPacket {
    fun receive(
        client: MinecraftClient,
        handler: ClientPlayNetworkHandler,
        buf: PacketByteBuf,
        responseSender: PacketSender
    ) {
	    val origin = Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble())
	    val radius = buf.readDouble()
	    val startColor = buf.readInt().toVec3f()
	    val endColor = buf.readInt().toVec3f()
        client.execute {
            // read buffer containing crit, amountToDamage and victim position and victim width and height
            Stuff.elixirHit(origin, radius, startColor, endColor)
        }
    }
}
