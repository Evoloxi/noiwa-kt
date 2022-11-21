package dev.evoloxi.noiwa.foundation.spell.entity

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.foundation.Extensions.squareXZ
import dev.evoloxi.noiwa.foundation.spell.ISpellEnum
import dev.evoloxi.noiwa.foundation.spell.cast.CastHelper
import dev.evoloxi.noiwa.foundation.spell.cast.TriggeredSpellPoolVisitor
import dev.evoloxi.noiwa.foundation.util.MethHelper
import dev.evoloxi.noiwa.foundation.util.NBTHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.*

abstract class SpellEntityBase(entityTypeIn: EntityType<out SpellEntityBase?>?, worldIn: World) :
    ProjectileEntity(entityTypeIn, worldIn) {
    protected var casterUUID: UUID? = null
    protected var inGround = false
    protected var age: Int = 0
	var castList: List<ISpellEnum>
    protected var hasTrigger: Boolean
    protected var hasTimer: Boolean
	protected val waterDrag: Float = 0.6f
	protected abstract val gravity: Float
	protected val airDrag: Float = 0.99f
	protected open val ageToCast: Int = 13
	val caster: LivingEntity? = if (casterUUID != null) world.getPlayerByUuid(casterUUID) as LivingEntity else null
	
    init {
        this.age = 0
        castList = ArrayList()
        hasTrigger = false
        hasTimer = false
    }

    override fun readNbt(compound: NbtCompound) {
        casterUUID = compound.getUuid("Caster")
        inGround = compound.getBoolean("inGround")
        this.age = compound.getInt("Age")
        hasTrigger = NBTHelper.getBoolean(compound, "HasTrigger").orElse(true)
        hasTimer = NBTHelper.getBoolean(compound, "HasTimer").orElse(true)
        castList = NBTHelper.spellListFromNBT(compound.getList("CastList", NBTHelper.NBTTypes.STRING.ordinal))
    }

    override fun writeNbt(compound: NbtCompound): NbtCompound {
	    compound.putUuid("Caster", casterUUID)
        compound.putBoolean("inGround", inGround)
        compound.putInt("Age", this.age)
        compound.putBoolean("HasTrigger", hasTrigger)
        compound.putBoolean("HasTimer", hasTimer)
        if (castList?.isNotEmpty() == true && (hasTrigger || hasTimer)) {
            compound.put("CastList", NBTHelper.spellNBTFromList(castList!!))
        }
        return compound
    }

    abstract fun shoot(shooter: Entity, pitch: Float, yaw: Float, velocity: Float, inaccuracy: Float)
    override fun tick() {
        super.tick()
        ++this.age
        if (this.age > maxAge) {
            onAgeExpire()
        } else if (hasTimer && this.age > ageToCast) {
            castSpellTimer()
            hasTimer = false
        }
        generateParticles()
    }

    protected fun castSpellTimer() {
        if (world.isClient) return
        castSpell(velocity)
    }

    protected fun castSpellTrigger(traceResult: HitResult) {
        if (world.isClient()) return
        if (castList.isEmpty()) return
        var reflectedVec: Vec3d
        if (traceResult.type == HitResult.Type.ENTITY) {
            reflectedVec = velocity.multiply(-1.0)
            val box = (traceResult as EntityHitResult).entity.boundingBox.expand(0.3)
            val hitPosOptional = box.raycast(pos, pos.add(velocity))
            if (hitPosOptional.isPresent) {
                val hitPos = hitPosOptional.get()
	            val axis: Direction.Axis = if (hitPos.getX() == box.minX || hitPos.getX() == box.maxX) {
	                Direction.Axis.X
	            } else if (hitPos.getY() == box.minY || hitPos.getY() == box.maxY) {
	                Direction.Axis.Y
	            } else {
	                Direction.Axis.Z
	            }
                reflectedVec = MethHelper.reflectByAxis(velocity, axis)
            }
        } else if (traceResult.type == HitResult.Type.BLOCK) {
            reflectedVec = MethHelper.reflectByAxis(velocity, (traceResult as BlockHitResult).side.axis)
        } else {
            return
        }
        castSpell(reflectedVec)
    }

    private fun castSpell(castVec: Vec3d) {
        val visitor = TriggeredSpellPoolVisitor(castList)
        val castResult = CastHelper.processSpellList(visitor)
        for ((spell, value) in castResult.spell2TriggeredSpellList) {
            val spellEntity = spell.entitySummoner().apply(world, caster ?: return)
            spellEntity.castList = value
            var speed = 0f
            val speedMin = spell.speedMin
            val speedMax = spell.speedMax
            if (speedMin < speedMax) speed = (Random().nextInt(speedMax - speedMin) + speedMin).toFloat()
            speed += 200f
            speed /= 600f
	        spellEntity.setPosition(this.x, this.y, this.z)
	        spellEntity.setVelocity(castVec.getX(), castVec.getY(), castVec.getZ(), speed, 1.0f)
            world.spawnEntity(spellEntity)
        }
    }

    fun hasTrigger(): SpellEntityBase {
        hasTrigger = true
        return this
    }

    fun hasTimer(): SpellEntityBase {
        hasTimer = true
        return this
    }

    protected open val maxAge: Int = 13

    protected open fun onAgeExpire() {
        discard()
    }


    protected open fun generateParticles() {
        val nextPos = pos.add(velocity)
        if (submergedInWater) {
            for (j in 0..3) {
                world.addParticle(
                    ParticleTypes.BUBBLE,
                    nextPos.x - velocity.x * 0.25,
                    nextPos.y - velocity.y * 0.25,
                    nextPos.z - velocity.z * 0.25,
	                velocity.x,
	                velocity.y,
	                velocity.z
                )
            }
        }
    }
	
	override fun setVelocity(x: Double, y: Double, z: Double, velocity: Float, inaccuracy: Float) {
		val vec3d = Vec3d(x, y, z).normalize().add(
			Core.RAND.nextGaussian() * 0.0075 * inaccuracy.toDouble(),
			Core.RAND.nextGaussian() * 0.0075 * inaccuracy.toDouble(),
			Core.RAND.nextGaussian() * 0.0075 * inaccuracy.toDouble()
		).multiply(velocity.toDouble())
		this.velocity = vec3d
		val f = MathHelper.sqrt(vec3d.squareXZ())
		yaw = (MathHelper.atan2(vec3d.x, vec3d.z) * Core.TO_DEG).toFloat()
		pitch = (MathHelper.atan2(vec3d.y, f.toDouble()) * Core.TO_DEG).toFloat()
		prevYaw = yaw
		prevPitch = pitch
	}
	
    protected open fun onHit(traceResult: HitResult) {
        if (hasTrigger || hasTimer) {
            castSpellTrigger(traceResult)
            hasTrigger = false
            hasTimer = false
        }
    }
}
