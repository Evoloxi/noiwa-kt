package dev.evoloxi.noiwa.calculation

import net.minecraft.block.Block
import net.minecraft.util.math.BlockPos

class RegeneratingBlock(// create a list of blocks that regenerate into something else
    val blockData: Block, val location: BlockPos, val regeneratingBlock: Block
) {
    private val date = System.currentTimeMillis()
    val isTimedOut: Boolean
        get() = date + 2 * 50 < System.currentTimeMillis()
}
