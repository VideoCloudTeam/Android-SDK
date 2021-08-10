package com.example.alan.sdkdemo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Process;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.qw.soul.permission.SoulPermission;
import com.tencent.bugly.crashreport.CrashReport;
import com.vcrtc.VCRTCPreferences;
import com.vcrtc.utils.LogUtil;
import com.vcrtc.utils.OkHttpUtil;
import com.vcrtc.webrtc.RTCManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
        OkHttpUtil.setApplicationContext(this);
        if (isAppMainProcess()){
            // 设置开发者token和deviceId
            prefs.setDeviceId("");
            prefs.setToken("");
            //复制关闭摄像头的图片到手机
            copyCloseVideoImageFromRaw(prefs);

        }
        prefs.setPrintLogs(true);
        LogUtil.startWriteLog(this, false);
        SoulPermission.init(this);
        RTCManager.init(this);
        RTCManager.DEVICE_TYPE = "Android";
        RTCManager.OEM = "";
        CrashReport.initCrashReport(getApplicationContext(), "941e592e23", true);


    }

    private boolean isAppMainProcess(){
        String process = getProcessName(this);
        return TextUtils.isEmpty(process) || BuildConfig.APPLICATION_ID.equalsIgnoreCase(process);
    }

    public static String getProcessName(Context cxt) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
