package com.ysk.mynote;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Date;

//传递对象要接入Serializable接口，这是一个javabean,对应数据库的note表
public class Note extends DataSupport implements Serializable {
    private int id;
    private String content;
    private Date date;
    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


}
