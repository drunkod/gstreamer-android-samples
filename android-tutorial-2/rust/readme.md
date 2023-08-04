https://github.com/rust-mobile/rust-android-examples/tree/main/na-subclass-jni

https://github.com/drunkod/rust-android-examples/tree/main/na-egui
Это тесты с использованием NativeActivity egui, winit и wgpu.


export ANDROID_NDK_HOME="path/to/ndk"
export ANDROID_HOME="path/to/sdk"

rustup target add aarch64-linux-android
cargo install cargo-ndk

[//]: # (cargo ndk -t arm64-v8a -o app/src/main/jniLibs/  build)
PKG_CONFIG_ALLOW_CROSS=1 cargo ndk -t arm64-v8a -o ./../src/main/jniLibs/  build
