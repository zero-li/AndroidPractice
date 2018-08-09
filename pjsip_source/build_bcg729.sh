# cmake ./src \
export PATH=/d/CMake/bin:/d/Android/sdk/cmake/3.6.4111459/bin/:$PATH
export ANDROID_NDK_ROOT=/d/Android/sdk/ndk-bundle

cmake  ./bcg729 \
  -DCMAKE_INSTALL_PREFIX=./android \
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
  #-DCMAKE_C_FLAGS = -gcc-toolchain /d/Android/sdk/ndk-bundle/toolchains/arm-linux-androideabi-4.9/prebuilt/windows-x86_64 -fpic -ffunction-sections -funwind-tables -Wno-invalid-command-line-argument -Wno-unused-command-line-argument -no-canonical-prefixes -fno-integrated-as -target armv7-none-linux-androideabi17 -march=armv7-a -mfloat-abi=softfp -mfpu=vfpv3-d16 -mthumb -Os -DNDEBUG -Ijni -DANDROID -D__ANDROID_API__=17 -Wa,--noexecstack -Wformat -Werror=format-security --sysroot /d/Android/sdk/ndk-bundle/platforms/android-17/arch-arm/ -isystem /d/Android/sdk/ndk-bundle/sysroot/usr/include/arm-linux-androideabi -I/d/Android/sdk/ndk-bundle/sources/cxx-stl/llvm-libc++/include \
  #-DCMAKE_C_COMPILER = /d/Android/sdk/ndk-bundle/toolchains/llvm/prebuilt/linux-x86_64/bin/clang/ \

ninja


