package dev.evoloxi.noiwa.foundation.registry

import dev.evoloxi.noiwa.Core.Companion.id
import dev.evoloxi.noiwa.content.item.*
import dev.evoloxi.noiwa.foundation.spell.ProjectileSpell
import dev.evoloxi.noiwa.foundation.spell.item.SpellItem
import dev.evoloxi.noiwa.foundation.spell.item.wand.WandItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.registry.Registry
import software.bernie.geckolib3.network.GeckoLibNetwork
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer

object ItemRegistry {
	
	val INFINISTEEL: FnSInfinite = FnSInfinite(Item.Settings().group(ItemGroup.TOOLS).maxCount(1))
	val REGULARSTEEL: FnSRegular = FnSRegular(Item.Settings().group(ItemGroup.TOOLS).maxCount(1))
	val LAUNCHER: LauncherItem = LauncherItem(Item.Settings().group(ItemGroup.TOOLS).maxCount(1))
	val RIFLE: RifleItem = RifleItem(Item.Settings().group(ItemGroup.COMBAT).maxCount(1))
	val COIN: CoinItem = CoinItem(Item.Settings().group(ItemGroup.MISC).maxCount(64))
	
	//val MANA_VESSEL: ManaVessel = ManaVessel(Item.Settings().group(ItemGroup.MISC).maxCount(1))
	val AMMO: StonerAmmoItem = StonerAmmoItem(Item.Settings().group(ItemGroup.COMBAT).maxCount(48))
	val CHAINGUN: ChaingunItem = ChaingunItem(Item.Settings().group(ItemGroup.COMBAT).maxCount(1))
	val BRAIN_WOOD: Item = Item(Item.Settings().group(ItemGroup.MISC).maxCount(64))
	val BRAIN_GOLD: Item = Item(Item.Settings().group(ItemGroup.MISC).maxCount(64))
	val BRAIN_MOSS: Item = Item(Item.Settings().group(ItemGroup.MISC).maxCount(64))
	val ELIXIR_OF_HARMING: ElixirHarmingItem = ElixirHarmingItem(Item.Settings().group(ItemGroup.MISC).maxCount(16))
	val LAVA_BALL: LavaballSpellItem = LavaballSpellItem(Item.Settings().group(ItemGroup.MISC).maxCount(1))
	
	@JvmField
	val WAND: WandItem = WandItem(Item.Settings().group(ItemGroup.MISC).maxCount(1))
	
	@JvmField
	val SPELL_SPARK_BOLT: SpellItem = SpellItem(Item.Settings().group(ItemGroup.MISC).maxCount(1), ProjectileSpell.SPARK_BOLT)
	
	@JvmField
	val SPARK_BOLT_TIMER: SpellItem = SpellItem(Item.Settings().group(ItemGroup.MISC).maxCount(1), ProjectileSpell.SPARK_BOLT_TIMER)
	
	@JvmField
	val SPARK_BOLT_TRIGGER: SpellItem = SpellItem(Item.Settings().group(ItemGroup.MISC).maxCount(1), ProjectileSpell.SPARK_BOLT_TRIGGER)
	@JvmField
	val SPARK_BOLT_TRIGGER_DOUBLE: SpellItem = SpellItem(Item.Settings().group(ItemGroup.MISC).maxCount(1), ProjectileSpell.SPARK_BOLT_TRIGGER_DOUBLE)
	@JvmField
	val BOMB: SpellItem = SpellItem(Item.Settings().group(ItemGroup.MISC).maxCount(1), ProjectileSpell.BOMB)
	@JvmField
	val ENERGY_SPHERE: SpellItem = SpellItem(Item.Settings().group(ItemGroup.MISC).maxCount(1), ProjectileSpell.ENERGY_SPHERE)
	@JvmField
	val ENERGY_SPHERE_TIMER: SpellItem = SpellItem(Item.Settings().group(ItemGroup.MISC).maxCount(1), ProjectileSpell.ENERGY_SPHERE_TIMER)
	
	fun register() {
		Registry.register(Registry.ITEM, id("infinitesteel"), INFINISTEEL)
		Registry.register(Registry.ITEM, id("regularsteel"), REGULARSTEEL)
		Registry.register(Registry.ITEM, id("launcher"), LAUNCHER)
		GeckoLibNetwork.registerSyncable(LAUNCHER)
		GeckoLibNetwork.registerSyncable(RIFLE)
		Registry.register(Registry.ITEM, id("coin"), COIN)
		//Registry.register(Registry.ITEM, id("mana_vessel"), MANA_VESSEL)
		Registry.register(Registry.ITEM, id("stoner_ammo"), AMMO)
		Registry.register(Registry.ITEM, id("chaingun"), CHAINGUN)
		Registry.register(Registry.ITEM, id("brain_wood"), BRAIN_WOOD)
		Registry.register(Registry.ITEM, id("brain_gold"), BRAIN_GOLD)
		Registry.register(Registry.ITEM, id("brain_moss"), BRAIN_MOSS)
		Registry.register(Registry.ITEM, id("rifle"), RIFLE)
		Registry.register(Registry.ITEM, id("elixir_of_harming"), ELIXIR_OF_HARMING)
		Registry.register(Registry.ITEM, id("lava_ball"), LAVA_BALL)
		Registry.register(Registry.ITEM, id("wand"), WAND)
		Registry.register(Registry.ITEM, id("spark_bolt"), SPELL_SPARK_BOLT)
		Registry.register(Registry.ITEM, id("spark_bolt_timer"), SPARK_BOLT_TIMER)
		Registry.register(Registry.ITEM, id("spark_bolt_trigger"), SPARK_BOLT_TRIGGER)
		Registry.register(Registry.ITEM, id("spark_bolt_trigger_double"), SPARK_BOLT_TRIGGER_DOUBLE)
		Registry.register(Registry.ITEM, id("bomb"), BOMB)
		Registry.register(Registry.ITEM, id("energy_sphere"), ENERGY_SPHERE)
		Registry.register(Registry.ITEM, id("energy_sphere_timer"), ENERGY_SPHERE_TIMER)
	}
	
	fun registerClient() {
		GeoItemRenderer.registerItemRenderer(RIFLE, RifleRenderer())
		GeoItemRenderer.registerItemRenderer(LAUNCHER, LauncherRenderer())
		
	}
}
