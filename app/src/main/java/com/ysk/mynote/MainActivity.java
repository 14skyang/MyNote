package com.ysk.mynote;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private int status;//判断读写
    public static final int WRITE = 1;// 1是新增便签,2是读取已有便签
    public static final int READ = 2;
    private RecyclerView recyclerView;
    private List<Note> noteList;//便签表
    private List<Note> deleteList;
    private NoteAdapter adapter;
    private ActionBar actionBar;
    private boolean showboxTag = false;//判断是否显示CheckBox



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        final ImageButton deletebtn = (ImageButton)findViewById(R.id.menu1);
        final ImageButton calendarbtn=(ImageButton)findViewById(R.id.calendar);
        TextView title = (TextView)findViewById(R.id.title);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setBackgroundColor(Color.YELLOW);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        //左上角菜单键
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu3);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        noteList = DataSupport.order("date desc").find(Note.class);//便签表映射到数据库，指定按照日期（date）的排序方式：desc表示降序排序;asc或者不写表示升序
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,WriteActivity.class);
                status = 1;
                intent.putExtra("Status", status);//传递信息
                startActivity(intent);
            }
        });
        calendarbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,CalendarActivity.class);
                startActivity(intent);
            }
        });
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);//创建dialog
                dialog.setTitle("删除便签");
                dialog.setMessage("您确定要删除所选便签？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获取保存通过CheckBox选中的便签,选中状态由adapter中的map保存,通过getMap()获取
                        Map<Integer,Boolean> map = adapter.getMap();//因为按照id对应内容，所以用Map保存
                        //从后往前删除
                        for(int i=map.size()-1; i>=0; i--){
                            if(map.get(i)){
                                int id = noteList.get(i).getId();//i为CheckBox选中的view的position，通过getId()赋为id
                                deleteData(id);
                                //noteList中移除
                                noteList.remove(i);
                                //adapter重新设置item
                                adapter.notifyItemRemoved(i);//recyclerView的方法

                            }

                        }
                        //adapter长度重新设置
                        adapter.notifyItemRangeChanged(0,noteList.size());
                        //删除后回到正常状态,CheckBox不显示,map重新归false
                        adapter.MUL_tag  = false;
                        adapter.initMaps();
                        showboxTag = false;
                        adapter.notifyDataSetChanged();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();//取消dialog
                    }
                });


                dialog.show();



            }
        });

        deletebtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //delete键按下后背景发生变化
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    deletebtn.setBackgroundColor(getResources().getColor(R.color.colorbg1));
                else if(event.getAction() == MotionEvent.ACTION_UP)
                    deletebtn.setBackgroundColor(getResources().getColor(R.color.colorbg2));
                return false;
            }
        });
        //创建adapter对象
        adapter = new NoteAdapter(noteList);
        //设置监听
        adapter.setRecycleViewOnItemClickListener(new NoteAdapter.RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                Intent intent = new Intent(MainActivity.this,WriteActivity.class);//跳转到便签详情页
                Note note = noteList.get(position);
                status = 2;//读   状态
                intent.putExtra("Status", status);
                intent.putExtra("Content",note);//传递对象
                startActivity(intent);


            }
            //长按时间
            @Override
            public boolean onLongClickListener(View view, int position) {
                //长按显示CheckBox,并且长按位置选中该便签
                adapter.setSelection(position);
                adapter.setCheckBox();
                adapter.notifyDataSetChanged();
                showboxTag = true;
                return true;
            }
        });
        //瀑布流布局
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onResume(){
        super.onResume();
        status = 0;//status归零
        noteList.clear();//noteList清空
        List<Note> newList = DataSupport.order("date desc").find(Note.class);//创建新list读取并按时间倒序排列
        noteList.addAll(newList);//将新list加入到noteList中
        adapter.MUL_tag  = false;//adapter中CheckBox初始化
        adapter.initMaps();
        showboxTag = false;
        adapter.notifyDataSetChanged();
    }

    public void deleteData(int id){
        DataSupport.deleteAll(Note.class,"id = ?",String.valueOf(id));

    }
    @Override
    public void onBackPressed(){
        //处于多选状态,按下返回键回到正常状态
        if(showboxTag){
            adapter.MUL_tag  = false;
            adapter.initMaps();
            adapter.notifyDataSetChanged();
            showboxTag = false;
        }
        else//否则调用父类方法
            super.onBackPressed();
    }


}
