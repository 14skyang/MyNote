package com.ysk.mynote;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.Date;

public class WriteActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private int noteID;
    private int status;
    private EditText contentText;
    private ImageButton menuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        contentText = (EditText)findViewById(R.id.editText);
        menuView = (ImageButton) findViewById(R.id.menu2);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        Intent intent = getIntent();
        status = intent.getIntExtra("Status",0);
        if(status == MainActivity.READ){
            //读操作
            Note note = (Note)intent.getSerializableExtra("Content");
            //显示内容和游标放到内容末端
            contentText.setText(note.getContent());
            contentText.setSelection(note.getContent().length());
            //获取id
            noteID = note.getId();
        }
        //按钮按下背景变化
        menuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    menuView.setBackgroundColor(getResources().getColor(R.color.colorbg1));
                else if(event.getAction() == MotionEvent.ACTION_UP)
                    menuView.setBackgroundColor(getResources().getColor(R.color.colorbg2));
                return false;
            }
        });

        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //新增便签
                if(status == MainActivity.WRITE){
                    Intent intent1 = new Intent(WriteActivity.this,MainActivity.class);
                    startActivity(intent1);
                    saveData();//保存数据
                }
                else if(status == MainActivity.READ){
                    //读取便签后更新数据
                    Intent intent1 = new Intent(WriteActivity.this,MainActivity.class);
                    startActivity(intent1);
                    updateData();//更新数据

                }
                finish();
            }
        });
    }
    //左上角home键
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(WriteActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }


    public void updateData(){//更新数据
        if(!(contentText.getText().toString().equals(""))){
            Note note = new Note();
            note.setDate(new Date());
            note.setContent(contentText.getText().toString());
            note.update(noteID);
        }

    }

    public void saveData(){//保存数据
        if(!(contentText.getText().toString().equals("")) ){
            Note note = new Note();
            note.setDate(new Date());
            note.setContent(contentText.getText().toString());
            note.save();
        }

    }
}

