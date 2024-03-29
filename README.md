## Simple Robot v4

> [!important]
> 此仓库是 Simple Robot v4 版本的**临时**开发仓库，会在大部分基本工作都完成或接近尾声的时候合并至 [主仓库](https://github.com/simple-robot/simpler-robot)
> 的相关分支并移除此仓库。


> [!warning]
> 本仓库内容已经迁移至 [simpler-robot v4-dev分支](https://github.com/simple-robot/simpler-robot/tree/v4-dev) 。


```kotlin
val application = launchApplication(Simple) {
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

eventDispatcher.register {
    // 注册事件处理器
}

eventDispatcher.listen<FooEvent> { event -> // this: EventContext
    // 指定类型地注册事件处理器
}

// push(event) -> Flow<EventResult>
eventDispatcher.push(event).collect {
    // ...
}

// 使用Bot管理器
val botManager = application.botManagers.get<FooBotManager>()

// ...
```
