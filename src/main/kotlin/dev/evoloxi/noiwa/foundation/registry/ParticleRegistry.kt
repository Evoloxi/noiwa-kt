package dev.evoloxi.noiwa.foundation.registry

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.content.particle.cube.CubeParticle
import dev.evoloxi.noiwa.content.particle.EchoParticle
import dev.evoloxi.noiwa.content.particle.generic.GenericParticleEffect
import dev.evoloxi.noiwa.content.particle.SparkleParticle
import dev.evoloxi.noiwa.content.particle.generic.GenericParticle
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.SpriteProvider
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleType
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object ParticleRegistry {
	
	// sparkles
	private val factoryRegistry: ParticleFactoryRegistry = ParticleFactoryRegistry.getInstance()
	
	private val SPARKLE_ID = Identifier(Core.MOD_ID, "sparkle")
	private val ECHO_ID = Identifier(Core.MOD_ID, "echo")
	private val CUBE_ID = Identifier(Core.MOD_ID, "cube")
	private val GENERIC_ID = Identifier(Core.MOD_ID, "generic")
	val SPARKLE: DefaultParticleType = FabricParticleTypes.simple()
	val ECHO: DefaultParticleType = FabricParticleTypes.simple()
	val CUBE: DefaultParticleType = FabricParticleTypes.simple()
	val GENERIC: ParticleType<GenericParticleEffect> = FabricParticleTypes.complex(true, GenericParticleEffect.FACTORY)
	fun register() {
		shortReg(SPARKLE_ID, SPARKLE)
		shortReg(ECHO_ID, ECHO)
		
		// cube
		shortReg(CUBE_ID, CUBE)
		Registry.register(Registry.PARTICLE_TYPE, GENERIC_ID, GENERIC)
	}
	
	private fun shortReg(identifier: Identifier, particleType: DefaultParticleType) {
		Registry.register(Registry.PARTICLE_TYPE, identifier, particleType)
	}
	
	@JvmStatic
	fun registerClient() {
		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(ClientSpriteRegistryCallback { _, registry ->
			registry.register(SPARKLE_ID)
			registry.register(ECHO_ID)
			registry.register(CUBE_ID)
			registry.register(GENERIC_ID)
		})
		
		factoryRegistry.register( SPARKLE ) { spriteProvider: SpriteProvider -> SparkleParticle.Factory(spriteProvider) }
		factoryRegistry.register( ECHO ) { spriteProvider: SpriteProvider -> EchoParticle.Factory(spriteProvider) }
		factoryRegistry.register( CUBE ) { spriteProvider: SpriteProvider -> CubeParticle.Factory(spriteProvider) }
		factoryRegistry.register( GENERIC ) {
			provider: SpriteProvider ->
			ParticleFactory { particleEffect: GenericParticleEffect, clientWorld: ClientWorld, x: Double, y: Double, z: Double, dx: Double, dy: Double, dz: Double ->
				GenericParticle(clientWorld, x, y, z, dx, dy, dz, false, particleEffect, provider)
			}
		}
		//factoryRegistry.register( CUBE, CubeParticle.Factory() )
//		factoryRegistry.register( CUBE ) { provider: SpriteProvider ->
//			ParticleFactory { parameters: ParticleEffect, world: ClientWorld, x: Double, y: Double, z: Double, velocityX: Double, velocityY: Double, velocityZ: Double ->
//				CubeParticle(
//					world,
//					x,
//					y,
//					z,
//					velocityX,
//					velocityY,
//					velocityZ
//				)
//			}
//		}
	}
}

