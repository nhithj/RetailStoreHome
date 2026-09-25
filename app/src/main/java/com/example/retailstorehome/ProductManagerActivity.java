package com.example.retailstorehome;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProductManagerActivity extends AppCompatActivity {
    @Override public void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_product_manager);findViewById(R.id.btnManagerAdd).setOnClickListener(v->startActivity(new Intent(this,AddProductActivity.class)));}
    @Override public void onResume(){super.onResume();render();}
    private void render(){LinearLayout list=findViewById(R.id.managerProductList);list.removeAllViews();for(int id:ProductRepository.getAllIds(this))addRow(list,id);}
    private void addRow(LinearLayout list,int id){
        LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.VERTICAL);row.setPadding(18,18,18,18);row.setBackgroundResource(R.drawable.bg_product_card);
        LinearLayout info=new LinearLayout(this);info.setGravity(Gravity.CENTER_VERTICAL);
        ImageView image=new ImageView(this);image.setScaleType(ImageView.ScaleType.CENTER_CROP);ProductRepository.showImage(image,this,id);info.addView(image,new LinearLayout.LayoutParams(86,86));
        TextView title=new TextView(this);title.setText(ProductRepository.getName(this,id)+"\n"+String.format("%,d VNĐ",ProductRepository.getPrice(this,id)).replace(',','.')+" • Còn: "+ProductRepository.getQuantity(this,id));title.setTextSize(17);title.setTextColor(getColor(R.color.text_primary));title.setPadding(18,0,0,0);info.addView(title,new LinearLayout.LayoutParams(0,-2,1));row.addView(info);
        LinearLayout buttons=new LinearLayout(this);buttons.setGravity(Gravity.END);Button edit=new Button(this);edit.setText("Sửa");Button delete=new Button(this);delete.setText("Xóa");buttons.addView(edit);buttons.addView(delete);row.addView(buttons);
        edit.setOnClickListener(v->{Intent i=new Intent(this,AddProductActivity.class);i.putExtra("product_id",id);startActivity(i);});
        delete.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Xóa sản phẩm").setMessage("Bạn muốn xóa "+ProductRepository.getName(this,id)+"?").setNegativeButton("Hủy",null).setPositiveButton("Xóa",(d,w)->{ProductRepository.delete(this,id);render();}).show());
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.topMargin=12;list.addView(row,p);
    }
}
