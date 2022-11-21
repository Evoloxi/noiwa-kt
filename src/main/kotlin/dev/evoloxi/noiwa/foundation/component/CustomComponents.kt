package dev.evoloxi.noiwa.foundation.component

import dev.evoloxi.noiwa.Core
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier

class CustomComponents : EntityComponentInitializer {
    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {

        // Add the component to every living entity
        registry.registerFor(PlayerEntity::class.java, COINS) { provider: PlayerEntity -> EntityCoinComponent(provider) }
        registry.registerFor(LivingEntity::class.java, STATIC) { provider: LivingEntity -> StaticComponents(provider) }
        registry.registerFor(LivingEntity::class.java, STATS) { provider: LivingEntity -> EntityStatComponents(provider) }

    }

    companion object {
        val COINS: ComponentKey<EntityCoinComponent> =
            ComponentRegistry.getOrCreate(Identifier(Core.MOD_ID, "coins"), EntityCoinComponent::class.java)
        val STATIC: ComponentKey<StaticComponents> =
            ComponentRegistry.getOrCreate(Identifier(Core.MOD_ID, "static"), StaticComponents::class.java)
        val STATS: ComponentKey<EntityStatComponents> =
            ComponentRegistry.getOrCreate(Identifier(Core.MOD_ID, "stats"), EntityStatComponents::class.java)
    }
}
