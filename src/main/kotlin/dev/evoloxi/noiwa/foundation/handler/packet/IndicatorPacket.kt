package dev.evoloxi.noiwa.foundation.handler.packet

import dev.evoloxi.noiwa.calculation.Stuff
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.Vec3d
import org.quiltmc.qsl.networking.api.PacketSender


object IndicatorPacket {
    fun receive(
        client: MinecraftClient,
        handler: ClientPlayNetworkHandler,
        buf: PacketByteBuf,
        responseSender: PacketSender
    ) {
	    val indicatorType = buf.readByte()
        val damageType = buf.readByte()
        val amountToDamage = buf.readLong()
        val victimX = buf.readDouble()
        val victimY = buf.readDouble()
        val victimZ = buf.readDouble()
        val victimWidth = buf.readFloat()
        val victimHeight = buf.readFloat()

        client.execute {
            // read buffer containing crit, amountToDamage and victim position and victim width and height
            Stuff.spawnIndicator(Vec3d(victimX, victimY, victimZ), indicatorType, victimWidth, victimHeight, amountToDamage, damageType)
        }
    }
}
