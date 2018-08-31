# Pjsip 编译Android 版本

## 1. 编译环境：

 > win7 MYSM

 > Android相关

 * sdk 路径  D:\Android\sdk

 * ndk 路径 D:\Android\sdk\ndk-bundle 版本号：source.properties--> Pkg.Revision = 15.1.4119039

 > pjproject-2.7.2

 > bcg729 - 1.0.4

## 2. 安装MSYS2

[**官方安装指导**](https://www.msys2.org/)

## 3. 单独编译Pjsip

[**官方编译指导**](https://trac.pjsip.org/repos/wiki/Getting-Started/Android)点击打开

增加头文件 pjproject-2.7.2\pjlib\include\pj\config_site.h


```
// 内容
/* Activate Android specific settings in the 'config_site_sample.h' */
#define PJ_CONFIG_ANDROID 1
#include <pj/config_site_sample.h>
```

```bash
// 导入Andorid ndk 路径
export ANDROID_NDK_ROOT=/d/Android/sdk/ndk-bundle

cd pjproject-2.7.2

// 编译 pjsip
./configure-android
make dep && make clean && make

// 编译 Android 静态库
cd pjsip-apps/src/swig/
make

```



## 4. 增加 G729 编码支持

[**官方编译指导**](https://trac.pjsip.org/repos/ticket/2029)点击打开

[**linphone bcg729源码**](https://gitlab.linphone.org/BC/public/bcg729) 比GitHub [**BelledonneCommunications bcg729**](https://github.com/BelledonneCommunications/bcg729) 新

## 4.1 编译 bcg729

### 4.1.1 查看build_bcg729.sh 文件，并修改相关配置

在以下路径存在build_bcg729.sh 文件

* AndroidPractice/pjsip_source/bcg729/android/build_bcg729.sh

```bash
$ pwd
/d/Android/work2018/my/AndroidPractice/pjsip_source/bcg729/android
$ ls
build_bcg729.sh
```

> build_bcg729.sh 文件内容

```bash
# 1. 导入 Android 环境变量
# * 此处需要根据您的 sdk ndk 路径进行修改
export PATH=/d/Android/sdk/cmake/3.6.4111459/bin/:$PATH
export ANDROID_NDK_ROOT=/d/Android/sdk/ndk-bundle

# 2. cmake 命令，
# 注意：
# * CMAKE_TOOLCHAIN_FILE ，此处需要根据您的 ndk 路径进行修改
# * CMAKE_MAKE_PROGRAM ，此处需要根据您的 sdk 路径进行修改
# * CMAKE_GENERATOR="Ninja" , 使用 Ninja，特别此处存在双引号
# * CMAKE_SYSTEM_NAME=Android, 使用 Android
# * CMAKE_SYSTEM_VERSION ，Android 版本
# * CMAKE_ANDROID_ARCH_ABI ，生成的 静态库 类型
# * 从此处开始到 cmake end ，不能存在注释分割命令行
cmake  ../ \
  -DCMAKE_TOOLCHAIN_FILE=/d/Android/sdk/ndk-bundle/build/cmake/android.toolchain.cmake  \
  -DCMAKE_MAKE_PROGRAM=/d/Android/sdk/cmake/3.6.4111459/bin/ninja.exe \
  -DANDROID_TOOLCHAIN=clang \
  -DCMAKE_GENERATOR="Ninja"  \
  -DCMAKE_SYSTEM_NAME=Android \
  -DCMAKE_SYSTEM_VERSION=17 \
  -DCMAKE_ANDROID_ARCH_ABI=armeabi \
  -DCMAKE_ANDROID_NDK=/d/Android/sdk/ndk-bundle \
  -DCMAKE_ANDROID_STL_TYPE=gnustl_static \
  -DENABLE_STATIC=ON \
  -DENABLE_SHARED=ON \
  -DCMAKE_BUILD_TYPE=Release \
  -DENABLE_TESTS=OFF \
  -DCMAKE_SKIP_INSTALL_RPATH=ON \

  # cmake end


# 3. 使用 ninja 进行编译
ninja
```

### 4.1.2 运行 build_bcg729.sh，进行编译

```bash
$ ./build_bcg729.sh
-- Android: Targeting API '14' with architecture 'arm', ABI 'armeabi-v7a', and processor 'armv7-a'
-- Android: Selected Clang toolchain 'arm-linux-androideabi-clang' with GCC toolchain 'arm-linux-androideabi-4.9'
-- Check for working C compiler: D:/Android/sdk/ndk-bundle/toolchains/llvm/prebuilt/windows-x86_64/bin/clang.exe
-- Check for working C compiler: D:/Android/sdk/ndk-bundle/toolchains/llvm/prebuilt/windows-x86_64/bin/clang.exe -- works
-- Detecting C compiler ABI info
-- Detecting C compiler ABI info - done
-- Detecting C compile features
-- Detecting C compile features - done
-- Package file name is bcg729-1.0.4
-- Configuring done
-- Generating done
-- Build files have been written to: .../../.../AndroidPractice/pjsip_source/bcg729/android
.......
[51/54] Building C object src/CMakeFiles/bcg729.dir/cng.c.o
[52/54] Building C object src/CMakeFiles/bcg729.dir/dtx.c.o
[53/54] Building C object src/CMakeFiles/bcg729.dir/vad.c.o
[54/54] Linking C shared library src\libbcg729.so
```

### 4.1.3 最终生成的静态库路径

```
D:\Android\work2018\my\AndroidPractice\pjsip_source\bcg729\android\src\libbcg729.so
```

## 4.2 根据pjsip 编译文件，修改bcg729相关路径

### 4.2.1 分析 pjsip 编译文件

* AndroidPractice\pjsip_source\pjproject-2.7.2\aconfigure

```bash

  if test "x$with_bcg729" != "xno" -a "x$with_bcg729" != "x"; then
		        BCG729_PREFIX=$with_bcg729
		  	BCG729_CFLAGS="-I$BCG729_PREFIX/include"
			BCG729_LDFLAGS="-L$BCG729_PREFIX/lib"
```

可知 pjsip 编译过程中，会查找对应的 “BCG729_PREFIX/include”、“BCG729_PREFIX/lib”

因此，在修改 bcg729文件目录

```bash
bcg729
    |-- include
        |-- bcg729
            |-- decoder.h
            |-- encoder.h
    |-- lib
        |-- libbcg729.so
```

### 4.2.2 编译pjsip

#### 4.2.2.1 执行以下命令，查看控制台输出
```bash
cd pjproject-2.7.2

export ANDROID_NDK_ROOT=/d/Android/sdk/ndk-bundle

./configure-android --with-bcg729=/d/Android/work2018/my/AndroidPractice/pjsip_source/bcg729

```

错误结果：

```bash 
Using bcg729 prefix... ./bcg729
checking bcg729 usability... no
Checking if libyuv is disabled...no


这是由于 上一步 bcg729文件目录 ，未正确配置, 需要修改 bcg729文件目录
```

正确结果：

```
Using bcg729 prefix... ./bcg729
checking bcg729 usability... ok
```



#### 4.2.2.2 第二步

```
make dep && make clean && make

// 编译 Android 静态库
cd pjsip-apps/src/swig/

make clean && make

```

错误：

```
d:/android/sdk/ndk-bundle/toolchains/arm-linux-androideabi-4.9/prebuilt/windows-x86_64/bin/../lib/gcc/arm-linux-androideabi/4.9.x/../../../../arm-linux-androideabi/bin/ld.exe: error: cannot find -lbcg729
collect2.exe: error: ld returned 1 exit status
make[2]: *** [/d/Android/work2018/my/AndroidPractice/pjsip_source/pjproject-2.7.2/build/rules.mak:125：../bin/pjlib-test-arm-unknown-linux-androideabi] 错误 1
make[2]: 离开目录“/d/Android/work2018/my/AndroidPractice/pjsip_source/pjproject-2.7.2/pjlib/build”
make[1]: *** [Makefile:112：pjlib-test-arm-unknown-linux-androideabi] 错误 2
make[1]: 离开目录“/d/Android/work2018/my/AndroidPractice/pjsip_source/pjproject-2.7.2/pjlib/build”
make: *** [Makefile:14：all] 错误 1


原因 无法找到 libbcg729.so
修正 ./configure-android --with-bcg729=[完整全路径]
```

大功告成,获取 pjsip 静态库、 bcg729 静态库
```
AndroidPractice\pjsip_source\pjproject-2.7.2\pjsip-apps\src\swig\java\android\app\src\main\jniLibs\armeabi\libpjua2.so

AndroidPractice\pjsip_source\bcg729\lib\libbcg729.so
```

