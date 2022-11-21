package dev.evoloxi.noiwa.mixin;

import dev.evoloxi.noiwa.Core;
import dev.evoloxi.noiwa.calculation.Stuff;
import dev.evoloxi.noiwa.content.item.ERarity;
import dev.evoloxi.noiwa.content.particle.generic.GenericParticleEffect;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.evoloxi.noiwa.foundation.registry.AttributeRegistry.getItemRarity;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
	
	private final ItemEntity itemEntity = (ItemEntity) (Object) this;
	private final ItemStack stack = itemEntity.getStack();
	
	@Inject(at = @At("HEAD"), method = "tick")
	public void tick(CallbackInfo info) {
		Stuff.itemEffect(itemEntity, stack);
		
	}
}

