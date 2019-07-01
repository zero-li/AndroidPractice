

ANDROID_NDK_ROOT=/home/zhhli/android/android-ndk-r17c
BCG729_VERSION=bcg729

OUTPUT_DIR="build-out"

archs=(armeabi-v7a arm64-v8a x86 x86_64)

# =========================================================
export ANDROID_NDK_ROOT


rm -rf ${BCG729_VERSION}
unzip ${BCG729_VERSION}.zip

cd ${BCG729_VERSION}

ANDROID_LIB_ROOT=../${OUTPUT_DIR}

# archs=(armeabi-v7a)

for arch in ${archs[@]}; do

    BUILD_PATH=android

    rm -rf ${BUILD_PATH}
    
    mkdir -p ${BUILD_PATH}

    cd ${BUILD_PATH}

    

    echo "============================================================="
    echo "building >>>>>>   ${arch}"
    pwd
    echo ""
    
    

    # https://developer.android.google.cn/ndk/guides/cmake
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
    

    ninja
    # ninja -v # log

    if [ $? -ne 0 ]; then
		  echo "Error executing ninja for platform:${arch}"
		  exit 1
	  fi

    BUILD_OUT=${ANDROID_LIB_ROOT}/build/${arch}


    
    echo ""
    echo "out path >>>>>>   ${BUILD_OUT}"
    echo ""
    
    mkdir -p "${BUILD_OUT}/lib"
    mkdir -p "${BUILD_OUT}/include/bcg729"
    
    cp src/*.so "${BUILD_OUT}/lib"

    cp -r ../include/bcg729/*.h "${BUILD_OUT}/include/bcg729"


    BUILD_OUT_JNI=${ANDROID_LIB_ROOT}/jniLibs/${arch}

    mkdir -p ${BUILD_OUT_JNI}

    cp src/*.so ${BUILD_OUT_JNI}

    cd ..


done




