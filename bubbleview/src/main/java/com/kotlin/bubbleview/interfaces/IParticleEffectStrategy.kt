package com.kotlin.bubbleview.interfaces

import android.graphics.Bitmap
import com.kotlin.bubbleview.Particle

/**
 * 粒子效果策略接口
 * 定义粒子生成和动画行为
 */
interface IParticleEffectStrategy {
    /**
     * 生成粒子
     * @param bitmap 气泡位图
     * @param cx 中心点X坐标
     * @param cy 中心点Y坐标
     * @param radius 气泡半径
     * @return 生成的粒子列表
     */
    fun generateParticles(bitmap: Bitmap, cx: Float, cy: Float, radius: Float): List<Particle>
    
    /**
     * 更新粒子状态
     * @param particles 粒子列表
     * @param animationProgress 动画进度 (0f-1f)
     * @param width 视图宽度
     * @param height 视图高度
     */
    fun updateParticles(particles: List<Particle>, animationProgress: Float, width: Int, height: Int)
    
    /**
     * 设置粒子效果参数
     * @param count 粒子数量
     * @param duration 动画持续时间(毫秒)
     * @param speedFactor 速度因子
     * @param sizeFactor 大小变化因子
     * @param alphaFactor 透明度变化因子
     */
    fun setEffectParams(
        count: Int,
        duration: Int,
        speedFactor: Float,
        sizeFactor: Float,
        alphaFactor: Float
    )
}