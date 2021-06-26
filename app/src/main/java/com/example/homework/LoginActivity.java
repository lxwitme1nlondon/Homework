package com.example.homework;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private final ArrayList<UserInfo> userInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //先判断有无读取内存权限
        PermissionUtils.permission(PermissionConstants.STORAGE)
                .callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {
                    }

                    @Override
                    public void onDenied() {
                        ToastUtils.showShort("你拒绝了读取内存权限将无法获取本地歌曲！");
                        finish();
                    }
                })
                .request();

        //实例化控件
        TextView register = findViewById(R.id.register);
        EditText account = findViewById(R.id.account);
        EditText password = findViewById(R.id.password);
        Button login = findViewById(R.id.login);

        //点击注册的点击事件
        register.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        //点击登录的点击事件
        login.setOnClickListener(v -> {
            //先对用户已输数据进行判空
            if (TextUtils.isEmpty(account.getText().toString().trim()) || TextUtils.isEmpty(password.getText().toString().trim())) {
                ToastUtils.showShort("账号或密码为空，请填写完整！");
                return;
            }
            //对账号密码进行判断
            UserInfo userInfo = new UserInfo(account.getText().toString().trim(), password.getText().toString().trim());
            boolean isSuccess = false;
            for (UserInfo info : userInfoList) {
                isSuccess = info.account.equals(userInfo.account) && info.password.equals(userInfo.password);
                if (isSuccess) break;
            }
            if (!isSuccess) ToastUtils.showShort("用户名或密码输入错误！");
            else {
                //输入正确跳转到主页 并将数据传过去
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("userInfoList", GsonUtils.toJson(userInfoList));
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userInfoList.clear();
        // 查询User表中所有的数据
        Cursor cursor = APP.database.query("User", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                // 遍历Cursor对象，取出数据并打印
                String account = cursor.getString(cursor.getColumnIndex("account"));
                String password = cursor.getString(cursor.getColumnIndex("password"));
                UserInfo userInfo = new UserInfo(account, password);
                userInfoList.add(userInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}