package com.example.retailstorehome;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText user = findViewById(R.id.edtUser);
        EditText password = findViewById(R.id.edtPassword);
        Button login = findViewById(R.id.btnLoginNow);
        login.setOnClickListener(v -> {
            String username = user.getText().toString().trim();
            String pass = password.getText().toString();
            if (username.equals("admin") && pass.equals("123")) {
                SharedPreferences data = getSharedPreferences("store_data", MODE_PRIVATE);
                data.edit().putBoolean("seller_logged_in", true).putBoolean("customer_logged_in", false).apply();
                Toast.makeText(this, "Đăng nhập người bán thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else if (username.equals("khachhang") && pass.equals("123")) {
                SharedPreferences data = getSharedPreferences("store_data", MODE_PRIVATE);
                data.edit().putBoolean("customer_logged_in", true).putBoolean("seller_logged_in", false).apply();
                Toast.makeText(this, "Đăng nhập khách hàng thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu. Dùng admin/123 hoặc khachhang/123", Toast.LENGTH_LONG).show();
            }
        });
    }
}
