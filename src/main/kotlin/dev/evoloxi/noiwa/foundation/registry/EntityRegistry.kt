package dev.evoloxi.noiwa.foundation.registry

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.content.entities.LavaBallEntity
import dev.evoloxi.noiwa.content.entities.LavaBallRenderer
import dev.evoloxi.noiwa.content.entities.coin.CoinEntity
import dev.evoloxi.noiwa.content.entities.coin.CoinRenderer
import dev.evoloxi.noiwa.content.entities.rockProjectile.RockEntity
import dev.evoloxi.noiwa.content.entities.rockProjectile.RockProjectileRenderer
import dev.evoloxi.noiwa.foundation.spell.entity.BombSpellEntity
import dev.evoloxi.noiwa.foundation.spell.entity.EnergySphereSpellEntity
import dev.evoloxi.noiwa.foundation.spell.entity.SparkBoltDoubleTriggerSpellEntity
import dev.evoloxi.noiwa.foundation.spell.entity.SparkBoltSpellEntity
import dev.evoloxi.noiwa.foundation.spell.entity.renderer.InvisibleEntityRenderer
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.FlyingItemEntityRenderer
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import dev.evoloxi.noiwa.content.entities.ElixirEntity
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object EntityRegistry {
	// entities
	private val ENTITIES: LinkedHashMap<EntityType<*>, Identifier> = LinkedHashMap()
	
	@JvmField
	val LAVA_BALL: EntityType<LavaBallEntity> = create(
		"lava_ball",
		FabricEntityTypeBuilder.create(SpawnGroup.MISC, ::LavaBallEntity)
			.dimensions(EntityDimensions.fixed(0.25f, 0.25f))
			.build()
	)
	
	val COIN: EntityType<CoinEntity> = create(
		"coin",
		FabricEntityTypeBuilder.create(SpawnGroup.MISC, ::CoinEntity)
			.dimensions(EntityDimensions.fixed(0.25f, 0.25f))
			.build()
	)
	
	val ROCK: EntityType<RockEntity> = create(
		"rock",
		FabricEntityTypeBuilder.create(SpawnGroup.MISC, ::RockEntity)
			.dimensions(EntityDimensions.fixed(1.75f, 1.75f))
			.build()
	)
	
	val ELIXIR: EntityType<ElixirEntity> = create(
		"elixir",
		FabricEntityTypeBuilder.create(SpawnGroup.MISC, ::ElixirEntity)
			.dimensions(EntityDimensions.fixed(0.25f, 0.25f))
			.build()
	)
	
	@JvmField
	val SPELL_SPARK_BOLT: EntityType<SparkBoltSpellEntity> = create(
		"spark_spell_bolt",
		FabricEntityTypeBuilder.create(SpawnGroup.MISC, ::SparkBoltSpellEntity)
			.dimensions(EntityDimensions.fixed(0.25f, 0.25f))
			.build()
	)
	
	@JvmField
	val SPELL_SPARK_BOLT_DOUBLE_TRIGGER: EntityType<SparkBoltDoubleTriggerSpellEntity> = create(
		"spark_spell_bolt_double_trigger",
		FabricEntityTypeBuilder.create(SpawnGroup.MISC, ::SparkBoltDoubleTriggerSpellEntity)
			.dimensions(EntityDimensions.fixed(0.25f, 0.25f))
			.build()
	)
	@JvmField
	val SPELL_ENERGY_SPHERE: EntityType<EnergySphereSpellEntity> = create(
		"energy_sphere",
		FabricEntityTypeBuilder.create(SpawnGroup.MISC, ::EnergySphereSpellEntity)
			.dimensions(EntityDimensions.fixed(0.25f, 0.25f))
			.build()
	)
	@JvmField
	val SPELL_BOMB: EntityType<BombSpellEntity> = create(
		"bomb",
		FabricEntityTypeBuilder.create(SpawnGroup.MISC, ::BombSpellEntity)
			.dimensions(EntityDimensions.fixed(0.25f, 0.25f))
			.build()
	)
	
	fun register() {
		ENTITIES.keys.forEach {
			Registry.register(
				Registry.ENTITY_TYPE,
				ENTITIES[it],
				it
			)
		}
	}
	
	private fun <T : Entity> create(name: String, type: EntityType<T>): EntityType<T> {
		ENTITIES[type] = Identifier(Core.MOD_ID, name)
		return type
	}
	
    fun registerClient() {
	    EntityRendererRegistry.register(
		    COIN
	    ) { ctx: EntityRendererFactory.Context -> CoinRenderer(ctx) }
	
	    EntityRendererRegistry.register(
		    ROCK
	    ) { ctx: EntityRendererFactory.Context -> RockProjectileRenderer(ctx) }
	    
	    EntityRendererRegistry.register(
		    ELIXIR
	    ) { ctx: EntityRendererFactory.Context -> FlyingItemEntityRenderer(ctx) }
	    
	    EntityRendererRegistry.register(
		    LAVA_BALL
	    ) { ctx: EntityRendererFactory.Context -> LavaBallRenderer(ctx) }
	    
	    EntityRendererRegistry.register(
		    SPELL_SPARK_BOLT
	    ) { ctx: EntityRendererFactory.Context -> InvisibleEntityRenderer(ctx) }
	    
	    EntityRendererRegistry.register(
		    SPELL_SPARK_BOLT_DOUBLE_TRIGGER
	    ) { ctx: EntityRendererFactory.Context -> InvisibleEntityRenderer(ctx) }
	    
	    EntityRendererRegistry.register( SPELL_ENERGY_SPHERE ) { ctx: EntityRendererFactory.Context -> InvisibleEntityRenderer(ctx) }
	    
	    EntityRendererRegistry.register( SPELL_BOMB ) { ctx: EntityRendererFactory.Context -> InvisibleEntityRenderer(ctx) }
    }
}
