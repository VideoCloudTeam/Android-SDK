package com.example.alan.sdkdemo;

import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tencent.bugly.crashreport.CrashReport;
import com.vcrtc.VCRTCPreferences;
import com.vcrtc.utils.LogUtil;
import com.vcrtc.webrtc.RTCManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ricardo
 * 2019/7/4.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();




        VCRTCPreferences prefs = new VCRTCPreferences(this);

        // rtsp
//        prefs.setCaptureVideoFps(34);
//        prefs.setCameraVideoFps(30);
//        prefs.setVideoFps(30);
//        prefs.setMaxVideoFps(30);
//        prefs.setDisableFrameDropper(true);
//        prefs.setCameraVideoSize(1920, 1080);
//        prefs.setRtspEncoder(true);
//        prefs.setRtspURL("");


        //复制关闭摄像头的图片到手机
        copyCloseVideoImageFromRaw(prefs);

        prefs.setPrintLogs(true);
        LogUtil.startWriteLog(this, false);

        RTCManager.init(this);
        RTCManager.DEVICE_TYPE = "Android";
        RTCManager.OEM = "";
        CrashReport.initCrashReport(getApplicationContext(), "941e592e23", true);


    }

    private void copyCloseVideoImageFromRaw(VCRTCPreferences prefs) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        AssetManager manager = getAssets();
        InputStream inputStream = null;
        try {
            inputStream = manager.open("novideo.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imagePath = getFilesDir().getAbsolutePath() + File.separator + "close_video.png";
//        InputStream inputStream = getResources().openRawResource(R.raw.close_video);
        File file = new File(imagePath);
        try {
            if (!file.exists()) {
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[inputStream.available()];
                int lenght;
                while ((lenght = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, lenght);
                }
                fos.flush();
                fos.close();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        prefs.setImageFilePath(imagePath);
    }
}
