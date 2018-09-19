package com.ysk.mynote;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

//RecyclerView所需的Adapter
public class NoteAdapter extends RecyclerView.Adapter <NoteAdapter.ViewHolder>
        implements View.OnClickListener,View.OnLongClickListener{

    private List<Note> mNoteList;

    private Context mContext;

    private RecyclerViewOnItemClickListener onItemClickListener;
    //多选
    public boolean MUL_tag = false;
    //保存CheckBox选中状态的集合HashMap
    private HashMap<Integer,Boolean> ischecked = new HashMap<Integer, Boolean>();//凡是id对应内容的保存和操作，都用HshMap,此处创建一个HashMap对象名字叫ischecked


    public NoteAdapter (List<Note> noteList){//构造函数，调用此类时先调用该函数
        mNoteList = noteList;
        initMaps();
    }

    public void initMaps(){  //初始化map
        for(int i = 0; i <mNoteList.size(); i++){
            ischecked.put(i,false);//HashMap中的put 方法,key,boolean
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView contentView;
        TextView dateView;
        CheckBox checkBox;

        public ViewHolder(View v){//定义控件的构造函数
            super(v);
            cardView = (CardView)v.findViewById(R.id.cardview);
            contentView = (TextView)v.findViewById(R.id.content);
            dateView = (TextView)v.findViewById(R.id.date);
            checkBox = (CheckBox)v.findViewById(R.id.checkbox);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder (final ViewGroup parent, int viewType){//负责承载每个子项的布局
        if(mContext == null){//上下文为空时，就是无操作时
            mContext = parent.getContext();//ViewGroup parent是指RecycleView的布局
        }
        final View view = LayoutInflater.from(mContext).inflate(R.layout.note_item,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.cardView.setOnClickListener(this);//设置点击监听事件
        viewHolder.cardView.setOnLongClickListener(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){//负责将每个子项holder绑定数据
        Note note = mNoteList.get(position);
        String values = note.getContent();//内容
        holder.contentView.setText(values);
        holder.dateView.setText(new SimpleDateFormat("yyyy/MM/dd    HH:mm:ss").format(note.getDate()));
        //多选状态,CheckBox显示,否则不显示
        if(MUL_tag) {
            holder.checkBox.setVisibility(View.VISIBLE);
        }
        else {
            holder.checkBox.setVisibility(View.INVISIBLE);
        }
        holder.cardView.setTag(position);//注意用setTag保存position信息
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override////判断CheckBox,保存选中信息
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ischecked.put(position,isChecked);
            }
        });
        if(ischecked.get(position) == null)
            ischecked.put(position,false);////CheckBox状态
        holder.checkBox.setChecked(ischecked.get(position));
    }

    @Override
    public int getItemCount(){
        return mNoteList.size();
    }

    @Override
    public void onClick(View v){//设置单击传入position
        if(onItemClickListener != null)
        {
            onItemClickListener.onItemClickListener(v,(Integer)v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v){//长按传入position
        initMaps();
        return onItemClickListener != null && onItemClickListener.onLongClickListener(v,(Integer)v.getTag());
    }
    //创建监听事件
    public void setRecycleViewOnItemClickListener(RecyclerViewOnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    //设置CheckBox显示状态
    public void setCheckBox(){
        MUL_tag = !MUL_tag;
    }
    //设置选中保存状态
    public void setSelection(int position){
        if(ischecked.get(position))
            ischecked.put(position,false);
        else
            ischecked.put(position,true);
        notifyItemChanged(position);
    }
    //getMap()以便MainActivity中获取Map
    public HashMap<Integer,Boolean> getMap(){
        return ischecked;
    }
    //接口,以便MainActivity中进行调用重写
    public interface RecyclerViewOnItemClickListener
    {
        void onItemClickListener(View view, int position);
        boolean onLongClickListener(View view, int position);
    }

}
