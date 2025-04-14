package com.kotlin.bubbleview.controllers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.view.MotionEvent
import com.kotlin.bubbleview.Circle
import com.kotlin.bubbleview.Point
import com.kotlin.bubbleview.interfaces.IParticleEffectStrategy
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 气泡控制器
 * 负责气泡拖拽逻辑和状态控制
 */
class BubbleController {
    // 两个圆：起始圆和移动圆
    private lateinit var startCircle: Circle
    private lateinit var endCircle: Circle
    
    // 贝塞尔曲线控制点
    private val startCircleA = Point()
    private val startCircleB = Point()
    private val endCircleC = Point()
    private val endCircleD = Point()
    private val quadControlE = Point()
    
    // 路径对象
    private val path = Path()
    
    // 状态控制
    private var canDrawPath = true
    private var canDrawParticle = false
    
    // 距离相关
    private var circleCenterDistant = 0f
    private var lastDistant = 0f
    
    // 断开连接的临界距离因子
    private var breakDistanceFactor = 5.0f
    
    // 粒子效果策略
    private var particleEffectStrategy: IParticleEffectStrategy? = null
    
    // 尺寸参数
    private var viewWidth = 0
    private var viewHeight = 0
    
    // 监听器接口
    interface OnBubbleStateChangeListener {
        fun onBubbleBreak()
        fun onAnimationEnd()
    }
    
    // 状态变化监听器
    private var stateChangeListener: OnBubbleStateChangeListener? = null
    
    /**
     * 初始化
     */
    fun init(width: Int, height: Int) {
        viewWidth = width
        viewHeight = height
        startCircle = Circle(width / 2f, height / 2f, width / 2f)
        endCircle = Circle(width / 2f, height / 2f, height / 2f)
        canDrawPath = true
        canDrawParticle = false
    }
    
    /**
     * 设置粒子效果策略
     */
    fun setParticleEffectStrategy(strategy: IParticleEffectStrategy) {
        this.particleEffectStrategy = strategy
    }
    
    /**
     * 设置状态变化监听器
     */
    fun setStateChangeListener(listener: OnBubbleStateChangeListener) {
        this.stateChangeListener = listener
    }
    
    /**
     * 设置断开连接的临界距离因子
     */
    fun setBreakDistanceFactor(factor: Float) {
        this.breakDistanceFactor = factor
    }
    
    /**
     * 获取断开连接的临界距离因子
     */
    fun getBreakDistanceFactor(): Float {
        return breakDistanceFactor
    }
    
    /**
     * 处理触摸事件
     * @return 是否生成了粒子效果
     */
    fun handleTouchEvent(event: MotionEvent, createBitmapCallback: () -> Bitmap): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 触摸开始，不做特殊处理
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                // 移动时更新终点圆位置并计算路径
                endCircle.set(event.x, event.y)
                computePath()
                lastDistant = circleCenterDistant
                return false
            }
            MotionEvent.ACTION_UP -> {
                // 触摸结束，检查是否应该触发粒子效果
                if (!canDrawPath) {
                    // 路径断开，生成粒子
                    stateChangeListener?.onBubbleBreak()
                    return true
                } else {
                    // 路径未断开，恢复原状
                    endCircle.set(startCircle.x, startCircle.y)
                    startCircle.radius = endCircle.radius
                    computePath()
                    return false
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                // 触摸取消，恢复原状
                if (canDrawPath) {
                    endCircle.set(startCircle.x, startCircle.y)
                    startCircle.radius = endCircle.radius
                    computePath()
                }
                return false
            }
            else -> return false
        }
    }
    
    /**
     * 计算路径
     */
    fun computePath() {
        val startX = startCircle.x
        val startY = startCircle.y
        val endX = endCircle.x
        val endY = endCircle.y
        
        // 计算圆心的距离
        circleCenterDistant = sqrt((startX - endX).pow(2) + (startY - endY).pow(2))
        
        // 如果圆心的距离大于临界值或者起点圆半径小于等于原来半径的1/5
        if (circleCenterDistant > endCircle.radius * breakDistanceFactor || startCircle.radius <= endCircle.radius / 5) {
            canDrawPath = false
            return
        }
        
        // 计算起点圆的半径
        if (circleCenterDistant > endCircle.radius) {
            startCircle.radius -= (circleCenterDistant - lastDistant) / 5
        }
        
        // 计算连接点
        val cos = (endY - startY) / circleCenterDistant
        val sin = (endX - startX) / circleCenterDistant
        
        // 计算控制点
        startCircleA.set(
            startX - startCircle.radius * cos,
            startY + startCircle.radius * sin
        )
        
        startCircleB.set(
            startX + startCircle.radius * cos,
            startY - startCircle.radius * sin
        )
        
        endCircleC.set(
            endX + endCircle.radius * cos,
            endY - endCircle.radius * sin
        )
        
        endCircleD.set(
            endX - endCircle.radius * cos,
            endY + endCircle.radius * sin
        )
        
        quadControlE.set(
            startX - (startX - endX) / 2,
            startY + (endY - startY) / 2
        )
    }
    
    /**
     * 绘制路径
     */
    fun drawPath(canvas: Canvas, drawPathCallback: (Path) -> Unit, drawCircleCallback: (Circle) -> Unit) {
        if (canDrawPath) {
            path.reset()
            path.moveTo(startCircleA.x, startCircleA.y)
            path.lineTo(startCircleB.x, startCircleB.y)
            path.quadTo(quadControlE.x, quadControlE.y, endCircleC.x, endCircleC.y)
            path.lineTo(endCircleD.x, endCircleD.y)
            path.quadTo(quadControlE.x, quadControlE.y, startCircleA.x, startCircleA.y)
            
            drawPathCallback(path)
            drawCircleCallback(startCircle)
        }
        
        if (!canDrawParticle) {
            drawCircleCallback(endCircle)
        }
    }
    
    /**
     * 获取终点圆
     */
    fun getEndCircle(): Circle {
        return endCircle
    }
    
    /**
     * 能否绘制路径
     */
    fun canDrawPath(): Boolean {
        return canDrawPath
    }
    
    /**
     * 能否绘制粒子
     */
    fun canDrawParticle(): Boolean {
        return canDrawParticle
    }
    
    /**
     * 设置粒子绘制状态
     */
    fun setParticleDrawState(state: Boolean) {
        canDrawParticle = state
    }
    
    /**
     * 清除状态
     */
    fun clearStatus() {
        path.reset()
        canDrawPath = true
        canDrawParticle = false
        endCircle.set(viewWidth / 2f, viewHeight / 2f)
        startCircle.set(viewWidth / 2f, viewHeight / 2f)
        startCircle.radius = viewWidth / 2f
        circleCenterDistant = 0f
        computePath()
    }
    
    /**
     * 通知动画结束
     */
    fun notifyAnimationEnd() {
        stateChangeListener?.onAnimationEnd()
    }
}