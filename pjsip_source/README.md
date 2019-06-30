# Pjsip 编译Android 版本

## 1. 编译环境：

 > ubuntu 14.0.4

 > Android相关

 * sdk 路径  

 * ndk 版本号：source.properties--> Pkg.Revision = 15.1.4119039

 > pjproject-2.9

 > bcg729 - 1.0.4

## 2. 安装cmake

[**官方下载地址**](https://cmake.org/download/)

当前最新版本为v3.15.0-rc3, [下载安装包](https://github.com/Kitware/CMake/releases/download/v3.15.0-rc3/cmake-3.15.0-rc3-Linux-x86_64.tar.gz)

1. 解压
```bash
tar zxvf cmake-3.15.0-rc3-Linux-x86_64.tar.gz

解压后的目录为：
cmake-3.15.0-rc3-Linux-x86_64
├── bin
│   ├── ccmake
│   ├── cmake
│   ├── cmake-gui
│   ├── cpack
│   └── ctest
├── doc
│   └── cmake
├── man
│   ├── man1
│   └── man7
└── share
    ├── aclocal
    ├── applications
    ├── cmake-3.9
    ├── icons
    └── mime
```

2. 创建软链接

注: 文件路径是可以指定的, 一般选择在/opt 或 /usr 路径下, 这里选择/opt

```bash
mv cmake-3.15.0-rc3-Linux-x86_64 /opt/cmake-3.15.0
ln -sf /opt/cmake-3.9.1/bin/*  /usr/bin/

cmake --version
```

## 3. 单独编译Pjsip

[**官方编译指导**](https://trac.pjsip.org/repos/wiki/Getting-Started/Android)点击打开

增加头文件 pjproject-2.9\pjlib\include\pj\config_site.h

```bash
// 内容
/* Activate Android specific settings in the 'config_site_sample.h' */
#define PJ_CONFIG_ANDROID 1
#include <pj/config_site_sample.h>
```

```bash
// 导入Andorid ndk 路径
export ANDROID_NDK_ROOT=/d/Android/sdk/ndk-bundle

cd pjproject-2.9

// 编译 pjsip
./configure-android
make dep && make clean && make

// 编译 Android 静态库
cd pjsip-apps/src/swig/
make

```

## 4. 编译 bcg729

[**官方编译指导**](https://trac.pjsip.org/repos/ticket/2029)点击打开

[**linphone bcg729源码**](https://gitlab.linphone.org/BC/public/bcg729) 比GitHub [**BelledonneCommunications bcg729**](https://github.com/BelledonneCommunications/bcg729) 新

## 4.1 编译 bcg729

### 4.1.1 查看build_bcg729.sh 文件，并修改相关配置

在以下路径存在build_bcg729.sh 文件

* AndroidPractice/pjsip_source/bcg729/android/build_bcg729.sh

> build_bcg729.sh 关键内容

```bash
# 1. 导入 Android 环境变量
# * 此处需要根据您的 sdk ndk 路径进行修改
export ANDROID_NDK_ROOT=/home/zhhli/android/android-ndk-r17c

# 2. cmake 命令（需要安装cmake），
# 注意：
# * CMAKE_TOOLCHAIN_FILE ，此处需要根据您的 ndk 路径进行修改
# * CMAKE_GENERATOR="Ninja" , 使用 Ninja，特别此处存在双引号
# * CMAKE_SYSTEM_NAME=Android, 使用 Android
# * DANDROID_ABI ，Android 版本
# * CMAKE_ANDROID_ARCH_ABI ，生成的 静态库 类型
# * 从此处开始到 cmake end ，不能存在注释分割命令行
cmake  ../ \
  -DCMAKE_INSTALL_PREFIX=${BUILD_PATH} \
  -DCMAKE_TOOLCHAIN_FILE=${ANDROID_NDK_ROOT}/build/cmake/android.toolchain.cmake  \
  -DANDROID_ABI=${arch} \
  -DANDROID_TOOLCHAIN=clang \
  -DCMAKE_GENERATOR="Ninja"  \
  -DCMAKE_SYSTEM_NAME=Android \
  -DCMAKE_SYSTEM_VERSION=14 \
  -DCMAKE_ANDROID_NDK=${ANDROID_NDK_ROOT} \
  -DCMAKE_ANDROID_STL_TYPE=gnustl_static \
  -DENABLE_STATIC=ON \
  -DENABLE_SHARED=ON \
  -DCMAKE_BUILD_TYPE=Release \
  -DENABLE_TESTS=OFF \
  -DCMAKE_SKIP_INSTALL_RPATH=ON

  # cmake end


# 3. 使用 ninja 进行编译
ninja
```

### 4.1.2 运行 build_bcg729.sh，进行编译

```bash
$ ./build_bcg729.sh
```

编译日志

```bash
--target=armv7-none-linux-androideabi
--target=aarch64-none-linux-android
--target=i686-none-linux-android
--target=x86_64-none-linux-android

--gcc-toolchain=/home/zhhli/android/android-ndk-r17c/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64
--gcc-toolchain=/home/zhhli/android/android-ndk-r17c/toolchains/aarch64-linux-android-4.9/prebuilt/linux-x86_64
--gcc-toolchain=/home/zhhli/android/android-ndk-r17c/toolchains/x86-4.9/prebuilt/linux-x86_64
--gcc-toolchain=/home/zhhli/android/android-ndk-r17c/toolchains/x86_64-4.9/prebuilt/linux-x86_64
```

### 4.1.3 最终生成的静态库路径

```bash
bcg729\build_out
```

## 5 编译 openssl

OpenSSL 版本为 1.1.1c，注意使用其他1.0.2版本（如1.0.2s）不支持 pjsip-2.9

ndk 版本为 r20编译通过

使用脚本build_openssl_1.1.1c.sh 进行编译

> 修改以下内容

```bash
ANDROID_NDK=/home/zhhli/android/sdk/ndk-bundle
```

输出目录为

```bash
build-openssl-1.1.1c
```

## 6 编译pjsip, 支持 bcg729、OpenSSL

使用脚本 build_pjsip_linux.sh 进行编译

> 修改以下内容

```bash
# 头文件以及lib文件路径
BCG729_PATH=/home/zhhli/work/pjsip-build/bcg729/build-out/build
OPENSSL_PATH=/home/zhhli/work/pjsip-build/build-openssl-1.1.1c

# pjsip 不支持 ndk 16 以上版本
export ANDROID_NDK_ROOT=/home/zhhli/android/android-ndk-r14b
```

输出目录为

```bash
out-pjsip/jniLibs
```

编译日志

```bash
Using SSL prefix... /home/zhhli/work/test/build-openssl-1.1.1c/armeabi-v7a
checking for OpenSSL installations..
checking openssl/ssl.h usability... yes
checking openssl/ssl.h presence... yes
checking for openssl/ssl.h... yes
checking for ERR_load_BIO_strings in -lcrypto... yes
checking for SSL_CTX_new in -lssl... yes
OpenSSL library found, SSL support enabled
checking for EVP_aes_128_gcm in -lcrypto... yes
OpenSSL has AES GCM support, SRTP will use OpenSSL
Checking if OpenCORE AMR support is disabled... yes
Checking if SILK support is disabled... yes
checking for OPUS installations..
checking opus/opus.h usability... no
checking opus/opus.h presence... no
checking for opus/opus.h... no
checking for opus_repacketizer_get_size in -lopus... no
OPUS library not found, OPUS support disabled
Using bcg729 prefix... /home/zhhli/work/pjsip-build/bcg729
checking bcg729 usability... ok

```

