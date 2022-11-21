package dev.evoloxi.noiwa.content.entities

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.calculation.Stuff
import dev.evoloxi.noiwa.content.particle.generic.GenericParticleEffect
import dev.evoloxi.noiwa.foundation.Extensions.reflect
import dev.evoloxi.noiwa.foundation.Extensions.toVec3f
import dev.evoloxi.noiwa.foundation.registry.EntityRegistry
import dev.evoloxi.noiwa.foundation.registry.SoundRegistry
import dev.evoloxi.noiwa.foundation.spell.entity.SpellEntityBase
import net.minecraft.block.FluidBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.MovementType
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundCategory
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3f
import net.minecraft.world.RaycastContext
import net.minecraft.world.World

class LavaBallEntity(entityType: EntityType<LavaBallEntity>, world: World) :  ProjectileEntity(entityType, world) {

	constructor(world: World, owner: LivingEntity) : this(EntityRegistry.LAVA_BALL, world) {
		this.owner = owner
		this.setPos(owner.x, owner.eyePos.y - 0.1, owner.z)
		this.updatePosition(this.x, this.y, this.z)
		this.velocity = owner.rotationVector.multiply(1.5)
	}


	companion object {
		private val DATA_BOUNCES =
			DataTracker.registerData(LavaBallEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
		private val DATA_LAST_BOUNCE =
			DataTracker.registerData(LavaBallEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
		private val DATA_MAX_BOUNCES =
			DataTracker.registerData(LavaBallEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
	}

	private var hitCount: Int
		get() = dataTracker.get(DATA_BOUNCES)
		set(value) {
			dataTracker.set(DATA_BOUNCES, value)
		}
	private var lastHit: Int
		get() = dataTracker.get(DATA_LAST_BOUNCE)
		set(value) {
			dataTracker.set(DATA_LAST_BOUNCE, value)
		}
	var maxBounces: Int
		get() = dataTracker.get(DATA_MAX_BOUNCES)
		set(value) {
			dataTracker.set(DATA_MAX_BOUNCES, value)
		}

	init {
		hitCount = 0
		lastHit = 0
	}
	
	override fun onBlockHit(blockHitResult: BlockHitResult?) {
		if (hitCount++ >= maxBounces) discard()

		val side = blockHitResult!!.side

		val surfaceVector = Vec3d(side.unitVector)

		val reflectionVector = velocity.reflect(surfaceVector).multiply(0.8, 0.9 - 0.1 * hitCount, 0.8)

		this.lastHit = world.time.toInt()

		if (world.isClient) {

			val pPos = blockHitResult.pos
			val v = reflectionVector.multiply(0.25)
			for (i in 0..5) {
				world.addParticle(
					ParticleTypes.SMOKE,
					pPos.x,
					pPos.y,
					pPos.z,
					v.x + Core.RAND.nextGaussian(-0.1, 0.1),
					v.y + Core.RAND.nextGaussian(-0.1, 0.1),
					v.z + Core.RAND.nextGaussian(-0.1, 0.1)
				)
			}
			world.players.filter { it.pos.distanceTo(this.pos) < 100 }.forEach {
				it.playSound(
					SoundRegistry.getBoingSound(),
					SoundCategory.BLOCKS,
					(1.2 * (1 - it.pos.distanceTo(this.pos) / 80f)).toFloat(),
					Core.RAND.nextFloat(1.7f, 2f)
				)
			}
			if (!world.isClient) {
				Stuff.addParticles(
					world,
					ParticleTypes.EXPLOSION,
					3,
					1.0 + hitCount * 0.4,
					pPos.x,
					pPos.y,
					pPos.z,
					0.0,
					0.0,
					0.0
				)
			}
		}

		this.setPosition(this.pos.add(reflectionVector.multiply(0.5)))

		this.velocity = reflectionVector

	}
	
	override fun tick() {
		this.age++
		if (this.age > 20 * 20) {
			this.kill()
		}
		if (!this.world.isClient) {
			Stuff.addParticles(
				this.world,
				ParticleTypes.FLAME,
				3,
				0.01,
				this.x + Core.RAND.nextGaussian(0.0, 0.2),
				this.y + Core.RAND.nextGaussian(0.0, 0.2),
				this.z + Core.RAND.nextGaussian(0.0, 0.2),
				Core.RAND.nextGaussian(-0.1, 0.1),
				Core.RAND.nextGaussian(-0.1, 0.1),
				Core.RAND.nextGaussian(-0.1, 0.1)
			)
		}
		
		val nextPosition = this.pos.add(velocity)
		
		//TODO: Revisit this, maybe change to @setVelocityClient
		this.velocity = if (world.getBlockState(BlockPos(pos)).block is FluidBlock) {
			this.velocity.multiply(0.9, 0.9, 0.9).add(0.0, -0.02, 0.0)
		} else {
			this.velocity.multiply(0.99, 0.99, 0.99).add(0.0, -0.05, 0.0)
		}
		setVelocityClient(velocity.x, velocity.y, velocity.z)
		// check for collisions
		val hitResult = world.raycast(
			RaycastContext(
				pos,
				nextPosition.add(velocity),
				RaycastContext.ShapeType.COLLIDER,
				RaycastContext.FluidHandling.NONE,
				this
			)
		)

		this.move(MovementType.SELF, this.velocity)
		// check collision
		if (hitResult.type != HitResult.Type.MISS) this.onBlockHit(hitResult)
		
	}

	override fun writeCustomDataToNbt(nbt: NbtCompound) {
		super.writeCustomDataToNbt(nbt)
		nbt.putInt("HitCount", hitCount)
		nbt.putInt("LastHit", lastHit)
		nbt.putInt("MaxBounces", maxBounces)
	}

	override fun readCustomDataFromNbt(nbt: NbtCompound) {
		super.readCustomDataFromNbt(nbt)
		dataTracker.set(DATA_BOUNCES, nbt.getInt("HitCount"))
		dataTracker.set(DATA_LAST_BOUNCE, nbt.getInt("LastHit"))
		dataTracker.set(DATA_MAX_BOUNCES, nbt.getInt("MaxBounces"))
	}

	override fun initDataTracker() {
		dataTracker.startTracking(DATA_BOUNCES, 0)
		dataTracker.startTracking(DATA_LAST_BOUNCE, 0)
		dataTracker.startTracking(DATA_MAX_BOUNCES, 3)
	}
}
