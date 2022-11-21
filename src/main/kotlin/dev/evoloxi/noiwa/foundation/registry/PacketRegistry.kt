package dev.evoloxi.noiwa.foundation.registry

import dev.evoloxi.noiwa.Core.Companion.id
import dev.evoloxi.noiwa.foundation.handler.packet.ElixirHitPacket
import dev.evoloxi.noiwa.foundation.handler.packet.IndicatorPacket
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking


object PacketRegistry {
    val INDICATOR_PACKET_ID = id("indicator_packet")
	val ELIXIR_HIT_PACKET_ID = id("elixir_hit_packet")

    @JvmStatic
    fun registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(INDICATOR_PACKET_ID, IndicatorPacket::receive)
	    ClientPlayNetworking.registerGlobalReceiver(ELIXIR_HIT_PACKET_ID, ElixirHitPacket::receive)
    }

}
