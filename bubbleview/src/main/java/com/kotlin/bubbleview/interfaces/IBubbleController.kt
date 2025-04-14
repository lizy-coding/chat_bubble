package com.kotlin.bubbleview.interfaces

import android.view.MotionEvent
import com.kotlin.bubbleview.controllers.BubbleController

/**
 * 气泡控制器接口
 * 定义气泡控制器的行为
 */
interface IBubbleController {
    /**
     * 初始化控制器
     * @param width 视图宽度
     * @param height 视图高度
     */
    fun init(width: Int, height: Int)
    
    /**
     * 设置粒子效果策略
     * @param strategy 粒子效果策略
     */
    fun setParticleEffectStrategy(strategy: IParticleEffectStrategy)
    
    /**
     * 设置状态变化监听器
     * @param listener 状态变化监听器
     */
    fun setStateChangeListener(listener: BubbleController.OnBubbleStateChangeListener)
    
    /**
     * A设置断开连接的临界距离因子
     * @param factor 临界距离因子
     */
    fun setBreakDistanceFactor(factor: Float)
    
    /**
     * 处理触摸事件
     * @param event 触摸事件
     * @param createBitmapCallback 创建位图的回调
     * @return 是否生成了粒子效果
     */
    fun handleTouchEvent(event: MotionEvent, createBitmapCallback: () -> android.graphics.Bitmap): Boolean
    
    /**
     * 计算路径
     */
    fun computePath()
    
    /**
     * 清除状态
     */
    fun clearStatus()
}