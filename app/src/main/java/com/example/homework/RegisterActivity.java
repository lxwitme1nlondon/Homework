package com.example.homework;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ToastUtils;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //初始化控件
        CheckBox checkBox = findViewById(R.id.checkbox);
        EditText account = findViewById(R.id.account);
        EditText password = findViewById(R.id.password);
        Button register = findViewById(R.id.register);

        //点击注册的点击事件
        register.setOnClickListener(v -> {
            //先判断有无勾选阅读协议
            if (!checkBox.isChecked()) {
                ToastUtils.showShort("你还没有勾选用户协议！");
                return;
            }
            //判断账号密码是否为空
            if (TextUtils.isEmpty(account.getText().toString().trim()) || TextUtils.isEmpty(password.getText().toString().trim())) {
                ToastUtils.showShort("账号或密码为空，请填写完整！");
                return;
            }

            String accountString = account.getText().toString().trim();
            //将用户填写的数据存入数据库
            //先查询有无重复数据
            Cursor cursor = APP.database.query("User", new String[]{"account"}, "account = ?", new String[]{accountString}, null, null, null);
            if (cursor.getCount() == 0){
                //插入数据
                ContentValues contentValues = new ContentValues();
                contentValues.put("account", accountString);
                contentValues.put("password", password.getText().toString().trim());
                APP.database.insert("User", null, contentValues);
                ToastUtils.showShort("注册成功！");
                finish();
            } else ToastUtils.showShort("此用户名已存在！");
            cursor.close();
        });
    }
}