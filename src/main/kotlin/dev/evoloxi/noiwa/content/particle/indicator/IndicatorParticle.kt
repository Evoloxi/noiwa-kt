package dev.evoloxi.noiwa.content.particle.indicator

import dev.evoloxi.noiwa.Core
import net.minecraft.client.MinecraftClient
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class IndicatorParticle(location: Vec3d, eWidth: Float, eHeight: Float, val txt: Text, itype: Byte) {
	val itype: Byte
	//var txt: MutableText
	var x: Double
	var y: Double
	var z: Double
	var xPrev: Double
	var yPrev: Double
	var zPrev: Double
	var age: Int
	var ax = 0.00
	var ay = 0.00
	var az = 0.00
	var vx = 0.00
	var vy = 0.00
	var vz = 0.00
	
	init {
		this.itype = itype
		val client = MinecraftClient.getInstance()
		val entityLocation = location.add(0.0, (eHeight / 1.8), 0.0)
		val cameraLocation = client.gameRenderer.camera.pos
		
		age = 0
		// offset to the side of the entity
		//Vec3d offset = cameraLocation.subtract(entityLocation).normalize().multiply(offsetBy);
		var pos = entityLocation
		val offsetBy = eWidth.toDouble()
		
		val dir = entityLocation.subtract(cameraLocation).normalize()
		
		var a = atan2(dir.getZ(), dir.getX())
		a += Math.PI / 2 * (Core.RAND.nextDouble() - .5)
		
		pos = pos.add(
			cos(a) * -0.7 + Core.RAND.nextDouble(-0.1, 0.1),
			Core.RAND.nextDouble(0.0, 0.9),
			sin(a) * -0.7 + Core.RAND.nextDouble(-0.1, 0.1)
		)
		// offset by entity width
		pos = pos.add(dir.multiply(-offsetBy * 0.5 + 0.2))
		
		
		x = pos.x
		y = pos.y
		z = pos.z
		xPrev = x
		yPrev = y
		zPrev = z
		
		
		if (itype == 1.toByte()) {
			// velocity away from entity
			vx = Core.RAND.nextGaussian(-0.02, 0.02)
			vy = 0.05 + Core.RAND.nextGaussian(0.0, 0.005)
			vz = Core.RAND.nextGaussian(-0.02, 0.02)
			ay = -0.005
		}
	}
	
	fun tick() {
		xPrev = x
		yPrev = y
		zPrev = z
		age++
		x += vx
		y += vy
		z += vz
		vx += ax
		vy += ay
		vz += az
	}
}
