package dev.evoloxi.noiwa.foundation.util

import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

object MethHelper {
    fun reflectByAxis(vec3d: Vec3d, axis: Direction.Axis): Vec3d {
        return when (axis) {
            Direction.Axis.X -> Vec3d(-vec3d.x, vec3d.y, vec3d.z)
            Direction.Axis.Y -> Vec3d(vec3d.x, -vec3d.y, vec3d.z)
            Direction.Axis.Z -> Vec3d(vec3d.x, vec3d.y, -vec3d.z)
        }
    }
}
