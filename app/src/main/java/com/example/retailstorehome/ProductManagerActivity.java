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
        ImageView image=new ImageView(this);image.setScaleType(ImageView.ScaleType.FIT_CENTER);image.setBackgroundColor(getColor(R.color.white));ProductRepository.showImage(image,this,id);info.addView(image,new LinearLayout.LayoutParams(86,86));
        TextView title=new TextView(this);title.setText(ProductRepository.getName(this,id)+"\nMã: "+ProductRepository.getCode(this,id)+"\n"+String.format("%,d VNĐ",ProductRepository.getPrice(this,id)).replace(',','.')+" • Còn: "+ProductRepository.getQuantity(this,id)+"\n"+ProductRepository.getAvailabilityText(this,id)+"\nThời gian thêm: "+ProductRepository.getDate(this,id));title.setTextSize(15);title.setTextColor(ProductRepository.isAvailable(this,id)?getColor(R.color.text_primary):0xFFC62828);title.setPadding(dp(14),0,0,0);info.addView(title,new LinearLayout.LayoutParams(0,-2,1));row.addView(info);
        LinearLayout buttons=new LinearLayout(this);buttons.setGravity(Gravity.END);buttons.setPadding(0,dp(8),0,0);
        ImageButton edit=new ImageButton(this);edit.setImageResource(R.drawable.ic_edit);edit.setBackgroundResource(R.drawable.bg_icon_button);edit.setContentDescription("Sửa sản phẩm");edit.setPadding(dp(8),dp(8),dp(8),dp(8));
        ImageButton delete=new ImageButton(this);delete.setImageResource(R.drawable.ic_delete);delete.setBackgroundResource(R.drawable.bg_icon_button);delete.setContentDescription("Xóa sản phẩm");delete.setPadding(dp(8),dp(8),dp(8),dp(8));
        LinearLayout.LayoutParams iconParams=new LinearLayout.LayoutParams(dp(42),dp(42));buttons.addView(edit,iconParams);LinearLayout.LayoutParams deleteParams=new LinearLayout.LayoutParams(dp(42),dp(42));deleteParams.leftMargin=dp(8);buttons.addView(delete,deleteParams);row.addView(buttons);
        edit.setOnClickListener(v->{Intent i=new Intent(this,AddProductActivity.class);i.putExtra("product_id",id);startActivity(i);});
        delete.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Xóa sản phẩm").setMessage("Bạn muốn xóa "+ProductRepository.getName(this,id)+"?").setNegativeButton("Hủy",null).setPositiveButton("Xóa",(d,w)->{ProductRepository.delete(this,id);render();}).show());
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.topMargin=12;list.addView(row,p);
    }
    private int dp(int value){return Math.round(value*getResources().getDisplayMetrics().density);}
}
