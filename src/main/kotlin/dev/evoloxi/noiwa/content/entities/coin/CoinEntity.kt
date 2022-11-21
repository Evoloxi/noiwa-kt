package dev.evoloxi.noiwa.content.entities.coin

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.foundation.registry.ParticleRegistry
import dev.evoloxi.noiwa.foundation.registry.SoundRegistry
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.world.World
import software.bernie.geckolib3.core.IAnimatable
import software.bernie.geckolib3.core.manager.AnimationData
import software.bernie.geckolib3.core.manager.AnimationFactory
import software.bernie.geckolib3.util.GeckoLibUtil

class CoinEntity(entityType: EntityType<out LivingEntity?>?, world: World?) : LivingEntity(entityType, world), IAnimatable {
    private val factory = GeckoLibUtil.createFactory(this)
    override fun getArmorItems(): Iterable<ItemStack> {
        return setOf(ItemStack.EMPTY)
    }

    override fun getEquippedStack(slot: EquipmentSlot): ItemStack {
        return ItemStack.EMPTY
    }

    override fun equipStack(slot: EquipmentSlot, stack: ItemStack) {}

	override fun isPushable() = false

    override fun getMainArm() = null

    override fun pushAway(entity: Entity) {}
    override fun registerControllers(animationData: AnimationData) {}
    override fun getFactory(): AnimationFactory {
        return factory
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putInt("Variant", typeVariant)
        nbt.putInt("Value", dataValue)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        dataTracker.set(DATA_ID_TYPE_VARIANT, nbt.getInt("Variant"))
        dataTracker.set(DATA_VALUE, nbt.getInt("Value"))
    }

    // cancel all baseDamage
    override fun damage(source: DamageSource, amount: Float): Boolean {
        if (amount > 1000000) this.remove(RemovalReason.KILLED)
        return false
    }

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0)
        dataTracker.startTracking(DATA_VALUE, 0)
    }

    var variant: CoinVariant
        get() = CoinVariant.byId(typeVariant and 255)
        set(variant) {
            dataTracker.set(DATA_ID_TYPE_VARIANT, variant.id and 255)
        }
    var value: Int
        get() = dataValue
        set(value) {
            dataTracker.set(DATA_VALUE, value)
        }

    private val typeVariant: Int
        get() = dataTracker.get(DATA_ID_TYPE_VARIANT)

    private val dataValue: Int
        get() = dataTracker.get(DATA_VALUE)

    override fun tick() {
	    super.tick()
        if (world.getClosestPlayer(this, 24.0) != null) {
        this.tickMovement()
        if ((world.time + Core.RAND.nextInt(0, 15)) % 16 == 0L) {
            if (world.isClient) {
                world.addParticle(
                    ParticleRegistry.SPARKLE,
                    this.x + Core.RAND.nextFloat(-0.3f, 0.3f),
                    this.y + Core.RAND.nextFloat(-0.1f, 0.1f) + 0.5f,
                    this.z + Core.RAND.nextFloat(-0.3f, 0.3f),
                    0.0,
                    0.0,
                    0.0
                )
            }
        }
        }
    }

    override fun getFallSounds(): FallSounds {
        return FallSounds(SoundRegistry.COIN_PICKUP_1, SoundRegistry.COIN_PICKUP_2)
    }

    companion object {
        private val DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(CoinEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
        private val DATA_VALUE =
            DataTracker.registerData(CoinEntity::class.java, TrackedDataHandlerRegistry.INTEGER)

        fun setAttributes(): DefaultAttributeContainer.Builder {
            return createLivingAttributes()
        }
    }
}
