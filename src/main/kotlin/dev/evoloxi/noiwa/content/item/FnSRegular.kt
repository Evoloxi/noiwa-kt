package dev.evoloxi.noiwa.content.item

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.calculation.Stuff
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World
import java.lang.Thread.sleep

open class FnSRegular(settings: Settings?) : Item(settings) {

    private val burnable: ArrayList<Block> = arrayListOf(
        Blocks.MANGROVE_ROOTS,
        Blocks.SPRUCE_FENCE
    )


    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val cd = context.stack.nbt?.getCompound("Base")?.getFloat("cooldown")?.times(20)?.toInt() ?: 0

        usageExecutor(context, true, 2, Core.RAND.nextInt(7, 10),
            cd, 70, 240)

        return ActionResult.SUCCESS
    }


    fun usageExecutor(context: ItemUsageContext, consumeFlint: Boolean, radius: Int,
                      maxBlocks: Int, cooldown: Int, minSleep: Long, maxSleep: Long): ActionResult {

        val player = context.player!!
        val world = context.world!!
        if (world.isClient) return ActionResult.SUCCESS

        val pos = context.blockPos

        val center = Vec3i(
            pos.x.toDouble() + .5,
            pos.y.toDouble() + .5,
            pos.z.toDouble() + .5
        )

        val block = world.getBlockState(pos).block

        player.itemCooldownManager.set(this, 10)

        if (consumeFlint) {
                if (player.inventory.count(Items.FLINT) >= 1) {

                    val flintStack = player.inventory.getStack(player.inventory.getSlotWithStack(ItemStack(Items.FLINT)))
                    flintStack.decrement(1)

                } else {

                    player.sendMessage(Text.literal("🧻 §cNot enough flint to use this item."), true)
                    player.playSound(SoundEvents.ITEM_SHIELD_BREAK, SoundCategory.PLAYERS, 1f,
                        Core.RAND.nextFloat(1.6f, 2f))
                    return ActionResult.SUCCESS

                }
        }

        world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS,
            1f, Core.RAND.nextFloat(0.8f, 1.5f)
        )
        val ray = player.raycast(5.0, 0.0f, false).pos

        burn3(world, ray)

        if (burnable.contains(block)) {

            player.itemCooldownManager.set(this, cooldown)
            world.playSound(null, pos, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.BLOCKS, 0.4f, 2.73f)
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1f, 1f)

            val blockList: MutableList<BlockPos> = mutableListOf()
            for (x in -radius..radius) {
                for (y in -radius..radius) {
                    for (z in -radius..radius) {
                        val blockPos = pos.add(x, y, z)
                        val blockState = world.getBlockState(blockPos) // distance from center < 4
                        if (blockState.block == Blocks.MANGROVE_ROOTS || blockState.block == Blocks.SPRUCE_FENCE && blockPos.getSquaredDistance(center) < 7) {
                            blockList.add(blockPos)
                        }
                    }
                }
            }

            blockList.sortBy {it.getSquaredDistance(center)}
            // get the first block
            val first = blockList[0]
            val shuffled = blockList.shuffled().toMutableList()
            // find the first block and swap it with the first block
            val firstIndex = shuffled.indexOf(first)
            val temp = shuffled[0]
            shuffled[0] = first
            shuffled[firstIndex] = temp

            // shuffle

            if (blockList.size > maxBlocks) {
                blockList.subList(maxBlocks, blockList.size).clear()
            }

            Thread {
                for (target in shuffled) {

                    if (world.getBlockState(target).isAir) continue

                    while (Core.isPaused) sleep(500)

                    val targetCenter = Vec3d(
                        target.x.toDouble() + .5,
                        target.y.toDouble() + .5,
                        target.z.toDouble() + .5
                    )

                    burn1(world, targetCenter)
                    burn2(world, targetCenter)

                    sleep(Core.RAND.nextLong(minSleep, maxSleep))

                    world.setBlockState(target, Blocks.AIR.defaultState)
                    world.syncWorldEvent(2001, target,
                        Block.getRawIdFromState(Blocks.MANGROVE_ROOTS.defaultState)
                    )
                }
            }.start()

        } else {

            player.sendMessage(Text.literal("🧻 §cNo flammable block found."), true)
            return ActionResult.SUCCESS

        }

        return ActionResult.SUCCESS
    }

    private fun burn1(world: World, pos: Vec3d) {
        Stuff.addParticles(
            world,
            ParticleTypes.SMALL_FLAME,
            9,
            0.01,
            pos.x,
            pos.y,
            pos.z,
            0.3,
            0.3,
            0.3
        )
        Stuff.addParticles(
            world,
            ParticleTypes.FLAME,
            3,
            0.01,
            pos.x,
            pos.y,
            pos.z,
            0.33,
            0.33,
            0.33
        )
    }
    private fun burn2(world: World, pos: Vec3d) {
        Stuff.addParticles(
            world,
            ParticleTypes.SMOKE,
            12,
            0.0,
            pos.x,
            pos.y,
            pos.z,
            0.3,
            0.3,
            0.3
        )
    }
    private fun burn3(world: World, pos: Vec3d) {
        Stuff.addParticles(
            world,
            ParticleTypes.SMALL_FLAME,
            10,
            0.043,
            pos.x,
            pos.y,
            pos.z,
            0.04,
            0.04,
            0.04
        )
        Stuff.addParticles(
            world,
            ParticleTypes.SMOKE,
            2,
            0.004,
            pos.x,
            pos.y,
            pos.z,
            0.01,
            0.01,
            0.01
        )
    }


}
