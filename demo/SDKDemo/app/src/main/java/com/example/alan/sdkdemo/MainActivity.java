package com.example.alan.sdkdemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alan.sdkdemo.ui.ZJConferenceActivity;
import com.vcrtc.VCRTCPreferences;
import com.vcrtc.entities.Call;
import com.vcrtc.utils.OkHttpUtil;

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
    Call call;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        call = new Call();
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

    private void goToConference(){
        call.setApiServer(tvAddress.getText().toString());
        call.setNickname(etNickname.getText().toString());
        call.setChannel(etMeetNum.getText().toString());
        call.setPassword(etPassword.getText().toString());

        Intent intent = new Intent(this, ZJConferenceActivity.class);
        intent.putExtra("call", call);
        startActivity(intent);
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

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION && grantResults.length >= 2) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            }
            if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            }
        } else if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                }
            }
        }
    }

    private Handler handler = new Handler();

    private void checkUrl(String url){
        if (TextUtils.isEmpty(url)) {
            displayMessage("fail");
            return;
        }

        String httpUrl = String.format("https://%s/api/v3/app/getPlatform", url);
        try {
            OkHttpUtil.doGet(httpUrl, new Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    displayMessage("fail");
                }

                @Override
                public void onResponse(okhttp3.Call c, Response response) throws IOException {
                    String resultJson = response.body().string();
                    try {
                        JSONObject result = new JSONObject(resultJson);
                        if (result.optInt("code") == 200) {
                            String dataJson = result.optString("data");
                            JSONObject data = new JSONObject(dataJson);
                            String platform = data.optString("platform");
                            if ("shitong".equals(platform)) {
                                call.setShitongPlatform(true);
                            } else if ("yunshi".equals(platform)) {
                                call.setShitongPlatform(false);
                            }
                            handler.post(() -> {
                                goToConference();
                            });
                        } else {
                            displayMessage("fail");

                        }
                    } catch (JSONException e) {
                        displayMessage("fail");

                    }
                }
            });
        } catch (Exception e) {
            displayMessage("fail");

        }
    }

    private void displayMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
