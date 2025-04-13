package com.kotlin.bubbleview

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.math.pow
import kotlin.math.sqrt
import androidx.core.graphics.get

/**
 * 气泡视图
 */
class BubbleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 贝塞尔曲线的path
    private val path = Path()
    
    // 画笔
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val particlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    // 圆
    private lateinit var startCircle: Circle
    private lateinit var endCircle: Circle
    
    // 两个圆连接的点
    private val startCircleA = Point()
    private val startCircleB = Point()
    private val endCircleC = Point()
    private val endCircleD = Point()
    
    // 控制贝塞尔曲线的点
    private val quadControlE = Point()
    
    // 圆心相距的距离
    private var circleCenterDistan = 0f
    
    // 是否能画path
    private var canDrawPath = true
    
    // 文本
    private var text = ""
    
    // 圆的颜色
    private var circleColor = Color.RED
    
    // 文字的颜色
    private var textColor = Color.WHITE
    
    // 文字的大小
    private var textSize = context.dpToPx(15f).toFloat()
    
    // 粒子的集合
    private val particleList = mutableListOf<Particle>()
    
    // 是否画粒子
    private var canDrawParticle = false
    
    // 生成粒子的bitmap
    private var bitmap: Bitmap? = null
    
    // 上次距离
    private var lastDistan = 0f
    
    // 视图尺寸
    private var viewWidth = 0
    private var viewHeight = 0
    
    // 动画结束的监听
    var onAnimationEndListener: OnAnimationEndListener? = null

    init {
        // 获取自定义属性
        context.withStyledAttributes(attrs, R.styleable.BubbleView) {
            text = getString(R.styleable.BubbleView_bubbleText) ?: ""
            circleColor = getColor(R.styleable.BubbleView_bubbleColor, Color.RED)
            textColor = getColor(R.styleable.BubbleView_textColor, Color.WHITE)
            textSize = getDimension(R.styleable.BubbleView_textSize, context.dpToPx(15f).toFloat())
        }
        
        // 初始化画笔
        circlePaint.color = circleColor
        textPaint.color = textColor
        textPaint.textSize = textSize
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        
        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            else -> context.dpToPx(15f)
        }
        
        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            else -> context.dpToPx(15f)
        }
        
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h
        startCircle = Circle(w / 2f, h / 2f, w / 2f)
        endCircle = Circle(w / 2f, h / 2f, h / 2f)
    }

    override fun onDraw(canvas: Canvas) {
        if (canDrawPath) {
            drawPath(canvas)
            drawCircle(canvas, startCircle)
        }
        
        if (canDrawParticle) {
            drawParticle(canvas)
            return
        }
        
        drawCircle(canvas, endCircle)
        drawText(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                bitmap = createBitmap()
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.x
                val y = event.y
                endCircle.set(x, y)
                computePath()
                invalidate()
                lastDistan = circleCenterDistan
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_UP -> {
                if (!canDrawPath) {
                    bitmap?.let {
                        generateParticles(it)
                        startAnimation()
                    }
                } else {
                    endCircle.set(startCircle.x, startCircle.y)
                    startCircle.radius = endCircle.radius
                    computePath()
                    invalidate()
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                if (canDrawPath) {
                    endCircle.set(startCircle.x, startCircle.y)
                    startCircle.radius = endCircle.radius
                    computePath()
                    invalidate()
                }
            }
        }
        return true
    }

    /**
     * 生成粒子
     */
    private fun generateParticles(bitmap: Bitmap) {
        canDrawParticle = true
        particleList.clear()
        
        val particleRadius = endCircle.radius * 2 / Particle.PARTICLE_COUNT / 2
        val bitmapWidth = bitmap.width
        val bitmapHeight = bitmap.height
        
        for (i in 0 until Particle.PARTICLE_COUNT) {
            for (j in 0 until Particle.PARTICLE_COUNT) {
                val width = endCircle.x - endCircle.radius + i * particleRadius * 2
                val height = endCircle.y - endCircle.radius + j * particleRadius * 2
                val color =
                    bitmap[bitmapWidth / Particle.PARTICLE_COUNT * i, bitmapHeight / Particle.PARTICLE_COUNT * j]
                
                val particle = Particle(width, height, particleRadius, color)
                particleList.add(particle)
            }
        }
    }

    /**
     * 开始动画
     */
    private fun startAnimation() {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1500
            addUpdateListener { animator ->
                val value = animator.animatedValue as Float
                particleList.forEach { particle ->
                    particle.broken(value, measuredWidth, measuredHeight)
                }
                invalidate()
            }
            
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                
                override fun onAnimationEnd(animation: Animator) {
                    clearStatus()
                    onAnimationEndListener?.onEnd(this@BubbleView)
                }
                
                override fun onAnimationCancel(animation: Animator) {}
                
                override fun onAnimationRepeat(animation: Animator) {}
            })
            
            start()
        }
    }

    /**
     * 生成粒子需要的bitmap
     */
    private fun createBitmap(): Bitmap {
        val size = (endCircle.radius * 2).toInt()
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        invalidate()
        return bitmap
    }

    /**
     * 计算路径
     */
    private fun computePath() {
        val startX = startCircle.x
        val startY = startCircle.y
        val endX = endCircle.x
        val endY = endCircle.y
        
        // 计算圆心的距离
        circleCenterDistan = sqrt((startX - endX).pow(2) + (startY - endY).pow(2))
        
        // 如果圆心的距离大于半径的5倍或者起点圆半径小于等于原来半径的1/5
        if (circleCenterDistan > endCircle.radius * 5 || startCircle.radius <= endCircle.radius / 5) {
            canDrawPath = false
            return
        }
        
        // 计算起点圆的半径
        if (circleCenterDistan > endCircle.radius) {
            startCircle.radius -= (circleCenterDistan - lastDistan) / 5
        }
        
        val cos = (endY - startY) / circleCenterDistan
        val sin = (endX - startX) / circleCenterDistan
        
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
     * 画路径
     */
    private fun drawPath(canvas: Canvas) {
        path.reset()
        path.moveTo(startCircleA.x, startCircleA.y)
        path.lineTo(startCircleB.x, startCircleB.y)
        path.quadTo(quadControlE.x, quadControlE.y, endCircleC.x, endCircleC.y)
        path.lineTo(endCircleD.x, endCircleD.y)
        path.quadTo(quadControlE.x, quadControlE.y, startCircleA.x, startCircleA.y)
        canvas.drawPath(path, circlePaint)
    }

    /**
     * 画圆
     */
    private fun drawCircle(canvas: Canvas, circle: Circle) {
        canvas.drawCircle(circle.x, circle.y, circle.radius, circlePaint)
    }

    /**
     * 画文字
     */
    private fun drawText(canvas: Canvas) {
        if (text.isEmpty()) return
        
        val rect = Rect()
        textPaint.getTextBounds(text, 0, text.length, rect)
        val textWidth = textPaint.measureText(text)
        val x = endCircle.x - textWidth / 2
        val y = endCircle.y + (rect.bottom - rect.top) / 2
        
        canvas.drawText(text, x, y, textPaint)
    }

    /**
     * 画粒子
     */
    private fun drawParticle(canvas: Canvas) {
        particleList.forEach { particle ->
            particlePaint.color = particle.color
            particlePaint.alpha = (particle.alpha * 255).toInt()
            canvas.drawCircle(particle.cx, particle.cy, particle.radius, particlePaint)
        }
    }

    /**
     * 设置文字
     */
    fun setText(text: String) {
        this.text = text
        invalidate()
    }

    /**
     * 设置文字颜色
     */
    fun setTextColor(textColor: Int) {
        this.textColor = textColor
        textPaint.color = textColor
        invalidate()
    }

    /**
     * 设置圆的颜色
     */
    fun setCircleColor(circleColor: Int) {
        this.circleColor = circleColor
        circlePaint.color = circleColor
        invalidate()
    }

    /**
     * 设置文字大小
     */
    fun setTextSize(size: Float) {
        this.textSize = size
        textPaint.textSize = size
        invalidate()
    }

    /**
     * 刷新状态
     */
    fun clearStatus() {
        path.reset()
        canDrawPath = true
        canDrawParticle = false
        particleList.clear()
        endCircle.set(viewWidth / 2f, viewHeight / 2f)
        startCircle.set(viewWidth / 2f, viewHeight / 2f)
        startCircle.radius = viewWidth / 2f
        circleCenterDistan = 0f
        computePath()
        invalidate()
    }

    /**
     * 动画结束监听接口
     */
    interface OnAnimationEndListener {
        fun onEnd(bubbleView: BubbleView)
    }
    
    companion object {
        private const val TAG = "BubbleView"
    }
}