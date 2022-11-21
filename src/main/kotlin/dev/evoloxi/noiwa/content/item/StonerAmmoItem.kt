package dev.evoloxi.noiwa.content.item

import dev.evoloxi.noiwa.calculation.Stuff
import net.minecraft.entity.ItemEntity
import net.minecraft.item.Item
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents

open class StonerAmmoItem(settings: Settings) : ItemWithRarity(settings) {
	override var rarity: ERarity = ERarity.COMMON

    override fun onItemEntityDestroyed(entity: ItemEntity) {
        val world = entity.world
        val pos = entity.pos
        // create an explosion


        // spawn particles
        if (world is ServerWorld) {
            Stuff.addParticles(world, ParticleTypes.DRIPPING_LAVA, 20, 2.0, pos.x, pos.y + 0.5, pos.z,
                0.5, 0.5, 0.5)
            Stuff.addParticles(world, ParticleTypes.SMOKE, 6, 0.1, pos.x, pos.y + 0.25, pos.z,
                0.1, 0.5, 0.1)
            Stuff.addParticles(world, ParticleTypes.SMALL_FLAME, 4, 0.013, pos.x, pos.y + 0.25, pos.z,
                0.1, 0.5, 0.1)
            Stuff.addParticles(world, ParticleTypes.FLAME, 2, 0.01, pos.x, pos.y + 0.25, pos.z,
                0.1, 0.5, 0.1)
            Stuff.addParticles(world, ParticleTypes.EXPLOSION, 1, 0.001, pos.x, pos.y + 0.35, pos.z,
                0.1, 0.5, 0.1)
            world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1f, 1f)

        }
        super.onItemEntityDestroyed(entity)
    }
}
