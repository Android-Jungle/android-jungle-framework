# android-jungle-framework 简介


### 1、简介

`android-jungle-framework` 是一款 Android 上 App 开发的库集合。目标致力于作为一款 **`基础框架`**，将一些 Android 开发中常见的模式 & 功能集中实现，让 Android 的开发变得简单。

### 2、模块

|library|功能|
|---|---|
|[jungle-base](https://github.com/arnozhang/android-jungle-framework/tree/master/docs/jungle-base)|提供基础的模块——Application、AppCore、各种 Manager、各种 Utils 等等|
|[jungle-toolbaractivity](https://github.com/arnozhang/android-jungle-framework/tree/master/docs/jungle-toolbaractivity)|提供 Toolbar 样式的基础 Activity，包括可右滑返回的 Activity 实现|
|[jungle-imageloader](https://github.com/arnozhang/android-jungle-framework/tree/master/docs/jungle-imageloader)|图片加载库——提供 ImageLoaderUtils 系列接口加载图片，目前图片加载引擎使用 [Fresco](https://github.com/facebook/fresco)。（可自由切换其他图片加载库）|
|[jungle-widgets](https://github.com/arnozhang/android-jungle-framework/tree/master/docs/jungle-widgets)|提供各种基础的 Widgets——各种 MessageBox & Dialog、模拟的 ActionSheet 等等|
|[jungle-mediaplayer](https://github.com/arnozhang/android-jungle-framework/tree/master/docs/jungle-mediaplayer)|提供 MediaPlayer、AudioRecorder 等等多媒体播放 & 录制组件|
|android-jungle-framework-photos|基于上述库实现的一款图片浏览类 App|
