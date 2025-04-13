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
2. 将位图分解为多个粒子（默认为10×10的网格）
3. 使用`ValueAnimator`动画控制粒子扩散和透明度变化

```kotlin
/**
 * 生成粒子
 */
private fun generateParticles(bitmap: Bitmap) {
    canDrawParticle = true
    particleList.clear()
    
    val particleRadius = endCircle.radius * 2 / Particle.PARTICLE_COUNT / 2
    val bitmapWidth = bitmap.width
    val bitmapHeight = bitmap.height
    
    // 根据位图生成粒子网格
    for (i in 0 until Particle.PARTICLE_COUNT) {
        for (j in 0 until Particle.PARTICLE_COUNT) {
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
    animator.duration = 500
    animator.addUpdateListener { animation ->
        val value = animation.animatedValue as Float
        // 更新每个粒子的位置和透明度
        particleList.forEach { particle ->
            particle.broken(value, viewWidth, viewHeight)
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
    app:bubbleText="99+"
    app:bubbleColor="#FF4081"
    app:textColor="#FFFFFF"
    app:textSize="16sp" />
```

### 3. 在代码中设置监听器

```kotlin
// 设置气泡的动画结束监听器
val bubbleView = findViewById<BubbleView>(R.id.bubbleView)
bubbleView.onAnimationEndListener = object : BubbleView.OnAnimationEndListener {
    override fun onEnd(bubbleView: BubbleView) {
        // 动画结束后的操作，可以在这里添加更多逻辑
        // 例如隐藏气泡或更新UI状态
    }
}
```

## 自定义属性

BubbleView支持以下自定义属性：

| 属性 | 说明 | 默认值 |
|------|------|--------|
| `app:bubbleText` | 气泡中显示的文本 | "" |
| `app:bubbleColor` | 气泡的背景颜色 | Color.RED |
| `app:textColor` | 文本颜色 | Color.WHITE |
| `app:textSize` | 文本大小 | 15sp |

## 示例应用

在示例应用中，展示了不同场景下气泡的使用方式：

1. 不同大小和颜色的气泡
2. 气泡拖拽效果
3. 爆炸效果及回调处理

## 开发环境

- Kotlin 2.0.21
- Android SDK 35
- Gradle 8.9.1

## 许可证

[MIT License](LICENSE)