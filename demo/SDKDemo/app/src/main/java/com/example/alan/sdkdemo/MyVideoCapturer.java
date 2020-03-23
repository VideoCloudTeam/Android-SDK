package com.example.alan.sdkdemo;

import com.vcrtc.VCVideoCapturer;
import com.vcrtc.VCVideoFrameConsumer;

public class MyVideoCapturer extends VCVideoCapturer {

    private VCVideoFrameConsumer consumer;

    @Override
    public void onInitialize(VCVideoFrameConsumer vcVideoFrameConsumer) {
        consumer = vcVideoFrameConsumer;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDispose() {

    }

    private void sendData() {

    }
}
