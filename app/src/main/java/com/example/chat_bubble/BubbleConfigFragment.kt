package com.example.chat_bubble

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kotlin.bubbleview.BubbleView
import com.kotlin.bubbleview.Particle

class BubbleConfigFragment : Fragment() {

    private lateinit var bubbleViewPreview: BubbleView
    private lateinit var btnTestExplosion: Button
    private lateinit var btnApplySettings: Button
    
    // 进度条
    private lateinit var seekBarParticleCount: SeekBar
    private lateinit var seekBarDuration: SeekBar
    private lateinit var seekBarSpeedFactor: SeekBar
    private lateinit var seekBarSizeFactor: SeekBar
    private lateinit var seekBarAlphaFactor: SeekBar
    private lateinit var seekBarBreakDistance: SeekBar
    
    // 文本显示
    private lateinit var tvParticleCount: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvSpeedFactor: TextView
    private lateinit var tvSizeFactor: TextView
    private lateinit var tvAlphaFactor: TextView
    private lateinit var tvBreakDistance: TextView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bubble_config, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 初始化视图
        initViews(view)
        // 设置监听器
        setupListeners()
        // 设置初始值
        updateUIValues()
        // 立即应用设置，确保预览气泡使用增强效果
        applySettings()
    }
    
    private fun initViews(view: View) {
        bubbleViewPreview = view.findViewById(R.id.bubbleViewPreview)
        btnTestExplosion = view.findViewById(R.id.btnTestExplosion)
        btnApplySettings = view.findViewById(R.id.btnApplySettings)
        
        // 初始化进度条
        seekBarParticleCount = view.findViewById(R.id.seekBarParticleCount)
        seekBarDuration = view.findViewById(R.id.seekBarDuration)
        seekBarSpeedFactor = view.findViewById(R.id.seekBarSpeedFactor)
        seekBarSizeFactor = view.findViewById(R.id.seekBarSizeFactor)
        seekBarAlphaFactor = view.findViewById(R.id.seekBarAlphaFactor)
        seekBarBreakDistance = view.findViewById(R.id.seekBarBreakDistance)
        
        // 设置初始进度条值 - 使用增强的参数
        seekBarParticleCount.progress = Particle.PARTICLE_COUNT
        seekBarDuration.progress = 3000  // 整体动画时间
        seekBarSpeedFactor.progress = 120 // 速度
        seekBarSizeFactor.progress = 85   // 颗粒缩放
        seekBarAlphaFactor.progress = 120 // 透明度变化
        seekBarBreakDistance.progress = 4 // 触发距离
        
        // 初始化显示文本
        tvParticleCount = view.findViewById(R.id.tvParticleCount)
        tvDuration = view.findViewById(R.id.tvDuration)
        tvSpeedFactor = view.findViewById(R.id.tvSpeedFactor)
        tvSizeFactor = view.findViewById(R.id.tvSizeFactor)
        tvAlphaFactor = view.findViewById(R.id.tvAlphaFactor)
        tvBreakDistance = view.findViewById(R.id.tvBreakDistance)
    }
    
    private fun setupListeners() {
        val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateUIValues()
                
                // 实时应用当前更改的滑块值，实现即时预览
                if (fromUser) {
                    applySettings()
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        }
        
        // 设置所有进度条的监听器
        seekBarParticleCount.setOnSeekBarChangeListener(seekBarChangeListener)
        seekBarDuration.setOnSeekBarChangeListener(seekBarChangeListener)
        seekBarSpeedFactor.setOnSeekBarChangeListener(seekBarChangeListener)
        seekBarSizeFactor.setOnSeekBarChangeListener(seekBarChangeListener)
        seekBarAlphaFactor.setOnSeekBarChangeListener(seekBarChangeListener)
        seekBarBreakDistance.setOnSeekBarChangeListener(seekBarChangeListener)
        
        // 应用设置按钮
        btnApplySettings.setOnClickListener {
            applySettings()
        }
        
        // 测试爆炸效果按钮
        btnTestExplosion.setOnClickListener {
            applySettings()
            
            // 创建一个测试用的BubbleView，位置设在屏幕外
            val testBubble = BubbleView(requireContext())
            val params = ViewGroup.LayoutParams(
                bubbleViewPreview.width,
                bubbleViewPreview.height
            )
            testBubble.layoutParams = params
            testBubble.x = -1000f // 屏幕外
            testBubble.y = -1000f // 屏幕外
            
            // 复制参数
            testBubble.setText(bubbleViewPreview.getText())
            testBubble.setCircleColor(bubbleViewPreview.getCircleColor())
            testBubble.setTextColor(bubbleViewPreview.getTextColor())
            testBubble.setExplosionParams(
                seekBarParticleCount.progress,
                seekBarDuration.progress,
                seekBarSpeedFactor.progress / 100f,
                seekBarSizeFactor.progress / 100f,
                seekBarAlphaFactor.progress / 100f
            )
            testBubble.setBreakDistanceFactor(seekBarBreakDistance.progress / 1f)
            
            // 添加到父视图
            (view as? ViewGroup)?.addView(testBubble)
            
            // 手动触发爆炸效果
            testBubble.onSizeChanged(
                bubbleViewPreview.width,
                bubbleViewPreview.height,
                bubbleViewPreview.width,
                bubbleViewPreview.height
            )
            testBubble.clearStatus()
            
            // 需要一个延迟来确保视图已准备好
            testBubble.postDelayed({
                // 手动模拟拖拽过程
                val event = android.view.MotionEvent.obtain(
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    android.view.MotionEvent.ACTION_DOWN,
                    testBubble.width / 2f,
                    testBubble.height / 2f,
                    0
                )
                testBubble.dispatchTouchEvent(event)
                event.recycle()
                
                // 移动到足够远的位置触发断开
                val moveEvent = android.view.MotionEvent.obtain(
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    android.view.MotionEvent.ACTION_MOVE,
                    testBubble.width / 2f + testBubble.width * 3,
                    testBubble.height / 2f,
                    0
                )
                testBubble.dispatchTouchEvent(moveEvent)
                moveEvent.recycle()
                
                // 松开触发爆炸
                val upEvent = android.view.MotionEvent.obtain(
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    android.view.MotionEvent.ACTION_UP,
                    testBubble.width / 2f + testBubble.width * 3,
                    testBubble.height / 2f,
                    0
                )
                testBubble.dispatchTouchEvent(upEvent)
                upEvent.recycle()
                
                // 复制到预览气泡上
                bubbleViewPreview.visibility = View.INVISIBLE
                testBubble.x = bubbleViewPreview.x
                testBubble.y = bubbleViewPreview.y
                
                // 动画完成后清理
                testBubble.onAnimationEndListener = object : BubbleView.OnAnimationEndListener {
                    override fun onEnd(bubbleView: BubbleView) {
                        (view as? ViewGroup)?.removeView(testBubble)
                        bubbleViewPreview.visibility = View.VISIBLE
                    }
                }
            }, 100)
        }
    }
    
    private fun updateUIValues() {
        // 更新文本显示
        tvParticleCount.text = seekBarParticleCount.progress.toString()
        tvDuration.text = seekBarDuration.progress.toString()
        tvSpeedFactor.text = (seekBarSpeedFactor.progress / 100f).toString()
        tvSizeFactor.text = (seekBarSizeFactor.progress / 100f).toString()
        tvAlphaFactor.text = (seekBarAlphaFactor.progress / 100f).toString()
        tvBreakDistance.text = (seekBarBreakDistance.progress / 1f).toString()
    }
    
    private fun applySettings() {
        // 应用设置到预览气泡
        bubbleViewPreview.setExplosionParams(
            seekBarParticleCount.progress,
            seekBarDuration.progress,
            seekBarSpeedFactor.progress / 100f,
            seekBarSizeFactor.progress / 100f,
            seekBarAlphaFactor.progress / 100f
        )
        bubbleViewPreview.setBreakDistanceFactor(seekBarBreakDistance.progress / 1f)
    }
    
    companion object {
        fun newInstance() = BubbleConfigFragment()
    }
}