package dev.evoloxi.noiwa.content.block

import dev.evoloxi.noiwa.foundation.Extensions.isGhost
import dev.evoloxi.noiwa.foundation.Extensions.server
import dev.evoloxi.noiwa.foundation.registry.SoundRegistry
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.random.RandomGenerator
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

@Suppress("PrivatePropertyName")
class SpeedPlateBlock(settings: Settings) : Block(settings), BlockEntityProvider {
	
	private val SHAPE_POWERED: VoxelShape = createCuboidShape(-11.0, 0.0, -11.0, 27.0, 5.0, 27.0)
	private val SHAPE_UNPOWERED: VoxelShape = createCuboidShape(-11.0, 0.0, -11.0, 27.0, 1.0, 27.0)
	init {
		this.defaultState = this.stateManager.defaultState.with(POWERED, false).with(FACING, Direction.NORTH)
			.with(PropertyRegistry.PLATE_PART, PlatePart.CENTER)
	}
	
	override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
		builder.add(POWERED, FACING, PropertyRegistry.PLATE_PART)
	}
	
	// name
	override fun getTranslationKey(): String {
		return "block.noiwa.plate_speed"
	}
	
	// default state
	override fun getPlacementState(ctx: ItemPlacementContext?): BlockState? {
		return this.defaultState.with(FACING, ctx?.playerFacing?.opposite)
	}
	
	override fun onSteppedOn(world: World, pos: BlockPos, state: BlockState, entity: Entity) {
		server(world) {
			if (entity !is LivingEntity || entity.isGhost) return@server
			val tile = world.getBlockEntity(pos) as? SpeedPlateTile ?: return@server
			if (tile.blocked) return@server
			if (!state.get(POWERED)) {
				entity.velocity = entity.velocity.add(0.0, 0.5, 0.0)
				world.playSound(null, pos, SoundRegistry.getStoneSound(), SoundCategory.BLOCKS, 1.0f, 0.5f)
				world.setBlockState(pos, state.with(POWERED, true))
				world.scheduleBlockTick(BlockPos(pos), this, 100)
				tile.blocked = true
				tile.timestamp = world.time
			}
		}
	}
	
	override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: RandomGenerator) {
		val tile = world.getBlockEntity(pos) as? SpeedPlateTile ?: return
		// if activated and blocked, unblock
		if (state.get(POWERED) && tile.blocked) {
			world.setBlockState(pos, state.with(POWERED, false))
			// schedule unblock
			world.playSound(null, pos, SoundRegistry.getStoneSound(), SoundCategory.BLOCKS, 1.0f, 0.5f)
			world.scheduleBlockTick(BlockPos(pos), this, 100)
			tile.timestamp = world.time
		} else if (!state.get(POWERED) && tile.blocked) {
			tile.blocked = false
		} else if (state.get(POWERED) && !tile.blocked) {
			world.setBlockState(pos, state.with(POWERED, false))
			tile.timestamp = world.time
		}
	}
	
	// shape
	override fun getOutlineShape(state: BlockState?, world: BlockView, pos: BlockPos?, context: ShapeContext?): VoxelShape? {
		val tile = world.getBlockEntity(pos) as? SpeedPlateTile ?: return VoxelShapes.fullCube()
		return VoxelShapes.cuboid(-0.6875, 0.0, -0.6875, 1.6875, tile.plateOffset.toDouble() + 0.32, 1.6875)
	}
	
	override fun getRenderType(state: BlockState): BlockRenderType {
		return BlockRenderType.INVISIBLE
	}
	
	override fun isTranslucent(state: BlockState, world: BlockView, pos: BlockPos): Boolean {
		return false
	}
	
	override fun getOpacity(state: BlockState?, world: BlockView?, pos: BlockPos?): Int {
		return 0
	}
	
	override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack?) {
		// place the 8 other parts around the center
		// 3x3 grid, middle is center, 8 other parts with state part: corner or edge
		// TO COPILOT. STOP FUCKING ASSUMING PLATEPARTS HAS AN OFFSET PROPERTY
		val facing = state.get(FACING)
		val allPlateParts = listOf(
			PlatePart.TOP_LEFT,
			PlatePart.TOP,
			PlatePart.TOP_RIGHT,
			PlatePart.LEFT,
			PlatePart.CENTER,
			PlatePart.RIGHT,
			PlatePart.BOTTOM_LEFT,
			PlatePart.BOTTOM,
			PlatePart.BOTTOM_RIGHT
		)
		for (x in -1..1) {
			for (z in -1..1) {
				val offsetIndex = when (facing) {
					Direction.EAST -> 0
					Direction.SOUTH -> 1
					Direction.WEST -> 2
					else -> 3
				}
				val platePart = allPlateParts[(x + 1) + (z + 1) * 3]
				if (platePart == PlatePart.CENTER) continue
				val platePos = pos.add(x, 0, z)
				world.setBlockState(platePos, state.with(PropertyRegistry.PLATE_PART, platePart))
			}
		}
		
		
	}
	
	override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
		return if (state.get(PropertyRegistry.PLATE_PART) == PlatePart.CENTER) {
			SpeedPlateTile(pos, state)
		} else {
			null
		}
	}
}
