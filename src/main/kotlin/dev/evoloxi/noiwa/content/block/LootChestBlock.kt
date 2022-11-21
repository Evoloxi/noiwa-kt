//@file:Suppress("OVERRIDE_DEPRECATION")
//
//package net.minecraft.block
//
//import it.unimi.dsi.fastutil.floats.Float2FloatFunction
//import net.minecraft.block.Block.createCuboidShape
//import net.minecraft.block.DoubleBlockProperties.PropertyRetriever
//import net.minecraft.block.entity.BlockEntity
//import net.minecraft.block.entity.BlockEntityTicker
//import net.minecraft.block.entity.BlockEntityType
//import net.minecraft.block.entity.ChestBlockEntity
//import net.minecraft.block.enums.ChestType
//import net.minecraft.client.block.ChestAnimationProgress
//import net.minecraft.entity.LivingEntity
//import net.minecraft.entity.ai.pathing.NavigationType
//import net.minecraft.entity.mob.PiglinBrain
//import net.minecraft.entity.passive.CatEntity
//import net.minecraft.entity.player.PlayerEntity
//import net.minecraft.entity.player.PlayerInventory
//import net.minecraft.fluid.FluidState
//import net.minecraft.fluid.Fluids
//import net.minecraft.inventory.DoubleInventory
//import net.minecraft.inventory.Inventory
//import net.minecraft.item.ItemPlacementContext
//import net.minecraft.item.ItemStack
//import net.minecraft.screen.GenericContainerScreenHandler
//import net.minecraft.screen.NamedScreenHandlerFactory
//import net.minecraft.screen.ScreenHandler
//import net.minecraft.server.world.ServerWorld
//import net.minecraft.stat.Stat
//import net.minecraft.stat.Stats
//import net.minecraft.state.StateManager
//import net.minecraft.state.property.Properties
//import net.minecraft.state.property.Properties.CHEST_TYPE
//import net.minecraft.text.Text
//import net.minecraft.util.*
//import net.minecraft.util.hit.BlockHitResult
//import net.minecraft.util.math.BlockPos
//import net.minecraft.util.math.Box
//import net.minecraft.util.math.Direction
//import net.minecraft.util.random.RandomGenerator
//import net.minecraft.util.shape.VoxelShape
//import net.minecraft.world.BlockView
//import net.minecraft.world.World
//import net.minecraft.world.WorldAccess
//import java.util.*
//import java.util.function.BiPredicate
//import java.util.function.Supplier
//
///**
// * Access widened by quilt_block_extensions to accessible
// */
//open class LootChestBlock(settings: Settings?, supplier: Supplier<BlockEntityType<out ChestBlockEntity?>?>?) :
//	AbstractChestBlock<ChestBlockEntity?>(settings, supplier), Waterloggable {
//	init {
//		defaultState =
//			stateManager.defaultState.with(FACING, Direction.NORTH).with(WATERLOGGED, java.lang.Boolean.valueOf(false))
//	}
//
//	override fun getRenderType(state: BlockState): BlockRenderType {
//		return BlockRenderType.ENTITYBLOCK_ANIMATED
//	}
//
//	override fun getStateForNeighborUpdate(
//		state: BlockState, direction: Direction, neighborState: BlockState, world: WorldAccess, pos: BlockPos, neighborPos: BlockPos
//	): BlockState {
//		if (state.get(WATERLOGGED)) {
//			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world))
//		}
//		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
//	}
//
//	override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
//		return SINGLE_SHAPE
//	}
//
//	override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
//		var direction = ctx.playerFacing.opposite
//		val fluidState = ctx.world.getFluidState(ctx.blockPos)
//		return defaultState.with(FACING, direction).with(WATERLOGGED, java.lang.Boolean.valueOf(fluidState.fluid === Fluids.WATER))
//	}
//
//	override fun getFluidState(state: BlockState): FluidState {
//		return if (state.get(WATERLOGGED)) Fluids.WATER.getStill(false) else super.getFluidState(state)
//	}
//
//	override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
//		if (itemStack.hasCustomName()) {
//			val blockEntity = world.getBlockEntity(pos)
//			if (blockEntity is ChestBlockEntity) {
//				blockEntity.customName = itemStack.name
//			}
//		}
//	}
//
//	override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
//		if (!state.isOf(newState.block)) {
//			val blockEntity = world.getBlockEntity(pos)
//			if (blockEntity is Inventory) {
//				ItemScatterer.spawn(world, pos, blockEntity as Inventory?)
//				world.updateComparators(pos, this)
//			}
//			super.onStateReplaced(state, world, pos, newState, moved)
//		}
//	}
//
//	override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
//		return if (world.isClient) {
//			ActionResult.SUCCESS
//		} else {
//			val namedScreenHandlerFactory = createScreenHandlerFactory(state, world, pos)
//			if (namedScreenHandlerFactory != null) {
//				player.openHandledScreen(namedScreenHandlerFactory)
//				player.incrementStat(openStat)
//				PiglinBrain.onGuardedBlockInteracted(player, true)
//			}
//			ActionResult.CONSUME
//		}
//	}
//
//	protected open val openStat: Stat<Identifier?>?
//		get() = Stats.CUSTOM.getOrCreateStat(Stats.OPEN_CHEST)
//	val expectedEntityType: BlockEntityType<out ChestBlockEntity?>
//		get() = entityTypeRetriever.get() as BlockEntityType<out ChestBlockEntity?>
//
//	override fun createScreenHandlerFactory(state: BlockState, world: World, pos: BlockPos): NamedScreenHandlerFactory? {
//		return (getBlockEntitySource(state, world, pos, false).apply(NAME_RETRIEVER) as Optional<*>).orElse(null) as NamedScreenHandlerFactory
//	}///////
//
//	override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
//		return ChestBlockEntity(pos, state)
//	}
//
//	override fun <T : BlockEntity?> getTicker(world: World, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
//		return if (world.isClient) checkType(
//			type,
//			expectedEntityType
//		) { world: World?, pos: BlockPos?, state: BlockState?, blockEntity: ChestBlockEntity? ->
//			ChestBlockEntity.clientTick(
//				world,
//				pos,
//				state,
//				blockEntity
//			)
//		} else null
//	}
//
//	override fun getBlockEntitySource(
//		state: BlockState?,
//		world: World?,
//		pos: BlockPos?,
//		ignoreBlocked: Boolean
//	): DoubleBlockProperties.PropertySource<out ChestBlockEntity> {
//		TODO("Not yet implemented")
//	}
//
//	override fun hasComparatorOutput(state: BlockState): Boolean {
//		return true
//	}
//
//	override fun getComparatorOutput(state: BlockState, world: World, pos: BlockPos): Int {
//		return ScreenHandler.calculateComparatorOutput(getInventory(this, state, world, pos, false))
//	}
//
//	override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
//		return state.with(FACING, rotation.rotate(state.get(FACING)))
//	}
//
//	override fun mirror(state: BlockState, mirror: BlockMirror): BlockState {
//		return state.rotate(mirror.getRotation(state.get(FACING)))
//	}
//
//	override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
//		builder.add(FACING, WATERLOGGED)
//	}
//
//	override fun canPathfindThrough(state: BlockState, world: BlockView, pos: BlockPos, type: NavigationType): Boolean {
//		return false
//	}
//
//	override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: RandomGenerator) {
//		val blockEntity = world.getBlockEntity(pos)!!
//		if (blockEntity is ChestBlockEntity) {
//			blockEntity.onScheduledTick()
//		}
//	}
//
//	companion object {
//		val FACING = HorizontalFacingBlock.FACING
//		val WATERLOGGED = Properties.WATERLOGGED
//		const val EVENT_SET_OPEN_COUNT = 1
//		protected const val SHAPE_OFFSET = 1
//		protected const val SHAPE_HEIGHT = 14
//		protected val SINGLE_SHAPE = createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0)
//		private val INVENTORY_RETRIEVER: PropertyRetriever<ChestBlockEntity, Optional<Inventory>> =
//			object : PropertyRetriever<ChestBlockEntity, Optional<Inventory>> {
//				override fun getFromBoth(chestBlockEntity: ChestBlockEntity, chestBlockEntity2: ChestBlockEntity): Optional<Inventory> {
//					return Optional.of(DoubleInventory(chestBlockEntity, chestBlockEntity2))
//				}
//
//				override fun getFrom(chestBlockEntity: ChestBlockEntity): Optional<Inventory> {
//					return Optional.of(chestBlockEntity)
//				}
//
//				override fun getFallback(): Optional<Inventory> {
//					return Optional.empty()
//				}
//			}
//		private val NAME_RETRIEVER: PropertyRetriever<ChestBlockEntity, Optional<NamedScreenHandlerFactory>> =
//			object : PropertyRetriever<ChestBlockEntity, Optional<NamedScreenHandlerFactory>> {
//				override fun getFromBoth(chestBlockEntity: ChestBlockEntity, chestBlockEntity2: ChestBlockEntity): Optional<NamedScreenHandlerFactory> {
//					val inventory: Inventory = DoubleInventory(chestBlockEntity, chestBlockEntity2)
//					return Optional.of(object : NamedScreenHandlerFactory {
//						override fun createMenu(i: Int, playerInventory: PlayerInventory, playerEntity: PlayerEntity): ScreenHandler? {
//							return if (chestBlockEntity.checkUnlocked(playerEntity) && chestBlockEntity2.checkUnlocked(playerEntity)) {
//								chestBlockEntity.checkLootInteraction(playerInventory.player)
//								chestBlockEntity2.checkLootInteraction(playerInventory.player)
//								GenericContainerScreenHandler.createGeneric9x6(i, playerInventory, inventory)
//							} else {
//								null
//							}
//						}
//
//						override fun getDisplayName(): Text {
//							return if (chestBlockEntity.hasCustomName()) {
//								chestBlockEntity.displayName
//							} else {
//								(if (chestBlockEntity2.hasCustomName()) chestBlockEntity2.displayName else Text.translatable("container.chestDouble")) as Text
//							}
//						}
//					})
//				}
//
//				override fun getFrom(chestBlockEntity: ChestBlockEntity): Optional<NamedScreenHandlerFactory> {
//					return Optional.of(chestBlockEntity)
//				}
//
//				override fun getFallback(): Optional<NamedScreenHandlerFactory> {
//					return Optional.empty()
//				}
//			}
//
//
//		fun getFacing(state: BlockState): Direction {
//			val direction = state.get(FACING)
//			return if (state.get(CHEST_TYPE) == ChestType.LEFT) direction.rotateYClockwise() else direction.rotateYCounterclockwise()
//		}
//
//		fun getInventory(block: ChestBlock, state: BlockState, world: World, pos: BlockPos, ignoreBlocked: Boolean): Inventory? {
//			return (block.getBlockEntitySource(state, world, pos, ignoreBlocked).apply(INVENTORY_RETRIEVER) as Optional<*>).orElse(null) as Inventory
//		}
//
//		fun isChestBlocked(world: WorldAccess, pos: BlockPos): Boolean {
//			return hasBlockOnTop(world, pos)
//		}
//
//		private fun hasBlockOnTop(world: BlockView, pos: BlockPos): Boolean {
//			val blockPos = pos.up()
//			return world.getBlockState(blockPos).isSolidBlock(world, blockPos)
//		}
//	}
//}
