package com.example.retailstorehome;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText fullName, username, phone, password, confirmPassword;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_register);
        fullName=findViewById(R.id.edtRegisterName);
        username=findViewById(R.id.edtRegisterUser);
        phone=findViewById(R.id.edtRegisterPhone);
        password=findViewById(R.id.edtRegisterPassword);
        confirmPassword=findViewById(R.id.edtRegisterConfirm);
        findViewById(R.id.btnRegister).setOnClickListener(v -> register());
    }

    private void register() {
        String name=fullName.getText().toString().trim();
        String user=username.getText().toString().trim();
        String mobile=phone.getText().toString().trim();
        String pass=password.getText().toString();
        String confirm=confirmPassword.getText().toString();
        boolean valid=true;
        if(name.length()<2){fullName.setError("Hãy nhập họ tên");valid=false;}
        if(user.length()<4){username.setError("Tên đăng nhập cần ít nhất 4 ký tự");valid=false;}
        if(user.equalsIgnoreCase("admin")||user.equalsIgnoreCase("khachhang")){username.setError("Tên đăng nhập đã tồn tại");valid=false;}
        if(!mobile.matches("0[0-9]{9}")){phone.setError("Số điện thoại phải gồm 10 số và bắt đầu bằng 0");valid=false;}
        if(pass.length()<6){password.setError("Mật khẩu cần ít nhất 6 ký tự");valid=false;}
        if(!pass.equals(confirm)){confirmPassword.setError("Mật khẩu xác nhận không khớp");valid=false;}
        if(!valid)return;
        SharedPreferences data=getSharedPreferences("store_data",MODE_PRIVATE);
        if(user.equalsIgnoreCase(data.getString("registered_username",""))){username.setError("Tên đăng nhập đã tồn tại");return;}
        data.edit().putString("registered_name",name).putString("registered_username",user)
                .putString("registered_phone",mobile).putString("registered_password",pass)
                .putString("customer_name",name).putString("customer_phone",mobile).apply();
        Toast.makeText(this,"Tạo tài khoản thành công, hãy đăng nhập",Toast.LENGTH_LONG).show();
        finish();
    }
}
