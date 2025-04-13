package com.kotlin.bubbleview

import java.util.Random

/**
 * 颗粒类
 * 使用Kotlin完成颗粒效果
 */
data class Particle(
    var cx: Float,
    var cy: Float,
    var radius: Float,
    var color: Int
) {
    var alpha: Float = 1f
    private val random = Random()
    
    /**
     * 粒子破碎效果
     */
    fun broken(factor: Float, width: Int, height: Int) {
        cx += factor * random.nextInt(width) * (random.nextFloat() - 0.5f)
        cy += factor * random.nextInt(height / 2)
        radius -= factor * random.nextInt(2)
        alpha = (1f - factor) * (1 + random.nextFloat())
    }
    
    companion object {
        const val PARTICLE_COUNT = 10
    }
}