一个网络请求，基类封装库

添加使用方法

第一步：
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}

第二步：
dependencies {
	        implementation 'com.github.Arialena:RxHttpSimp:${Latest}'
}

使用
Application继承BaseApplication

初始化http请求

activity继承BaseActivity

fragment继承BaseLazyLoadFragment
