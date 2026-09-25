package com.example.retailstorehome;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
public class CustomerInfoActivity extends AppCompatActivity {
 public void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_customer_info);
  EditText name=findViewById(R.id.edtCustomerName),phone=findViewById(R.id.edtCustomerPhone),address=findViewById(R.id.edtCustomerAddress);
  findViewById(R.id.btnSaveCustomer).setOnClickListener(v->{if(name.getText().toString().trim().isEmpty()||phone.getText().toString().trim().isEmpty()||address.getText().toString().trim().isEmpty()){Toast.makeText(this,"Nhập đủ thông tin nhận hàng",Toast.LENGTH_SHORT).show();return;}getSharedPreferences("store_data",MODE_PRIVATE).edit().putString("customer_name",name.getText().toString()).putString("customer_phone",phone.getText().toString()).putString("customer_address",address.getText().toString()).apply();Toast.makeText(this,"Đã đăng ký thông tin nhận hàng",Toast.LENGTH_SHORT).show();finish();});}
}
