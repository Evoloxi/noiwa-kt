package dev.evoloxi.noiwa.foundation.spell.item.wand

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.foundation.inventory.WandInventory
import dev.evoloxi.noiwa.foundation.inventory.WandScreenHandler
import dev.evoloxi.noiwa.foundation.registry.ItemRegistry
import dev.evoloxi.noiwa.foundation.spell.cast.CastHelper
import dev.evoloxi.noiwa.foundation.spell.cast.WandSpellPoolVisitor
import dev.evoloxi.noiwa.foundation.spell.entity.SpellEntityBase
import dev.evoloxi.noiwa.foundation.spell.item.BaseItem
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import java.awt.Color
import java.util.*

/**
 * About Wand:
 * [WandItem] how wand is used, displayed or ticked in inventory as an [Item] Singleton
 * [WandData] how data of wand is stored, accessed and modified as an instance of [ItemStack]
 * [WandInventory] how the inventory(spell list) is stored, accessed and modified as an instance of [ItemStack]
 *
 * Most direct operations of NBT should be kept in [WandData] and [WandInventory]
 */
class WandItem(settings: Settings) : BaseItem(settings.maxCount(1).group(ItemGroup.COMBAT)) {
    /******************** Cast spells  */
    override fun onStoppedUsing(stack: ItemStack, world: World, player: LivingEntity, count: Int) {
        if (!player.world.isClient && player is ServerPlayerEntity) {
            if (!player.isSneaking()) {
                cast(player.world, player, stack)
            }
        } else {
            player.forwardSpeed *= 5f
            player.sidewaysSpeed *= 5f
        }
    }

    /**
     * Only effective in main hand.
     * Shift + right click = open GUI to edit wand
     */
    override fun use(worldIn: World, playerIn: PlayerEntity, handIn: Hand): TypedActionResult<ItemStack> {
        val itemStack = playerIn.getStackInHand(handIn)
        if (!worldIn.isClient && playerIn is ServerPlayerEntity) {
            if (handIn == Hand.MAIN_HAND && itemStack.item == ItemRegistry.WAND) {
                if (playerIn.isSneaking()) {
                    WandData(itemStack)
	                openScreen(playerIn, playerIn.getStackInHand(handIn))
                    return TypedActionResult.success(itemStack)
                } else {
					cast(worldIn, playerIn, itemStack)
				}
            }
        }
        return TypedActionResult.pass(itemStack)
    }

    private fun cast(world: World, caster: PlayerEntity, wandStack: ItemStack) {
        Core.LOGGER.log(Level.WARN,"Start casting...")
        val wandData = WandData(wandStack)
        if (wandData.cooldown > 0) {
            Core.LOGGER.log(Level.WARN, "Cooldown is not over!")
            return
        }
        Core.LOGGER.log(Level.WARN,"Spell pool is {}", wandData.spellPool.contentToString())
        Core.LOGGER.log(Level.WARN,"Current spell pool pointer: {}", wandData.spellPoolPointer)
        val oldPoolPointer = wandData.spellPoolPointer.toInt()
        val castResult = CastHelper.processSpellList(
	        WandSpellPoolVisitor(
		        wandData,
		        WandInventory(wandStack)
	        ),
	        wandData.mana
        )
        if (wandData.spellPoolPointer < wandData.spellPool.size && wandData.spellPoolPointer > oldPoolPointer) {
            wandData.cooldown = castResult.castDelay + wandData.castDelay
        } else {
            wandData.cooldown =
	            (castResult.rechargeTime + wandData.rechargeTime).coerceAtLeast(castResult.castDelay + wandData.castDelay)
            wandData.refreshWandPool()
        }
	    wandData.manaMax = 10000
        Core.LOGGER.log(Level.WARN,"Start summoning spells...")
        for ((spell, value) in castResult.spell2TriggeredSpellList) {
            val spellEntity: SpellEntityBase = spell.entitySummoner().apply(world, caster)
            spellEntity.castList = value
            var speed = 0f
            val speedMin = spell.speedMin
            val speedMax = spell.speedMax
            if (speedMin < speedMax) speed = (Core.RAND.nextInt(speedMax - speedMin) + speedMin).toFloat()
            speed += 200f
            speed /= 600f
            spellEntity.shoot(caster, caster.pitch, caster.yaw, speed, 1.0f)
            world.spawnEntity(spellEntity)
        }
        Core.LOGGER.log(Level.WARN,"Casting finish!")
    }

    /**
     * Cooldown and Mana regen.
     * If wand is not in main hand, rest its spell pool.
     */
    override fun inventoryTick(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        if (entityIn is PlayerEntity) {
            val wandData = WandData(stack)
            if (wandData.cooldown > 0) {
                wandData.cooldown = wandData.cooldown - 1
            }
            if (wandData.mana < wandData.manaMax) {
                wandData.mana = wandData.mana + wandData.manaChargeSpeed / 20f
            }
            if (entityIn.mainHandStack != stack) {
                wandData.resetSpellPool()
            }
        }
    }

    /******************** Client-side info  */
    override fun isItemBarVisible(stack: ItemStack): Boolean {
        return true
    }

    // make it show mana as the durability bar
    override fun getItemBarStep(stack: ItemStack): Int {
        val property = WandData(stack)
        return if (property.manaMax == 0) super.getItemBarStep(stack) else (1.0 - property.mana.toDouble() / property.manaMax.toDouble()).toInt()
    }

    override fun getItemBarColor(stack: ItemStack): Int {
        return Color(51, 188, 247).rgb
    }

    override fun appendTooltip(
        wandStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<Text>,
        flagIn: TooltipContext
    ) {
        val wandPropertyInfo: MutableList<Text> = ArrayList()
        val wandData = WandData(wandStack)
        wandPropertyInfo.add(Text.translatable("desc.noitaWands.wand.shuffle", wandData.isShuffle.toString()))
        wandPropertyInfo.add(Text.translatable("desc.noitaWands.wand.casts", wandData.casts))
        wandPropertyInfo.add(Text.translatable("desc.noitaWands.wand.cast_delay", wandData.castDelay / 20.0))
        wandPropertyInfo.add(Text.translatable("desc.noitaWands.wand.recharge_time", wandData.rechargeTime / 20.0))
        wandPropertyInfo.add(Text.translatable("desc.noitaWands.wand.mana_max", wandData.manaMax))
        wandPropertyInfo.add(Text.translatable("desc.noitaWands.wand.mana_charge_speed", wandData.manaChargeSpeed))
        wandPropertyInfo.add(Text.translatable("desc.noitaWands.wand.capacity", wandData.capacity))
        wandPropertyInfo.add(Text.translatable("desc.noitaWands.wand.spread", wandData.spread))
        tooltip.addAll(wandPropertyInfo)
    }
	
	fun openScreen(player: PlayerEntity, wandItemStack: ItemStack) {
		if (player.world != null && !player.world.isClient) {
			player.openHandledScreen(object : ExtendedScreenHandlerFactory {
				override fun writeScreenOpeningData(serverPlayerEntity: ServerPlayerEntity, packetByteBuf: PacketByteBuf) {
					packetByteBuf.writeItemStack(wandItemStack)
				}
				
				override fun getDisplayName(): Text {
					return Text.translatable(wandItemStack.item.translationKey)
				}
				
				override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler? {
					return WandScreenHandler(syncId, inv, wandItemStack)
				}
			})
		}
	}
}
