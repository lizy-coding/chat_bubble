# 气泡拖拽动画模块

一个可以拖拽的气泡效果控件库，支持自定义拖拽动效，断裂爆炸粒子效果，可用于未读消息提示、红点提醒等场景。

![Bubble](https://github.com/lizy-coding/chat_bubble/blob/master/bubble.gif)

## 项目结构

本项目采用模块化结构，方便集成和使用：

```
chat_bubble/
├── app/                    # 示例应用模块
│   ├── src/main/
│   │   ├── java/           # 示例应用代码
│   │   └── res/            # 示例应用资源文件
│   └── build.gradle.kts    # 应用级构建配置
├── bubbleview/             # 气泡视图库模块
│   ├── src/main/
│   │   ├── java/com/kotlin/bubbleview/
│   │   │   ├── BubbleView.kt      # 主要气泡视图组件
│   │   │   ├── Circle.kt          # 圆形数据模型
│   │   │   ├── Particle.kt        # 粒子效果实现
│   │   │   ├── Point.kt           # 点坐标数据模型
│   │   │   └── Extensions.kt      # Kotlin扩展工具函数
│   │   ├── res/
│   │   │   └── values/
│   │   │       └── attrs.xml      # 自定义属性定义
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts    # 库模块构建配置
├── gradle/                 # Gradle配置
├── build.gradle.kts        # 项目级构建配置
└── settings.gradle.kts     # 项目设置配置
```

## 功能特性

- 支持圆形气泡拖拽效果
- 支持断裂爆炸粒子效果
- 可自定义气泡颜色、文字和样式
- 支持动画结束回调
- 支持灵活的布局配置
- 支持自定义粒子效果参数

## 气泡实现原理

### 主要组件

1. **BubbleView**: 主视图组件，处理绘制和交互逻辑
2. **Circle**: 圆形模型，用于表示固定圆和可拖动圆
3. **Point**: 点坐标模型，用于贝塞尔曲线计算
4. **Particle**: 爆炸效果的粒子模型

### 气泡生成与绘制

气泡视图使用两个圆形来实现拖拽效果：
- `startCircle`: 固定在原位置的圆
- `endCircle`: 跟随手指移动的圆

当拖拽时，两个圆之间通过贝塞尔曲线连接形成流体变形效果：

1. 计算两个圆的连接点（A、B、C、D四个点）
2. 计算贝塞尔曲线的控制点（E点）
3. 使用Path绘制两条二阶贝塞尔曲线和两条直线来连接圆形

```kotlin
private fun computePath() {
    // 计算两圆心之间的距离
    circleCenterDistan = sqrt(
        (startCircle.x - endCircle.x).pow(2) + 
        (startCircle.y - endCircle.y).pow(2)
    )
    
    // 根据距离判断是否可以绘制连接路径
    if (circleCenterDistan > maxDistance()) {
        canDrawPath = false
        return
    }
    
    // 计算固定圆变化的半径
    startCircle.radius = (startCircleInitRadius * (1 - circleCenterDistan / maxDistance()))
        .coerceAtLeast(startCircleMinRadius)
        
    // 计算连接点和控制点
    // ...
    
    // 构建路径
    path.reset()
    path.moveTo(startCircleA.x, startCircleA.y)
    path.quadTo(quadControlE.x, quadControlE.y, endCircleD.x, endCircleD.y)
    path.lineTo(endCircleC.x, endCircleC.y)
    path.quadTo(quadControlE.x, quadControlE.y, startCircleB.x, startCircleB.y)
    path.close()
}
```

## 动画实现

### 拖拽动画

拖拽动画通过处理触摸事件（`onTouchEvent`）实时计算并重绘视图来实现：

1. `ACTION_DOWN`: 捕获初始状态，创建位图用于后续粒子爆炸效果
2. `ACTION_MOVE`: 更新`endCircle`位置，重新计算路径并重绘
3. `ACTION_UP`: 
   - 如果超过临界距离，触发爆炸效果
   - 否则回弹到原位置

### 爆炸粒子效果

当拖拽超过临界距离并松开手指时，气泡会爆炸成多个粒子：

1. 基于原气泡生成位图
2. 将位图分解为多个粒子（粒子数量可配置）
3. 使用`ValueAnimator`动画控制粒子扩散和透明度变化

```kotlin
/**
 * 生成粒子
 */
private fun generateParticles(bitmap: Bitmap) {
    canDrawParticle = true
    particleList.clear()
    
    val count = particleCount // 可自定义粒子数量
    val particleRadius = endCircle.radius * 2 / count / 2
    val bitmapWidth = bitmap.width
    val bitmapHeight = bitmap.height
    
    // 根据位图生成粒子网格
    for (i in 0 until count) {
        for (j in 0 until count) {
            // 计算粒子位置和颜色
            // ...
            particleList.add(Particle(cx, cy, particleRadius, color))
        }
    }
}

/**
 * 启动粒子动画
 */
private fun startAnimation() {
    val animator = ValueAnimator.ofFloat(0f, 1f)
    animator.duration = explosionDuration // 可自定义动画持续时间
    animator.addUpdateListener { animation ->
        val value = animation.animatedValue as Float
        // 更新每个粒子的位置和透明度
        particleList.forEach { particle ->
            particle.broken(
                value, 
                viewWidth, 
                viewHeight,
                particleSpeedFactor,  // 速度因子
                particleSizeFactor,   // 大小变化因子
                particleAlphaFactor   // 透明度变化因子
            )
        }
        // 重绘视图
        invalidate()
    }
    
    animator.addListener(object : Animator.AnimatorListener {
        // 动画结束时触发回调
        override fun onAnimationEnd(animation: Animator) {
            onAnimationEndListener?.onEnd(this@BubbleView)
        }
        // ...
    })
    
    animator.start()
}
```

### 粒子爆炸参数控制

粒子爆炸效果支持多种参数控制，可实现各种不同的视觉效果：

```kotlin
// 在代码中设置爆炸效果参数
bubbleView.setExplosionParams(
    count = 15,             // 粒子数量，数量越多效果越细腻
    duration = 1500,        // 爆炸动画持续时间(毫秒)
    speedFactor = 1.5f,     // 粒子速度因子，越大飞得越远
    sizeFactor = 1.0f,      // 粒子大小变化因子，越大缩小得越快
    alphaFactor = 0.8f      // 透明度变化因子，越大消失得越快
)

// 设置断开连接的临界距离
bubbleView.setBreakDistanceFactor(5.0f)
```

粒子爆炸算法实现（Particle.kt）：

```kotlin
fun broken(
    factor: Float,          // 动画进度(0-1)
    width: Int,             // 视图宽度
    height: Int,            // 视图高度
    speedFactor: Float,     // 速度因子
    sizeFactor: Float,      // 大小变化因子
    alphaFactor: Float      // 透明度变化因子
) {
    // 计算方向性移动
    val distance = factor * speed * speedFactor * width / 6
    cx += distance * cos(angle)
    cy += distance * sin(angle)
    
    // 添加重力效果
    cy += factor * factor * height / 3
    
    // 添加随机抖动
    if (random.nextFloat() > 0.7f) {
        cx += (random.nextFloat() - 0.5f) * width / 20 * factor
        cy += (random.nextFloat() - 0.5f) * height / 20 * factor
    }
    
    // 缩小粒子大小
    radius = radius * (1f - factor * sizeFactor * 0.7f)
    
    // 透明度变化
    alpha = (1f - factor * alphaFactor) * (1 + random.nextFloat() * 0.3f)
}
```

## 使用方法

### 1. 在应用中集成库

在项目的`settings.gradle.kts`中添加模块引用：

```kotlin
include(":app")
include(":bubbleview")
```

在应用模块的`build.gradle.kts`中添加依赖：

```kotlin
dependencies {
    implementation(project(":bubbleview"))
    // 其他依赖...
}
```

### 2. 在布局中使用BubbleView

```xml
    <com.kotlin.bubbleview.BubbleView
        android:id="@+id/bubbleView"
        android:layout_width="120dp"
        android:layout_height="120dp"
        android:layout_marginTop="32dp"
        app:bubbleText="99+"
        app:bubbleColor="#FF4081"
        app:textColor="#FFFFFF"
        app:textSize="16sp"
        app:layout_constraintTop_toBottomOf="@id/descriptionText"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />
```

### 3. 在代码中设置监听器和参数

```kotlin
// 设置气泡的动画结束监听器
val bubbleView = findViewById<BubbleView>(R.id.bubbleView)
bubbleView.onAnimationEndListener = object : BubbleView.OnAnimationEndListener {
    override fun onEnd(bubbleView: BubbleView) {
        // 动画结束后的操作，可以在这里添加更多逻辑
        // 例如隐藏气泡或更新UI状态
    }
}

// 自定义粒子爆炸效果
seekBarParticleCount.progress = Particle.PARTICLE_COUNT
seekBarDuration.progress = 3000  // 整体动画时间
seekBarSpeedFactor.progress = 120 // 速度
seekBarSizeFactor.progress = 85   // 颗粒缩放
seekBarAlphaFactor.progress = 120 // 透明度变化
seekBarBreakDistance.progress = 4 // 触发距离

// 设置断开连接的临界距离
testBubble.setBreakDistanceFactor(seekBarBreakDistance.progress / 1f)
// 值越小，越容易断开
```

## 自定义属性

BubbleView支持以下自定义属性：

| 属性 | 说明 | 默认值 |
|------|------|--------|
| `app:bubbleText` | 气泡中显示的文本 | "" |
| `app:bubbleColor` | 气泡的背景颜色 | Color.RED |
| `app:textColor` | 文本颜色 | Color.WHITE |
| `app:textSize` | 文本大小 | 15sp |
| `app:particleCount` | 粒子数量 | 15 |
| `app:explosionDuration` | 爆炸动画持续时间(毫秒) | 1500 |
| `app:particleSpeedFactor` | 粒子速度因子 | 1.0 |
| `app:particleSizeFactor` | 粒子大小变化因子 | 1.0 |
| `app:particleAlphaFactor` | 粒子透明度变化因子 | 1.0 |

## 示例应用

在示例应用中，展示了不同场景下气泡的使用方式：

1. 不同大小和颜色的气泡
2. 气泡拖拽效果
3. 爆炸效果及回调处理
4. 粒子效果参数配置界面

## 开发环境

- Kotlin 2.0.21
- Android SDK 35
- Gradle 8.9.1

## 许可证

[MIT License](LICENSE)