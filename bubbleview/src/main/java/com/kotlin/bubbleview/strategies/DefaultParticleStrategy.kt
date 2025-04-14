package com.kotlin.bubbleview.strategies

import android.graphics.Bitmap
import com.kotlin.bubbleview.Particle
import com.kotlin.bubbleview.interfaces.IParticleEffectStrategy
import androidx.core.graphics.get
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.pow

/**
 * 默认粒子效果策略实现
 * 提供增强型爆炸效果动画
 */
class DefaultParticleStrategy : IParticleEffectStrategy {
    // 粒子效果参数
    private var particleCount = Particle.PARTICLE_COUNT
    private var durationMillis = 1500
    private var speedFactor = 1.5f  // 默认值增加为1.5，让粒子飞得更远
    private var sizeFactor = 0.8f   // 默认值降低为0.8，让粒子大小变化更缓慢
    private var alphaFactor = 1.2f  // 默认值增加为1.2，让粒子消失更快一些
    
    // 随机数生成器
    private val random = Random()
    
    /**
     * 生成粒子
     */
    override fun generateParticles(bitmap: Bitmap, cx: Float, cy: Float, radius: Float): List<Particle> {
        val particles = mutableListOf<Particle>()
        
        val count = particleCount
        // 粒子直径略微调大，使粒子更加明显
        val particleRadius = radius * 2.2f / count / 2
        val bitmapWidth = bitmap.width
        val bitmapHeight = bitmap.height
        
        // 在生成粒子时加入一些随机性，不再严格遵循网格布局
        for (i in 0 until count) {
            for (j in 0 until count) {
                // 为每个粒子增加少量随机偏移
                val randomOffsetX = (random.nextFloat() - 0.5f) * particleRadius
                val randomOffsetY = (random.nextFloat() - 0.5f) * particleRadius
                
                val x = cx - radius + i * particleRadius * 2 + randomOffsetX
                val y = cy - radius + j * particleRadius * 2 + randomOffsetY
                
                // 确保索引在有效范围内
                val pixelX = (bitmapWidth / count * i).coerceIn(0, bitmapWidth - 1)
                val pixelY = (bitmapHeight / count * j).coerceIn(0, bitmapHeight - 1)
                
                // 获取像素颜色
                val color = bitmap[pixelX, pixelY]
                
                // 创建粒子并添加到列表，根据距离中心的远近设置不同的初始大小
                val distanceFromCenter = Math.sqrt(
                    Math.pow((x - cx).toDouble(), 2.0) + 
                    Math.pow((y - cy).toDouble(), 2.0)
                ).toFloat()
                
                // 边缘粒子稍大一些
                val sizeMultiplier = 1.0f + (distanceFromCenter / radius) * 0.3f
                
                val particle = Particle(x, y, particleRadius * sizeMultiplier, color)
                
                // 根据位置给粒子一个初始透明度
                if (random.nextFloat() > 0.85f) {
                    // 随机让一些粒子初始就半透明，增加多样性
                    particle.alpha = 0.7f + random.nextFloat() * 0.3f
                }
                
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
     * 更新单个粒子 - 增强版动画效果
     */
    private fun updateParticle(
        particle: Particle, 
        factor: Float, 
        width: Int, 
        height: Int
    ) {
        // 添加非线性变化，让动画更有弹性
        val adjustedFactor = factor.pow(0.8f) // 非线性变化，使动画初期更快
        
        // 创建爆炸波动动画效果
        val waveEffect = sin(adjustedFactor * 6 * Math.PI.toFloat()) * (1 - adjustedFactor) * width * 0.02f
        
        // 使用调整后的因子，增强运动效果
        particle.broken(
            adjustedFactor,
            width,
            height,
            speedFactor * (1 + random.nextFloat() * 0.5f), // 随机增加一些速度变化
            sizeFactor,
            alphaFactor
        )
        
        // 应用额外的波动效果
        particle.cx += waveEffect * (random.nextFloat() - 0.5f)
        particle.cy += waveEffect * (random.nextFloat() - 0.5f)
        
        // 对粒子大小施加额外的脉动效果
        if (adjustedFactor < 0.6f) {
            val pulseEffect = sin(adjustedFactor * 12 * Math.PI.toFloat()) * 0.15f + 1.0f
            particle.radius *= pulseEffect
        }
        
        // 随着时间推移增加更多随机性
        if (random.nextFloat() > 0.9f && adjustedFactor > 0.4f) {
            // 偶尔的随机抖动
            particle.cx += (random.nextFloat() - 0.5f) * width * 0.03f * adjustedFactor
            particle.cy += (random.nextFloat() - 0.5f) * height * 0.03f * adjustedFactor
        }
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