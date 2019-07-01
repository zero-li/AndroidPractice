PJSIP_VERSION="pjproject-2.9"

BCG729_PATH=/home/zhhli/work/pjsip-build/bcg729/build-out/build
OPENSSL_PATH=/home/zhhli/work/pjsip-build/build-openssl-1.1.1c




rm -rf ${PJSIP_VERSION}

#extract (every run empty)
tar -xjf ${PJSIP_VERSION}.tar.bz2
# unzip ${PJSIP_VERSION}.zip

cd ${PJSIP_VERSION}

# when use .zip file
# dos2unix configure-android
# sudo chmod -R 777 ./

################ 
# https://trac.pjsip.org/repos/wiki/Getting-Started/Android

export ANDROID_NDK_ROOT=/home/zhhli/android/android-ndk-r14b
# export ANDROID_NDK_ROOT=/home/zhhli/android/sdk/ndk-bundle





archs=(armeabi-v7a arm64-v8a x86 x86_64)
# archs=(armeabi-v7a)

# for ANDROID_TARGET_PLATFORM in android-arm android-arm64 android-x86 android-x86_64
for arch in ${archs[@]}; do

NDK_TOOLCHAIN_VERSION=4.9 \
TARGET_ABI=${arch} \
APP_PLATFORM=android-14 \
./configure-android \
        --use-ndk-cflags --with-bcg729=${BCG729_PATH}/${arch} \
        --with-ssl=${OPENSSL_PATH}/${arch}




make dep && make clean && make


cd pjsip-apps/src/swig/
make clean && make

cd -

done

mkdir -p ../out-pjsip


cp -r pjsip-apps/src/swig/java/android/app/src/main/jniLibs ../out-pjsip
