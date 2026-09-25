package com.example.retailstorehome;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddProductActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    private EditText code, name, price, quantity; private Spinner category; private TextView date; private ImageView image; private Uri selectedImage; private int editingId = -1; private Calendar calendar = Calendar.getInstance();
    @Override public void onCreate(Bundle b) {
        super.onCreate(b); setContentView(R.layout.activity_add_product);
        code=findViewById(R.id.edtCode); name=findViewById(R.id.edtName); price=findViewById(R.id.edtPrice); quantity=findViewById(R.id.edtQuantity); category=findViewById(R.id.spinnerCategory); date=findViewById(R.id.tvDate); image=findViewById(R.id.imgProduct);
        editingId=getIntent().getIntExtra("product_id", -1);
        if (editingId >= 0 && ProductRepository.exists(this,editingId)) fillForEdit();
        findViewById(R.id.btnChooseDate).setOnClickListener(v -> pickDate());
        findViewById(R.id.btnChooseImage).setOnClickListener(v -> pickImage());
        findViewById(R.id.btnSave).setOnClickListener(v -> save());
    }
    private void fillForEdit() {
        ((TextView)findViewById(R.id.addProductTitle)).setText("SỬA SẢN PHẨM"); ((Button)findViewById(R.id.btnSave)).setText("Cập nhật sản phẩm");
        code.setText(ProductRepository.getCode(this,editingId)); name.setText(ProductRepository.getName(this,editingId)); price.setText(String.valueOf(ProductRepository.getPrice(this,editingId))); quantity.setText(String.valueOf(ProductRepository.getQuantity(this,editingId)));
        String wanted=ProductRepository.getCategory(this,editingId); for(int i=0;i<category.getCount();i++) if(category.getItemAtPosition(i).toString().equals(wanted)) category.setSelection(i);
        String uri=ProductRepository.getImageUri(this,editingId); if(!uri.isEmpty()){selectedImage=Uri.parse(uri);image.setImageURI(selectedImage);}
    }
    private void pickDate() { new DatePickerDialog(this,(v,y,m,d)->{calendar.set(y,m,d); date.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime()));date.setError(null);},calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show(); }
    private void pickImage() { Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT); i.setType("image/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,PICK_IMAGE); }
    @Override protected void onActivityResult(int r,int c,Intent d){
        super.onActivityResult(r,c,d);
        if(r==PICK_IMAGE&&c==RESULT_OK&&d!=null){
            selectedImage=d.getData();
            try {
                getContentResolver().takePersistableUriPermission(selectedImage,
                        d.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
            } catch (Exception ignored) { }
            image.setImageURI(selectedImage);
        }
    }
    private void save() {
        String c=code.getText().toString().trim(), n=name.getText().toString().trim(), p=price.getText().toString().trim(), q=quantity.getText().toString().trim(); boolean ok=true;
        if(c.isEmpty()){code.setError("Mã sản phẩm không được để trống");ok=false;} if(n.isEmpty()){name.setError("Tên sản phẩm không được để trống");ok=false;}
        long value=0; try{value=Long.parseLong(p);if(value<=0){price.setError("Giá phải lớn hơn 0");ok=false;}}catch(Exception e){price.setError("Giá phải là số hợp lệ");ok=false;}
        int stock=0; try{stock=Integer.parseInt(q);if(stock<0){quantity.setError("Số lượng không được nhỏ hơn 0");ok=false;}}catch(Exception e){quantity.setError("Số lượng phải là số hợp lệ");ok=false;}
        if(category.getSelectedItemPosition()==0){Toast.makeText(this,"Hãy chọn loại sản phẩm",Toast.LENGTH_SHORT).show();ok=false;} if(date.getText().toString().equals(getString(R.string.choose_import_date))){date.setError("Hãy chọn ngày nhập");ok=false;} if(!ok)return;
        String status=((RadioButton)findViewById(R.id.rbSelling)).isChecked()?"Đang bán":"Ngừng bán"; String photo=selectedImage==null?"":selectedImage.toString();
        if(editingId<0){ProductRepository.create(this,c,n,value,stock,category.getSelectedItem().toString(),date.getText().toString(),status,photo);Toast.makeText(this,"Đã thêm sản phẩm vào danh sách",Toast.LENGTH_SHORT).show();}
        else {ProductRepository.save(this,editingId,c,n,value,stock,category.getSelectedItem().toString(),date.getText().toString(),status,photo);Toast.makeText(this,"Đã cập nhật sản phẩm",Toast.LENGTH_SHORT).show();}
        finish();
    }
}
