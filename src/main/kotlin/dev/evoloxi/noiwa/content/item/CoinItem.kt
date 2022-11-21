package dev.evoloxi.noiwa.content.item

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.content.entities.coin.CoinEntity
import dev.evoloxi.noiwa.content.entities.coin.CoinVariant
import dev.evoloxi.noiwa.foundation.registry.EntityRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import kotlin.math.abs

class CoinItem(settings: Settings?) : Item(settings) {
    override fun use(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack>? {
        if (world.isClient) return TypedActionResult.success(player.getStackInHand(hand))
        val raycast = player.raycast(20.0, 0.0f, false)

        val coinEntity = CoinEntity(EntityRegistry.COIN, world)

        val spread = 1.0
        val maxSpread = 15000.0

        val random = Core.RAND.nextInt(0, 350)
        val randomInt = abs(random)

        var v: CoinVariant
        random.let {
            v = when {
                it <= 30 -> CoinVariant.SMALL_1
                it <= 60 -> CoinVariant.SMALL_0
                it <= 90 -> CoinVariant.SMALL_2
                it <= 120 -> CoinVariant.NORMAL_0
                it <= 150 -> CoinVariant.NORMAL_1
                it <= 180 -> CoinVariant.NORMAL_2
                it <= 210 -> CoinVariant.LARGE_0
                it <= 240 -> CoinVariant.LARGE_1
                it <= 270 -> CoinVariant.LARGE_2
                it <= 300 -> CoinVariant.LARGE_3
                else -> CoinVariant.LARGE_3
            }
            coinEntity.variant = v
        }
        coinEntity.value = randomInt

        val pos = raycast.pos
        coinEntity.setPos(pos.x, pos.y + 0.081, pos.z)

        coinEntity.yaw = player.yaw

        world.spawnEntity(coinEntity)

        return TypedActionResult.success(player.getStackInHand(hand))
    }
}
