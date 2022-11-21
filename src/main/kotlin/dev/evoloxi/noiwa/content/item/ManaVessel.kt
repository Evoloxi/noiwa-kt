//package dev.evoloxi.noiwa.content.item
//
//import com.google.common.collect.Multimap
//import dev.emi.trinkets.api.SlotAttributes
//import dev.emi.trinkets.api.SlotReference
//import dev.emi.trinkets.api.TrinketItem
//import net.minecraft.entity.LivingEntity
//import net.minecraft.entity.attribute.EntityAttribute
//import net.minecraft.entity.attribute.EntityAttributeModifier
//import net.minecraft.item.ItemStack
//import java.util.*
//
//class ManaVessel(settings: Settings) : TrinketItem(settings) {
//
//    override fun getModifiers(
//        stack: ItemStack?,
//        slot: SlotReference?,
//        entity: LivingEntity?,
//        uuid: UUID?
//    ): Multimap<EntityAttribute, EntityAttributeModifier> {
//        val modifiers = super.getModifiers(stack, slot, entity, uuid)
//        SlotAttributes.addSlotModifier(modifiers, "hand/ring", uuid, 2.0, EntityAttributeModifier.Operation.ADDITION)
//        return modifiers
//    }
//
//
//
//}
