package com.kotlin.bubbleview

import java.util.Random
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.pow

/**
 * 颗粒类
 */
data class Particle(
    var cx: Float,
    var cy: Float,
    var radius: Float,
    var color: Int
) {
    var alpha: Float = 1f
    private val random = Random()
    
    // 随机运动方向和速度
    private val angle = random.nextFloat() * Math.PI.toFloat() * 2
    private val speed = 1.5f + random.nextFloat() * 3  // 增加基础速度
    
    // 随机旋转方向和速度
    private val rotationSpeed = (random.nextFloat() - 0.5f) * 0.2f

    /**
     * 粒子破碎效果 - 增强版
     * @param factor 动画进度因子(0-1)
     * @param width 视图宽度
     * @param height 视图高度
     * @param speedFactor 速度因子
     * @param sizeFactor 大小变化因子
     * @param alphaFactor 透明度变化因子
     */
    fun broken(
        factor: Float, 
        width: Int, 
        height: Int,
        speedFactor: Float = 1.0f,
        sizeFactor: Float = 1.0f,
        alphaFactor: Float = 1.0f
    ) {
        // 计算非线性的加速运动
        val acceleratedFactor = factor.pow(1.3f)
        
        // 使用角度实现方向性运动，增加非线性加速度
        val distance = acceleratedFactor * speed * speedFactor * width / 5
        cx += distance * cos(angle)
        cy += distance * sin(angle)
        
        // 增强重力效果
        cy += factor.pow(2f) * height / 2.5f
        
        // 添加随机漂移效果，使粒子行为更加自然
        if (factor > 0.3f) {
            val drift = cos(factor * 10) * width * 0.01f * (1 - factor)
            cx += drift * (random.nextFloat() - 0.5f)
        }
        
        // 增加随机性抖动，提高频率
        if (random.nextFloat() > 0.6f) {
            cx += (random.nextFloat() - 0.5f) * width / 15 * factor
            cy += (random.nextFloat() - 0.5f) * height / 15 * factor
        }
        
        // 随时间减小粒子大小，保留更大的核心
        radius = radius * (1f - factor.pow(1.2f) * sizeFactor * 0.6f)
        
        // 透明度渐变效果 - 更微妙的变化
        val baseAlpha = (1f - factor.pow(0.8f) * alphaFactor)
        
        // 在爆炸初期增加透明度波动，创造闪烁感
        if (factor < 0.4f) {
            val pulse = (sin(factor * 20) * 0.1f + 0.9f)
            alpha = baseAlpha * pulse * (1 + random.nextFloat() * 0.3f)
        } else {
            alpha = baseAlpha * (1 + random.nextFloat() * 0.3f)
        }
        
        // 限制透明度范围
        alpha = alpha.coerceIn(0f, 1f)
    }
    
    companion object {
        const val PARTICLE_COUNT = 18  // 粒子数量，使效果更细腻
    }
}