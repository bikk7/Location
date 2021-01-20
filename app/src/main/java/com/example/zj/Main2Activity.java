package com.example.zj;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sql.Operation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Main2Activity extends AppCompatActivity {
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //如果已经输入过电话号码，则直接结束这个活动
        FileInputStream in =null;
        BufferedReader reader=null;
        StringBuilder content=new StringBuilder();
        try {
            in=openFileInput("phonenumber");
            reader=new BufferedReader(new InputStreamReader(in));
            //String line="";
            while((reader.readLine())!=null)
            {
                Intent intent =new Intent(Main2Activity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //view层的控件和业务层的控件，靠id关联和映射  给btn1赋值，即设置布局文件中的Button按钮id进行关联
        Button btn1=(Button)findViewById(R.id.btn1);
        editText=(EditText) findViewById(R.id.editText);

        //给btn1绑定监听事件
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 给bnt1添加点击响应事件
                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
               final String inputtext = editText.getText().toString();
                if (inputtext.length() != 11) {
                    Toast.makeText(getApplicationContext(), "请输入11位电话号码", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Operation.Insert1(inputtext);
                        }
                    }).start();

                    intent.putExtra("phone", inputtext);
                    //启动
                    startActivity(intent);
                    //存储输入的电话号码
                    FileOutputStream out = null;
                    BufferedWriter writer = null;

                    try {
                        out = openFileOutput("phonenumber", Context.MODE_PRIVATE);
                        writer = new BufferedWriter(new OutputStreamWriter(out));
                        try {
                            writer.write(inputtext);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (writer != null) {
                            try {
                                writer.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    finish();
                }
            }
        });
        }
}
