
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
