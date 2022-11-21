package dev.evoloxi.noiwa.foundation.registry

import dev.evoloxi.noiwa.foundation.handler.LoreHandler
import dev.evoloxi.noiwa.foundation.handler.NbtUpdater
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import dev.evoloxi.noiwa.foundation.handler.AttributeHandler
import net.minecraft.command.CommandBuildContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.ServerCommandSource
import org.quiltmc.qsl.command.api.CommandRegistrationCallback
import org.quiltmc.qsl.command.api.EnumArgumentType

object CommandRegistry {
	fun register() {
		CommandRegistrationCallback.EVENT.register(
			CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>, _: CommandBuildContext?, _: RegistrationEnvironment? ->
				dispatcher.register(
					CommandManager.literal("itype")
						.then(argument("type", EnumArgumentType.enumConstant(EnumRegistry.Type::class.java))
							.executes { ctx: CommandContext<ServerCommandSource> ->
								LoreHandler.setType(ctx)
								1
							}
						)
				)
			}
		)
		CommandRegistrationCallback.EVENT.register(
			CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>, _: CommandBuildContext?, _: RegistrationEnvironment? ->
				dispatcher.register(CommandManager.literal("nbt")
					.executes { ctx: CommandContext<ServerCommandSource> ->
						val player: PlayerEntity = ctx.source.player!!
						val nbt = player.mainHandStack.orCreateNbt
						NbtUpdater.updateNBT(nbt, player)
						Command.SINGLE_SUCCESS
					}
				)
			}
		)
		CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>, _: CommandBuildContext?, _: RegistrationEnvironment? ->
			dispatcher.register(
				CommandManager.literal("iquality")
					.then(argument("value", FloatArgumentType.floatArg(-1.0f, 100.0f))
						.executes { ctx: CommandContext<ServerCommandSource> ->
							LoreHandler.setRarity(ctx)
							1
						}
					)
			)
		})
		CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>, _: CommandBuildContext?, _: RegistrationEnvironment? ->
			dispatcher.register(
				CommandManager.literal("iname")
					.then(argument("name", StringArgumentType.greedyString())
						.executes { ctx: CommandContext<ServerCommandSource> ->
							LoreHandler.setDisplayName(ctx)
							1
						})
			)
		})
		CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>, _: CommandBuildContext?, _: RegistrationEnvironment? ->
			dispatcher.register(
				CommandManager.literal("irarity")
					.then(argument("rarity", EnumArgumentType.enumConstant(EnumRegistry.Rarity::class.java))
						.executes { ctx: CommandContext<ServerCommandSource> ->
							LoreHandler.setRarity(ctx, ctx.getArgument("rarity", String::class.java).uppercase())
							1
						}
					)
			)
		})
		CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>, _: CommandBuildContext?, _: RegistrationEnvironment? ->
			dispatcher.register(CommandManager.literal("ilore")
				.then(
					argument("line", IntegerArgumentType.integer())
						.then(
							argument("text", StringArgumentType.greedyString())
								.executes { context: CommandContext<ServerCommandSource> ->
									LoreHandler.addDescription(context, LoreHandler.loreMode.REPLACE)
									1
								}
						)
				)
				.then(
					// if the keword "add" is used, the lore will be added to the end of the lore
					LiteralArgumentBuilder.literal<ServerCommandSource>("add")
						.then(
							argument("text", StringArgumentType.greedyString())
								.executes { context: CommandContext<ServerCommandSource> ->
									LoreHandler.addDescription(context, LoreHandler.loreMode.ADD)
									1
								}
						)
				)
			)
		})

		CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>, _: CommandBuildContext?, _: RegistrationEnvironment? ->
			dispatcher.register(CommandManager.literal("istat")
				.then(
					// enum
					argument("stat", EnumArgumentType.enumConstant(EnumRegistry.Stats::class.java))
						.then(
							argument("value", DoubleArgumentType.doubleArg())
								.executes { context: CommandContext<ServerCommandSource> ->
									AttributeHandler.addAttribute(context)
									1
								}
						)
				)
			)
		})
		CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>, _: CommandBuildContext?, _: RegistrationEnvironment? ->
			dispatcher.register(CommandManager.literal("ienchant")
				.then(
					argument("enchant", StringArgumentType.word())
						.then(
							argument("level", IntegerArgumentType.integer(0))
								.executes { context: CommandContext<ServerCommandSource> ->
									LoreHandler.addEnchant(context)
									1
								}
						)
				)
			)
		})
	}
}
