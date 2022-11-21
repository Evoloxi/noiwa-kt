package dev.evoloxi.noiwa.content.block

import dev.evoloxi.noiwa.Core
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import org.quiltmc.qsl.block.entity.api.QuiltBlockEntityTypeBuilder

object TileRegistry {
	// TODO: revisit this
	val SPIKE_TRAP_TILE: BlockEntityType<SpikeTrapTile> = Registry.register(
		Registry.BLOCK_ENTITY_TYPE,
		Core.id("spike_trap_tile"),
		QuiltBlockEntityTypeBuilder.create({ pos: BlockPos, state: BlockState ->
			SpikeTrapTile(
				pos,
				state
			)
		}, Core.Blocks.SPIKE_TRAP).build()
	)
	val SPRING_TRAP_TILE: BlockEntityType<SpringTrapTile> = Registry.register(
		Registry.BLOCK_ENTITY_TYPE,
		Core.id("spring_trap_tile"),
		QuiltBlockEntityTypeBuilder.create({ pos: BlockPos, state: BlockState ->
			SpringTrapTile(
				pos,
				state
			)
		}, Core.Blocks.SPRING_TRAP).build()
	)
	val SPEED_PLATE_TILE: BlockEntityType<SpeedPlateTile> = Registry.register(
		Registry.BLOCK_ENTITY_TYPE,
		Core.id("speed_plate_tile"),
		QuiltBlockEntityTypeBuilder.create({ pos: BlockPos, state: BlockState ->
			SpeedPlateTile(
				pos,
				state
			)
		}, Core.Blocks.SPEED_PLATE_BLOCK).build()
	)
	
	fun registerRenderers() {
		BlockEntityRendererRegistry.register(
			SPIKE_TRAP_TILE,
			BlockEntityRendererFactory { SpikeTileRenderer() })
		BlockEntityRendererRegistry.register(
			SPRING_TRAP_TILE,
			BlockEntityRendererFactory { SpringTrapTileRenderer() })
		BlockEntityRendererRegistry.register(
			SPEED_PLATE_TILE,
			BlockEntityRendererFactory { SpeedPlateTileRenderer() })
	}
	
	fun register() {
	
	}
}
