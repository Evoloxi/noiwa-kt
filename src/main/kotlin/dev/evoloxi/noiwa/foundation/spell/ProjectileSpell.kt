package dev.evoloxi.noiwa.foundation.spell

import dev.evoloxi.noiwa.foundation.registry.EntityRegistry
import dev.evoloxi.noiwa.foundation.registry.ItemRegistry.SPARK_BOLT_TIMER
import dev.evoloxi.noiwa.foundation.spell.entity.*
import net.minecraft.entity.LivingEntity
import net.minecraft.world.World
import java.util.function.BiFunction

enum class ProjectileSpell(
	override val spellType: SpellType,
    override val manaDrain: Int,
    private val damageCollection: DamageCollection,
    val speedMin: Int,
    val speedMax: Int,
    override val castDelay: Int,
    override val rechargeTime: Int,
    override val spread: Float,
    override val spreadModifier: Float,
    private val criticalChance: Float,
    override val uses: Int,
    private val entitySummoner: BiFunction<World, LivingEntity, SpellEntityBase>,
    override val castNumber: Int = 0
) : ISpellEnum {
    SPARK_BOLT(
        SpellType.PROJECTILE_MAGICAL,
        5,
        DamageCollection(3.0f, 0f, 2f, 0f, 0f),
        750,
        850,
        1,
        0,
        0f,
        -1f,
        0.05f,
        -1,
        BiFunction<World, LivingEntity, SpellEntityBase> { world: World, playerEntity: LivingEntity ->
            SparkBoltSpellEntity(
                EntityRegistry.SPELL_SPARK_BOLT, playerEntity, world
            )
        }
    ),
    SPARK_BOLT_TIMER(
        SpellType.PROJECTILE_MAGICAL,
        10,
        DamageCollection(3.0f, 0f, 2f, 0f, 0f),
        750,
        850,
        1,
        0,
        0F,
        -1F,
        0.05f,
        -1,
        BiFunction { world: World, playerEntity: LivingEntity ->
            SparkBoltSpellEntity(
                EntityRegistry.SPELL_SPARK_BOLT,
                playerEntity,
                world
            ).hasTimer()
        },
        1
    ),
    SPARK_BOLT_TRIGGER(
        SpellType.PROJECTILE_MAGICAL,
        10,
        DamageCollection(3.0f, 0f, 2f, 0f, 0f),
        750,
        850,
        1,
        0,
        0F,
        -1F,
        0.05f,
        -1,
        BiFunction { world: World, playerEntity: LivingEntity ->
            SparkBoltSpellEntity(
                EntityRegistry.SPELL_SPARK_BOLT,
                playerEntity,
                world
            ).hasTrigger()
        },
        1
    ),
    SPARK_BOLT_TRIGGER_DOUBLE(
        SpellType.PROJECTILE_MAGICAL,
        15,
        DamageCollection(3.75f, 10f, 12f, 0f, 0f),
        650,
        750,
        1,
        0,
        0f,
        -1f,
        0.05f,
        -1,
        BiFunction { world: World, playerEntity: LivingEntity ->
            SparkBoltDoubleTriggerSpellEntity(
                EntityRegistry.SPELL_SPARK_BOLT_DOUBLE_TRIGGER,
                playerEntity,
                world
            ).hasTrigger()
        },
        2
    ),
    ENERGY_SPHERE(
        SpellType.PROJECTILE_MAGICAL,
        20,
        DamageCollection(0f, 0f, 2f, 10f, 0f),
        400,
        500,
        3,
        0,
        0.6f,
        0f,
        0f,
        -1,
        BiFunction<World, LivingEntity, SpellEntityBase> { world: World, playerEntity: LivingEntity ->
            EnergySphereSpellEntity(
                EntityRegistry.SPELL_ENERGY_SPHERE, playerEntity, world
            )
        }
    ),
    ENERGY_SPHERE_TIMER(
        SpellType.PROJECTILE_MAGICAL,
        50,
        DamageCollection(0f, 0f, 2f, 10f, 0f),
        400,
        500,
        3,
        0,
        0.6f,
        0f,
        0f,
        -1,
        BiFunction { world: World, playerEntity: LivingEntity ->
            EnergySphereSpellEntity(
                EntityRegistry.SPELL_ENERGY_SPHERE,
                playerEntity,
                world
            ).hasTimer()
        },
        1
    ),
    BOMB(
        SpellType.PROJECTILE_MAGICAL,
        25,
        DamageCollection(0f, 0f, 2f, 10f, 0f),
        0,
        0,
        33,
        0,
        0f,
        0f,
        0f,
        3,
        BiFunction<World, LivingEntity, SpellEntityBase> { world: World, playerEntity: LivingEntity ->
            BombSpellEntity(
                EntityRegistry.SPELL_BOMB, playerEntity, world
            )
        }
    );

    fun damageCollection(): DamageCollection {
        return damageCollection
    }

    fun entitySummoner(): BiFunction<World, LivingEntity, SpellEntityBase> {
        return entitySummoner
    }
}
