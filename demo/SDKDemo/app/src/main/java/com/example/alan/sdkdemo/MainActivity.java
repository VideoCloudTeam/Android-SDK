package com.example.alan.sdkdemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alan.sdkdemo.ui.ZJConferenceActivity;
import com.vcrtc.VCRTCPreferences;
import com.vcrtc.callbacks.CallBack;
import com.vcrtc.entities.Call;
import com.vcrtc.utils.CheckUtils;
import com.vcrtc.utils.OkHttpUtil;
import com.vcrtc.utils.SystemUtil;
import com.vcrtc.utils.VCUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.et_nickname)
    EditText etNickname;
    @BindView(R.id.et_meet_num)
    EditText etMeetNum;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.btn_connect)
    Button btnConnect;
    @BindView(R.id.btn_setting)
    Button btnSetting;
    private final int REQUEST_PERMISSION = 1000;
    private final int OVERLAY_PERMISSION_REQ_CODE = 1001;
    private VCRTCPreferences vcPrefs;
    private final int REQUEST_SUCCESS = 0;
    private final int REQUEST_FAILED = 1;
    private MainHandler mainHandler;
    Call call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        vcPrefs = new VCRTCPreferences(getApplicationContext());
        call = new Call();
        mainHandler = new MainHandler();
        new Handler().postDelayed(this::checkPermission, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.btn_connect)
    public void onClick() {
        checkUrl(tvAddress.getText().toString());
    }

    private void goToConference() {

        call.setNickname(etNickname.getText().toString());
        call.setChannel(etMeetNum.getText().toString());
        call.setPassword(etPassword.getText().toString());
        call.setCheckDup(VCUtil.MD5(SystemUtil.getMac(this) + call.getNickname()));
        call.setHideMe(false);
        Intent intent = new Intent(this, ZJConferenceActivity.class);
        intent.putExtra("call", call);
        startActivity(intent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @OnClick({R.id.btn_setting, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                break;
            default:
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        PackageManager pm = getPackageManager();
        String pkgName = this.getPackageName();
        boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, pkgName)
                && PackageManager.PERMISSION_GRANTED == pm.checkPermission(Manifest.permission.READ_PHONE_STATE, pkgName)
                && PackageManager.PERMISSION_GRANTED == pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, pkgName));
        if (!permission) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.main_tips);
                builder.setMessage(getString(R.string.main_request_system_alert_window));
                //设置确定按钮
                builder.setPositiveButton(R.string.main_go_setting, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + pkgName));
                    startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                    dialog.dismiss(); //关闭dialog
                });
                builder.setCancelable(false);

                builder.create();
                builder.show();
            }
        }
    }

    /**
     * 检测会议室号码或密码是否正确
     * @param num
     * @param apiServer
     */
    private void loadInfoAndCall(String num, String apiServer) {

        CheckUtils.checkConference(num, apiServer, new CheckUtils.CheckConferenceListener() {
            @Override
            public void onSuccess(String s) {
                Message msg = new Message();
                msg.what = REQUEST_SUCCESS;
                Bundle bundle = new Bundle();
                bundle.putString("rep", s);
                msg.setData(bundle);
                mainHandler.sendMessage(msg);
            }

            @Override
            public void onFail(Exception e) {
                Message msg = new Message();
                msg.what = REQUEST_FAILED;
                mainHandler.sendMessage(msg);
            }
        });

    }


    class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_SUCCESS) {
                String rep = msg.getData().getString("rep");
                try {
                    JSONObject jsonObject = new JSONObject(rep);
                    int statusCode = jsonObject.optInt("code");
                    if (statusCode == 200) {
                        String hostPwd = jsonObject.optString("hostpwd");
                        String guestPwd = jsonObject.optString("guestpwd");
                        if (hostPwd.equals(etPassword.getText().toString()) || guestPwd.equals(etPassword.getText().toString())) {
                            goToConference();
                        } else {
                            displayMessage("会议号码或密码错误");
                        }
                    } else {
                        displayMessage("会议号码或密码错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (msg.what == REQUEST_FAILED) {
                displayMessage("请求失败");
            }
            super.handleMessage(msg);
        }
    }

    private void checkUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            displayMessage("fail");
            return;
        }

        vcPrefs.setServerAddress(url, "443", new CallBack() {
            @Override
            public void success(String s) {
                mainHandler.post(() -> {
                    loadInfoAndCall(etMeetNum.getText().toString(), tvAddress.getText().toString());
                });
            }

            @Override
            public void failure(String s) {
                displayMessage(s);
            }
        });
    }

    private void displayMessage(String message) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show());

    }

}
