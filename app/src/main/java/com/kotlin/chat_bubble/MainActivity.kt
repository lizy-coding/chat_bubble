package com.kotlin.chat_bubble

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.chat_bubble.databinding.ActivityMainBinding

/**
 * 主界面 - 使用ViewBinding和ViewModel
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val adapter = BubbleAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        observeViewModel()
    }
    
    /**
     * 设置RecyclerView
     */
    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            // 确保滑动顺滑且不裁剪子视图
            setHasFixedSize(true)
            clipChildren = false
        }
    }
    
    /**
     * 观察ViewModel数据变化
     */
    private fun observeViewModel() {
        viewModel.messageItems.observe(this) { messages ->
            adapter.submitList(messages)
        }
    }
}