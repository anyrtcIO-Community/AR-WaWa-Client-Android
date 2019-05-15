# anyRTC娃娃机客户端信令安卓SDK

# 简介
第三代娃娃机在线解决方案，全新娱乐方式，超低延时娱乐。</br>

## 如何使用？

## 注册账号
登陆[AnyRTC官网](https://www.anyrtc.io/)

## 填写信息
创建应用，在管理中心获取开发者ID，AppID，AppKey，AppToken，WaWaApplication中的相关信息


## 资源中心
[更多详细方法使用，请查看API文档](https://www.anyrtc.io/resoure)


## 扫描二维码体验

### iOS
![image](https://github.com/AnyRTC/anyRTC-WaWa-Client-iOS/blob/master/anyRTC_WaWaji_iOS.png)
### android
![image](https://github.com/AnyRTC/anyRTC-WaWa-Client-iOS/blob/master/anyRTC_WaWaji_android.png)
### h5
![image](https://github.com/AnyRTC/anyRTC-WaWa-Client-iOS/blob/master/anyRTC_WaWaji_h5.png)

# 集成方式

## > 方式一（推荐）

添加Jcenter仓库 Gradle依赖：

```
dependencies {
    compile 'org.anyrtc:anyrtcwawaclient:1.0.0'
}
```

或者 Maven
```
<dependency>
  <groupId>org.anyrtc</groupId>
  <artifactId>anyrtcwawaclient</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

> 方式二



>1. 将下载DEMO中的anyrtcwawaclient-release.aar文件放入项目的libs目录中
>2. 在Model下的build.gradle文件添加如下代码依赖anyRTCWaWaClient SDK

```
android
{

 repositories {
        flatDir {dirs 'libs'}
    }
    
 }
    
```
```
dependencies {
    compile(name: 'anyrtcwawaclient-release', ext: 'aar')
    //还需添加第三方socket框架
    compile('io.socket:socket.io-client:1.0.0') {
        exclude group: 'org.json', module: 'json'
    }
}
```


## IOS版娃娃机Demo
[anyRTC-WaWa-Client-IOS](https://github.com/AnyRTC/anyRTC-WaWa-Client-iOS)
## Web 版娃娃机Demo
[anyRTC-WaWa-Client-Web](https://github.com/AnyRTC/anyRTC-WaWa-Client-Web)


## 技术支持 
- anyRTC官方网址：[https://www.anyrtc.io](https://www.anyrtc.io/resoure)
- QQ技术咨询群：554714720
- 联系电话:021-65650071-816
- Email:hi@dync.cc
## 关于直播
本公司有一整套直播解决方案，特别针对移动端。本公司开发者平台[www.anyrtc.io](http://www.anyrtc.io)。除了基于RTMP协议的直播系统外，我公司还有基于WebRTC的时时交互直播系统、P2P呼叫系统、会议系统等。快捷集成SDK，便可让你的应用拥有时时通话功能。欢迎您的来电~
# License

anyRTC-WaWa-Client is available under the MIT license. See the LICENSE file for more info.
