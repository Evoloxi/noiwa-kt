package dev.evoloxi.noiwa.foundation.handler

import net.minecraft.block.Blocks
import net.minecraft.entity.TntEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos

object PlayerActionHandler {
    fun breakBlock(
        player: PlayerEntity,
        blockPos: BlockPos
    ): Boolean {
        val world = player.world
        if (world.getBlockState(blockPos).block === Blocks.TNT) {
            player.sendMessage(Text.literal("You can't break TNT here"), true)
            //world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
            val tntEntity = TntEntity(world, blockPos.x + 0.5, blockPos.y + 0.5, blockPos.z + 0.5, player)
            tntEntity.fuse = 140
            tntEntity.velocity = player.rotationVector.add(0.0, 1.0, 0.0)
            tntEntity.customName = Text.literal("TNT")
            tntEntity.isCustomNameVisible = true
            world.spawnEntity(tntEntity)
            return false
        }
        return true
    }
    fun rightClickBlock(
        player: PlayerEntity,
        blockPos: BlockPos
    ): Boolean {
        val world = player.world
        if (world.getBlockState(blockPos).block === Blocks.TNT) {
            player.sendMessage(Text.literal("You can't right-click TNT here"), true)
            return false
        }
        return true
    }
}
