package dev.evoloxi.noiwa.content.block

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.foundation.CombatHandler.hurtV
import dev.evoloxi.noiwa.foundation.DamageType
import dev.evoloxi.noiwa.foundation.registry.SoundRegistry
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties.EXTENDED
import net.minecraft.state.property.Properties.FACING
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class SpringTrapBlock(settings: Settings) : Block(settings), BlockEntityProvider {
	init {
		this.defaultState = this.stateManager.defaultState.with(EXTENDED, false).with(FACING, Direction.UP)
	}
	
	override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
		builder.add(EXTENDED)
		builder.add(FACING)
	}
	
	override fun getTranslationKey(): String {
		return "block.noiwa.spring_trap"
	}
	
	override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
		return defaultState.with(FACING, ctx.side)
	}
	
	override fun onSteppedOn(world: World, pos: BlockPos, state: BlockState, entity: Entity) {
		if (entity is LivingEntity || entity is ItemEntity) {
			if (!state.get(EXTENDED)) {
				world.setBlockState(pos, state.with(EXTENDED, true))
				entity.velocity = Vec3d(0.0, 1.0, 0.0)
				entity.velocityModified = true
				world.playSound(null, pos, SoundRegistry.boingSounds.random(), SoundCategory.BLOCKS, 1.0f,
				Core.RAND.nextFloat(0.8f, 1.2f))
				if (entity is LivingEntity) entity.hurtV(
                    amount = 100.0,
                    damageType = DamageType.TRAP
                )
			}
		}
	}

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
		return TileRegistry.SPRING_TRAP_TILE.instantiate(pos, state)
	}
}
