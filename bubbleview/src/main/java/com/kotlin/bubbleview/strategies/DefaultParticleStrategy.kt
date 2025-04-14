package com.kotlin.bubbleview.strategies

import android.graphics.Bitmap
import com.kotlin.bubbleview.Particle
import com.kotlin.bubbleview.interfaces.IParticleEffectStrategy
import androidx.core.graphics.get
import java.util.Random
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 默认粒子效果策略实现
 * 提供基本的爆炸效果动画
 */
class DefaultParticleStrategy : IParticleEffectStrategy {
    // 粒子效果参数
    private var particleCount = Particle.PARTICLE_COUNT
    private var durationMillis = 1500
    private var speedFactor = 1.0f
    private var sizeFactor = 1.0f
    private var alphaFactor = 1.0f
    
    // 随机数生成器
    private val random = Random()
    
    /**
     * 生成粒子
     */
    override fun generateParticles(bitmap: Bitmap, cx: Float, cy: Float, radius: Float): List<Particle> {
        val particles = mutableListOf<Particle>()
        
        val count = particleCount
        val particleRadius = radius * 2 / count / 2
        val bitmapWidth = bitmap.width
        val bitmapHeight = bitmap.height
        
        for (i in 0 until count) {
            for (j in 0 until count) {
                val x = cx - radius + i * particleRadius * 2
                val y = cy - radius + j * particleRadius * 2
                
                // 确保索引在有效范围内
                val pixelX = (bitmapWidth / count * i).coerceIn(0, bitmapWidth - 1)
                val pixelY = (bitmapHeight / count * j).coerceIn(0, bitmapHeight - 1)
                
                // 获取像素颜色
                val color = bitmap[pixelX, pixelY]
                
                // 创建粒子并添加到列表
                val particle = Particle(x, y, particleRadius, color)
                particles.add(particle)
            }
        }
        
        return particles
    }
    
    /**
     * 更新粒子状态
     */
    override fun updateParticles(
        particles: List<Particle>, 
        animationProgress: Float, 
        width: Int, 
        height: Int
    ) {
        particles.forEach { particle ->
            updateParticle(particle, animationProgress, width, height)
        }
    }
    
    /**
     * 更新单个粒子
     */
    private fun updateParticle(
        particle: Particle, 
        factor: Float, 
        width: Int, 
        height: Int
    ) {
        particle.broken(
            factor,
            width,
            height,
            speedFactor,
            sizeFactor,
            alphaFactor
        )
    }
    
    /**
     * 设置粒子效果参数
     */
    override fun setEffectParams(
        count: Int,
        duration: Int,
        speedFactor: Float,
        sizeFactor: Float,
        alphaFactor: Float
    ) {
        this.particleCount = count
        this.durationMillis = duration
        this.speedFactor = speedFactor
        this.sizeFactor = sizeFactor
        this.alphaFactor = alphaFactor
    }
    
    /**
     * 获取动画持续时间
     */
    fun getDuration(): Int {
        return durationMillis
    }
}