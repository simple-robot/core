## Simple Robot v4

施工中。


```kotlin
val application = launchSimpleApplication {
    config {
        // 部分配置属性...
    }
    
    eventDispatcher {
        // 事件调度器配置....
    }

    // 安装组件或插件
    install(...)
}

// 使用事件调度器
val eventDispatcher = application.eventDispatcher



```

