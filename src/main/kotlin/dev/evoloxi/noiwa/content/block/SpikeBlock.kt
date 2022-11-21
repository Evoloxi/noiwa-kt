@file:Suppress("OVERRIDE_DEPRECATION", "NAME_SHADOWING")

package dev.evoloxi.noiwa.content.block

import dev.evoloxi.noiwa.foundation.CombatHandler.hurtV
import dev.evoloxi.noiwa.foundation.DamageType
import dev.evoloxi.noiwa.foundation.Extensions.isGhost
import dev.evoloxi.noiwa.foundation.Extensions.server
import dev.evoloxi.noiwa.foundation.Extensions.stat
import dev.evoloxi.noiwa.foundation.registry.SoundRegistry
import dev.evoloxi.noiwa.foundation.registry.AttributeRegistry
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties.EXTENDED
import net.minecraft.state.property.Properties.LOCKED
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.random.RandomGenerator
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

open class SpikeBlock(settings: Settings) : Block(settings), BlockEntityProvider {
	
	init {
		this.defaultState = this.stateManager.defaultState.with(EXTENDED, false).with(LOCKED, false)
	}
	
	override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
		builder.add(EXTENDED)
		builder.add(LOCKED)
	}
	
	// name
	override fun getTranslationKey(): String {
		return "block.noiwa.spike"
	}
	
	// default state
	override fun getPlacementState(ctx: ItemPlacementContext?): BlockState? {
		return this.defaultState
	}
	
	override fun onSteppedOn(world: World, pos: BlockPos, state: BlockState, entity: Entity) {
		
		server(world) {
			if (entity !is LivingEntity) return@server
			if (entity.isGhost) return@server
			
			if (!state.get(EXTENDED)) {
				world.playSound(null, pos, SoundRegistry.SPIKE_TRAP_OUT, SoundCategory.MASTER, 1f, 1f)
				val hp = entity.stat.maxHealth
				entity.hurtV(hp * 1.5f + 100, DamageType.TRAP_CRIT)
				
				world.setBlockState(pos, state.with(EXTENDED, true))
				
				world.scheduleBlockTick(BlockPos(pos), this, getTickRate())
				
			} else {
				if (entity.fallDistance > 1f) {
					entity.sendSystemMessage(Text.of("You fell on a spike!"))
					entity.hurtV(amount = 50.0 * entity.fallDistance * entity.fallDistance, DamageType.TRAP_CRIT)
				}
			}
		}
	}
	
	override fun getVelocityMultiplier(): Float {
		return -0.1f
	}
	
	private fun getTickRate(): Int {
		return 100
	}
	
	override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: RandomGenerator) {
		if (world.getBlockState(pos).block is SpikeBlock && state.get(EXTENDED)) {
			world.setBlockState(pos, state.with(EXTENDED, false))
			world.playSound(null, pos, SoundRegistry.SPIKE_TRAP_IN, SoundCategory.MASTER, 1f, 1f)
		}
	}
	

	
	// get all spike blocks in a 3x1x3 cube
	/*
	private fun getConnectedSpikes(world: World, pos: BlockPos): MutableList<BlockPos> {
		val nearbySpikes = mutableListOf<BlockPos>()
		for (x in -2..2) {
			for (z in -2..2) {
				val block = world.getBlockState(pos.add(x, 0, z)).block
				if (block is SpikeBlock) {
					nearbySpikes.add(pos.add(x, 0, z))
				}
			}
		}
		return nearbySpikes
	}
	
	override fun randomTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: RandomGenerator?) {
		if (state.get(EXTENDED) == true && state.get(LOCKED) == false) {
			world.setBlockState(pos, state.with(EXTENDED, false))
			world.playSound(null, pos, SoundRegistry.SPIKE_TRAP_IN, SoundCategory.MASTER, 1f, 1f)
		}
		// also retract nearby blocks
		for (direction in Direction.values()) {
			val blockPos = pos.offset(direction)
			val blockState = world.getBlockState(blockPos)
			if (blockState.block is SpikeBlock && blockState.get(EXTENDED) == true) {
				world.setBlockState(blockPos, blockState.with(EXTENDED, false))
			}
		}
		
	}
	
	override fun hasRandomTicks(state: BlockState): Boolean {
		return !state.get(LOCKED)
	}
	*/
	
	override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
		// in creative mode, you toggle extended state by right clicking, and lock state by sneaking and right clicking
		if (player.isCreative && player.mainHandStack.isEmpty) {
			return if (player.isSneaking) {
				world.setBlockState(pos, state.with(LOCKED, !state.get(LOCKED)))
				ActionResult.SUCCESS
			} else {
				world.setBlockState(pos, state.with(EXTENDED, !state.get(EXTENDED)))
				ActionResult.SUCCESS
			}
		}
		return ActionResult.PASS
	}
	
	override fun onLandedUpon(world: World, state: BlockState, pos: BlockPos, entity: Entity, fallDistance: Float) {
		// hurt entities that fall on the spike
		
	}
	
	// shape
	override fun getOutlineShape(state: BlockState?, view: BlockView?, pos: BlockPos?, context: ShapeContext?): VoxelShape {
		return VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
	}
	
	override fun getRenderType(state: BlockState): BlockRenderType {
		return BlockRenderType.MODEL
	}
	
	override fun isTranslucent(state: BlockState, world: BlockView, pos: BlockPos): Boolean {
		return false
	}
	
	override fun getOpacity(state: BlockState?, world: BlockView?, pos: BlockPos?): Int {
		return 0
	}
	
	override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
		return TileRegistry.SPIKE_TRAP_TILE.instantiate(pos, state)
	}
	
}
