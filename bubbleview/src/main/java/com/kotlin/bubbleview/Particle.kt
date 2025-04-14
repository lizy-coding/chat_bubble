package com.kotlin.bubbleview

import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

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
    private val speed = 1f + random.nextFloat() * 2  // 基础速度
    
    /**
     * 粒子破碎效果
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
        // 使用角度实现方向性运动
        val distance = factor * speed * speedFactor * width / 6
        cx += distance * cos(angle)
        cy += distance * sin(angle)
        
        // 添加重力效果
        cy += factor * factor * height / 3
        
        // 添加随机性抖动
        if (random.nextFloat() > 0.7f) {
            cx += (random.nextFloat() - 0.5f) * width / 20 * factor
            cy += (random.nextFloat() - 0.5f) * height / 20 * factor
        }
        
        // 随时间减小粒子大小
        radius = radius * (1f - factor * sizeFactor * 0.7f)
        
        // 透明度渐变效果
        alpha = (1f - factor * alphaFactor) * (1 + random.nextFloat() * 0.3f)
    }
    
    companion object {
        const val PARTICLE_COUNT = 30  // 增加粒子数量，使效果更细腻
    }
}