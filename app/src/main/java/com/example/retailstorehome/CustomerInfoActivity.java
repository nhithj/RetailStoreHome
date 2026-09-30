package com.example.retailstorehome;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
public class CustomerInfoActivity extends AppCompatActivity {
 public void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_customer_info);
 EditText name=findViewById(R.id.edtCustomerName),phone=findViewById(R.id.edtCustomerPhone),address=findViewById(R.id.edtCustomerAddress);
  android.content.SharedPreferences data=getSharedPreferences("store_data",MODE_PRIVATE);
  name.setText(data.getString("customer_name", ""));
  phone.setText(data.getString("customer_phone", ""));
  address.setText(data.getString("customer_address", ""));
  if(!data.getString("customer_address", "").isEmpty()) ((Button)findViewById(R.id.btnSaveCustomer)).setText("Lưu thay đổi địa chỉ");
  findViewById(R.id.btnSaveCustomer).setOnClickListener(v->{if(name.getText().toString().trim().isEmpty()||phone.getText().toString().trim().isEmpty()||address.getText().toString().trim().isEmpty()){Toast.makeText(this,"Nhập đủ thông tin nhận hàng",Toast.LENGTH_SHORT).show();return;}getSharedPreferences("store_data",MODE_PRIVATE).edit().putString("customer_name",name.getText().toString()).putString("customer_phone",phone.getText().toString()).putString("customer_address",address.getText().toString()).apply();Toast.makeText(this,"Đã đăng ký thông tin nhận hàng",Toast.LENGTH_SHORT).show();finish();});}
}
