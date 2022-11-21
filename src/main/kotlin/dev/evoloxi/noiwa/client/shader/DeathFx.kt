package dev.evoloxi.noiwa.client.shader

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.foundation.component.CustomComponents
import ladysnake.satin.api.event.ShaderEffectRenderCallback
import ladysnake.satin.api.managed.ManagedShaderEffect
import ladysnake.satin.api.managed.ShaderEffectManager
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.MathHelper.clamp
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents
import kotlin.math.exp

object DeathFx {
	//val FISH_EYE_SHADER: ManagedShaderEffect = ShaderEffectManager.getInstance()
	//	.manage(Core.id("shaders/post/color.json"))
	val FISH_EYE_SHADER: ManagedShaderEffect = ShaderEffectManager.getInstance()
		.manage(Core.id("shaders/post/fish_eye.json"))
	
	fun register() {
		ClientTickEvents.START.register(this::render)
		ShaderEffectRenderCallback.EVENT.register(ShaderEffectRenderCallback { shader ->
			val cam = MinecraftClient.getInstance().cameraEntity
			if (cam is LivingEntity && cam.getComponent(CustomComponents.STATS).renderGhost) {
				this.FISH_EYE_SHADER.render(shader)
		}})
	}
	
	fun render(client: MinecraftClient) {
		val cam = client.cameraEntity ?: return
		if (cam is LivingEntity) {
			val world = cam.world
			
			val components = cam.getComponent(CustomComponents.STATS)
			val isGhost = components.isGhost
			val timeOfDeath = components.timeOfDeath
			val timeOfRespawn = components.timeOfRespawn
			val timeSinceRespawn = world.time - timeOfRespawn
			// if player is a ghost, slowly decrease saturation
			/*
			 saturation default: 1f
			 brightness default: 0f
			 distortion default: 0f
			 red default: 1f
			 */
			if (isGhost) {
				val timeSinceDeath = world.time - timeOfDeath
				val saturation = lerp(1f, 0.1f, timeSinceDeath / 120f)
				val brightness = lerp(0.0f, 0.9f, timeSinceDeath / 100f)
				val distortion = easeInOut(0.5f, 0.4f, timeSinceDeath / 200f)
				
				setSaturation(saturation)
				setBrightnessAdjust(brightness)
				distortAmount = distortion
				
			} else if (timeSinceRespawn < 500) {
				// return to normal
				val saturation = lerp(0.1f, 1f, timeSinceRespawn / 200f)
				val brightness = lerp(0.9f, 0f, timeSinceRespawn / 200f)
				val distortion = easeInOut(0.4f, 0.5f, timeSinceRespawn / 200f, 30f, 0.5f)
				
				
				setSaturation(saturation)
				setBrightnessAdjust(brightness)
				distortAmount = distortion
			}
		}
	}
	
	private fun easeInOut(s: Float, e: Float, d: Float, easeIn: Float, easeOut: Float): Float {
		val t = clamp(d, 0f, 1f)
		val t2 = t * t
		val t3 = t2 * t
		val t4 = t3 * t
		val t5 = t4 * t
		return s + (e - s) * (6 * t5 - 15 * t4 + 10 * t3)
	}
	private fun easeInOut(s: Float, e: Float, d: Float): Float {
		return easeInOut(s, e, d, 10f, 10f)
	}
	private fun lerp(start: Float, end: Float, delta: Float): Float {
		return start + (end - start) * clamp(delta, 0f, 1f)
	}
	
	var invisLengthValue = 10
	
	var distortAmount = 0.0f
		set(value) {
			field = (value * 1 * MinecraftClient.getInstance().options.distortionEffectScale.get()).toFloat()
			this.FISH_EYE_SHADER.setUniformValue("Strength", field)
		}
	
	fun setRedAmount(value: Float) = this.FISH_EYE_SHADER.setUniformValue("RedMatrix", value, 0.0f, 0.0f)
	
	
	fun setGreenAmount(value: Float) = this.FISH_EYE_SHADER.setUniformValue("GreenMatrix", 0.0f, value, 0.0f)
	
	
	fun setBlueAmount(value: Float) = this.FISH_EYE_SHADER.setUniformValue("BlueMatrix", 0.0f, 0.0f, value)
	
	
	fun setBrightnessAdjust(value: Float) = this.FISH_EYE_SHADER.setUniformValue("BrightnessAdjust", value)
	
	
	fun setSaturation(value: Float) = this.FISH_EYE_SHADER.setUniformValue("Saturation", value)
	
	fun impulse(k: Float, x: Float): Float {
		val h = k * x
		return (h * exp(1.0 - h)).toFloat()
	}
	
}
