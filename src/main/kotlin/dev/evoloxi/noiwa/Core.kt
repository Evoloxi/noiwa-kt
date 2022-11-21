package dev.evoloxi.noiwa

import dev.evoloxi.noiwa.Core.Blocks.SPEED_PLATE_BLOCK
import dev.evoloxi.noiwa.Core.Blocks.SPIKE_TRAP
import dev.evoloxi.noiwa.Core.Blocks.SPRING_TRAP
import dev.evoloxi.noiwa.content.block.SpeedPlateBlock
import dev.evoloxi.noiwa.content.block.SpikeBlock
import dev.evoloxi.noiwa.content.block.SpringTrapBlock
import dev.evoloxi.noiwa.content.block.TileRegistry
import dev.evoloxi.noiwa.foundation.handler.TickHandler
import dev.evoloxi.noiwa.foundation.registry.*
//import dev.evoloxi.noiwa.content.item.AlphaPass
import net.minecraft.block.Material
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.attribute.ClampedEntityAttribute
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemGroup
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings
import org.quiltmc.qsl.lifecycle.api.event.ServerTickEvents
import java.util.*

class Core : ModInitializer {
	
	object Blocks {
		val SPIKE_TRAP = SpikeBlock(QuiltBlockSettings.of(Material.WOOD).strength(30f))
		val SPRING_TRAP = SpringTrapBlock(QuiltBlockSettings.of(Material.STONE).strength(30f))
		val SPEED_PLATE_BLOCK = SpeedPlateBlock(QuiltBlockSettings.of(Material.STONE).strength(30f))
	}

	override fun onInitialize(mod: ModContainer?) {
		
		Registry.register(Registry.BLOCK, Identifier(MOD_ID, "spike_trap"), SPIKE_TRAP)
		Registry.register(
			Registry.ITEM,
			Identifier(MOD_ID, "spikes"),
			BlockItem(SPIKE_TRAP, QuiltItemSettings().group(ItemGroup.MISC))
		)
		
		Registry.register(Registry.BLOCK, Identifier(MOD_ID, "spring_trap"), SPRING_TRAP)
		Registry.register(
			Registry.ITEM,
			Identifier(MOD_ID, "spring_trap"),
			BlockItem(SPRING_TRAP, QuiltItemSettings().group(ItemGroup.MISC))
		)
		
		Registry.register(Registry.BLOCK, Identifier(MOD_ID, "speed_plate_block"), SPEED_PLATE_BLOCK)
		Registry.register(
			Registry.ITEM,
			Identifier(MOD_ID, "speed_plate_block"),
			BlockItem(SPEED_PLATE_BLOCK, QuiltItemSettings().group(ItemGroup.MISC))
		)
		
		CommandRegistry.register()
		ParticleRegistry.register()
		EntityRegistry.register()
		SoundRegistry.register()
		TileRegistry.register()
		PacketRegistry.registerS2CPackets()
		ItemRegistry.register()
		AttributeRegistry.register()
		ServerTickEvents.START.register(ServerTickEvents.Start { obj: MinecraftServer -> TickHandler.onTick(obj) })
		ContainerRegistry.register()
//        Registry.register(Registry.ATTRIBUTE, id("hp_regen"), EntityAttributes.HP_REGEN)
//        Registry.register(Registry.ATTRIBUTE, id("damage"), EntityAttributes.DAMAGE)
//        Registry.register(Registry.ATTRIBUTE, id("strength"), EntityAttributes.STRENGTH)
//        Registry.register(Registry.ATTRIBUTE, id("crit_damage"), EntityAttributes.CRIT_DAMAGE)
//        Registry.register(Registry.ATTRIBUTE, id("crit_chance"), EntityAttributes.CRIT_CHANCE)
//        Registry.register(Registry.ATTRIBUTE, id("attack_speed"), EntityAttributes.ATTACK_SPEED)
//        Registry.register(Registry.ATTRIBUTE, id("ferocity"), EntityAttributes.ECHO)
//        Registry.register(Registry.ATTRIBUTE, id("baseDefense"), EntityAttributes.DEFENSE)
//        Registry.register(Registry.ATTRIBUTE, id("max_mana"), EntityAttributes.MAX_MANA)
//        Registry.register(Registry.ATTRIBUTE, id("mana_regen"), EntityAttributes.MANA_REGEN)
//        Registry.register(Registry.ATTRIBUTE, id("mana_regen_percent"), EntityAttributes.MANA_REGEN_PERCENT)
		// baseDamage, strength, attack baseSpeed, ferocity
		
		//Registry.register(Registry.ATTRIBUTE, new Identifier(Core.MOD_ID, "casting_multiplier"), EntityAttributes.MANA_COST);
		//Registry.register(Registry.ATTRIBUTE, new Identifier(Core.MOD_ID, "burnout_regen"), EntityAttributes.BURNOUT_REGEN);
		//Registry.register(Registry.ATTRIBUTE, new Identifier(Core.MOD_ID, "mana_lock"), EntityAttributes.MANA_LOCK);
	}
	
	object EntityAttributes {
		val MAX_MANA: EntityAttribute = ClampedEntityAttribute(generic + "max_mana", 100.0, 0.0, 102400.0).setTracked(true)
		val MANA_REGEN: EntityAttribute = ClampedEntityAttribute(generic + "mana_regen", 1.0, 0.0, 1024.0).setTracked(true)
		val MANA_REGEN_PERCENT: EntityAttribute =
			ClampedEntityAttribute(generic + "mana_regen_percent", 0.2, 0.0, 1024.0).setTracked(true)
	}
	
	companion object {
		const val TO_RAD: Double = 0.01745329251
		const val TO_DEG: Double = 57.2957795131
		
		const val TO_RAD_F: Float = 0.017453292f
		const val TO_DEG_F: Float = 57.29578f
		// get the thread of the main game
		val isPaused: Boolean
			get() = MinecraftClient.getInstance().isPaused
		val LOGGER: Logger = LogManager.getLogger()
		const val MOD_ID = "noiwa"
		const val MOD_NAME = "Noiwa"
		
		@JvmField
		var RAND = Random()
		
		// Entity Attributes
		const val generic = "attribute.generic"
		
		fun id(name: String): Identifier {
			return Identifier(MOD_ID, name)
		}
		
		fun log(level: Level?, message: String) {
			LOGGER.log(level, "[$MOD_NAME] $message")
		}
	}
}

// kotlin extension function that is accessible from everywhere


