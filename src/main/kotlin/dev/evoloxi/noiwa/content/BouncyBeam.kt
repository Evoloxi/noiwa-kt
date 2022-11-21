package dev.evoloxi.noiwa.content

import dev.evoloxi.noiwa.Core
import dev.evoloxi.noiwa.foundation.Extensions.reflect
import dev.evoloxi.noiwa.content.particle.generic.GenericParticleEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.entity.Entity
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3f
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
import java.awt.Color

class BouncyBeam constructor(world: World, start: Vec3d, vec3d: Vec3d, entity: Entity) {
	var maxDistance = 30.0
	var world: World
	var start: Vec3d
	var vec3d: Vec3d
	var bounces = 10
	var distanceReduction = 0.0
	var entity: Entity
	var spread = 0.0
	var delay = 0L
	
	init {
		this.world = world
		this.start = start
		this.vec3d = vec3d
		this.entity = entity
	}
	
	fun create() {
		if (spread != 0.0) vec3d = vec3d.multiply(
			Core.RAND.nextDouble(-spread, spread) + 1.0,
			Core.RAND.nextDouble(-spread, spread) + 1.0,
			Core.RAND.nextDouble(-spread, spread) + 1.0
		)
		
		val raycast: BlockHitResult = world.raycast(
			RaycastContext(
				start,
				start.add(vec3d.multiply(maxDistance)),
				RaycastContext.ShapeType.VISUAL,
				RaycastContext.FluidHandling.NONE,
				entity
			)
		)
		
		val hitPos = raycast.pos
		
		// draw particle line every 0.1 blocks
		val distance = start.distanceTo(hitPos)
		val step = 0.2
		val steps = (distance / step).toInt()

/*			for (i in 0..steps) {
//				val p = start.add(vec3d.multiply(step * i))
//				// add particle, rainbow color
//				val rawHSB = Color.getHSBColor((i.toFloat() * 0.033f), 0.9f, 1.0f)
//				val color = Vec3f(rawHSB.red.toFloat() / 255.0f, rawHSB.green.toFloat() / 255.0f, rawHSB.blue.toFloat() / 255.0f)
//				world.addParticle(
//					GenericParticleEffect(
//						Vec3f(1f, 1f, 1f),
//						Vec3f(0.2392156f,0.2392156f, 0.2392156f),
//						1f
//					),
//					p.x,
//					p.y,
//					p.z,
//					0.0,
//					0.0,
//					0.0
//				)
			}*/
		
		// spiral of particles around vec3d, using Quaternion to rotate the vector
		val q = Quaternion(Vec3f(0f, 1f, 0f), 360f, true)
		val q2 = Quaternion(Vec3f(1f, 0f, 0f), 360f, true)
		val q3 = Quaternion(Vec3f(0f, 0f, 1f), 360f, true)
		
		val particleCount = 100
		val particleStep = 360f / particleCount
		val particleRadius = 0.5
		
		for (i in 0..particleCount) {
			val p = start.add(vec3d.multiply(step * steps))
			val p2 = p.add(Vec3d(q.x.toDouble(), q.y.toDouble(), q.z.toDouble()).multiply(particleRadius))
			val p3 = p2.add(Vec3d(q2.x.toDouble(), q2.y.toDouble(), q2.z.toDouble()).multiply(particleRadius))
			val p4 = p3.add(Vec3d(q3.x.toDouble(), q3.y.toDouble(), q3.z.toDouble()).multiply(particleRadius))
			
			// add particle, rainbow color
			val rawHSB = Color.getHSBColor((i.toFloat() * 0.033f), 0.9f, 1.0f)
			val color = Vec3f(rawHSB.red.toFloat() / 255.0f, rawHSB.green.toFloat() / 255.0f, rawHSB.blue.toFloat() / 255.0f)
			world.addParticle(
				GenericParticleEffect(
					Vec3f(1f, 1f, 1f),
					Vec3f(0.2392156f,0.2392156f, 0.2392156f),
					1f
				),
				p4.x,
				p4.y,
				p4.z,
				0.0,
				0.0,
				0.0
			)
			
			q.hamiltonProduct(Quaternion(Vec3f(0f, 1f, 0f), particleStep, true))
			q2.hamiltonProduct(Quaternion(Vec3f(1f, 0f, 0f), particleStep, true))
			q3.hamiltonProduct(Quaternion(Vec3f(0f, 0f, 1f), particleStep, true))
		}
		
		if (raycast.type == HitResult.Type.BLOCK) {
			if (bounces > 0 && distance > 0.1 && distance < maxDistance) {
				val normal = Vec3d(raycast.side.unitVector)
				val bounceVec = vec3d.reflect(normal)
				val bounce = BouncyBeam(
					world = world,
					start = hitPos,
					vec3d = bounceVec,
					entity = entity
				)
				bounce.delay = delay
				bounce.bounces = bounces - 1
				bounce.maxDistance = maxDistance - distanceReduction
				val new = CoroutineScope(Dispatchers.Default).launch {
					delay(delay)
					bounce.create()
				}
				new.start()
				
				//bounce.create()
			}
		}
	}
}
