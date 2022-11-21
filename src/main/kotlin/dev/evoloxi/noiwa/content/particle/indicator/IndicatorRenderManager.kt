package dev.evoloxi.noiwa.content.particle.indicator

object IndicatorRenderManager {
    @JvmField
    var PARTICLES: MutableList<IndicatorParticle> = ArrayList()
    @JvmStatic
    fun tick() {
        val i = PARTICLES.iterator()
        while (i.hasNext()) {
            val p = i.next()
            if (p.age > 18) i.remove() else p.tick()
        }
    }
}
