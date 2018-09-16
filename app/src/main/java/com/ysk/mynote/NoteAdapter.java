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
    private HashMap<Integer,Boolean> ischecked = new HashMap<Integer, Boolean>();


    public NoteAdapter (List<Note> noteList){
        mNoteList = noteList;
        initMaps();
    }

    public void initMaps(){
        for(int i = 0; i <mNoteList.size(); i++){
            ischecked.put(i,false);//HashMap中的put 方法
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView contentView;
        TextView dateView;
        CheckBox checkBox;

        public ViewHolder(View v){
            super(v);
            cardView = (CardView)v.findViewById(R.id.cardview);
            contentView = (TextView)v.findViewById(R.id.content);
            dateView = (TextView)v.findViewById(R.id.date);
            checkBox = (CheckBox)v.findViewById(R.id.checkbox);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder (final ViewGroup parent, int viewType){
        if(mContext == null){
            mContext = parent.getContext();
        }
        final View view = LayoutInflater.from(mContext).inflate(R.layout.note_item,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.cardView.setOnClickListener(this);
        viewHolder.cardView.setOnLongClickListener(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        Note note = mNoteList.get(position);
        String values = note.getContent();
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
