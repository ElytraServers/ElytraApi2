# Elytra Api 2

## 介绍

鞘翅接口2，是用于 Elytra 服务器插件系列开发的库。目前包含本地化支持方案（LocaleService）。

## 如何使用

### 对服主

在 [Release](https://github.com/ElytraServers/ElytraApi2/release/latest) 里下载最新版本。

### 对开发者 To Developers

#### 在项目中引入 JitPack

```groovy
repositories {
  maven {
    name = 'jitpack'
    url = 'https://jitpack.io'
  }
}
```

#### 在项目中加入 ElytraApi2

```groovy
denpendencies {
  implementation 'com.github.ElytraServers:ElytraApi2:<TAG>'
}
```

## 提供支持

### 本地化语言服务 LocaleService

[`UseLocaleServicePlugin.java`](https://github.com/ElytraServers/ElytraApi2/blob/master/src/test/java/cn/elytra/code/example/UseLocaleServicePlugin.java)
