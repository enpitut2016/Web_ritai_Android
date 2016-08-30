package com.example.kasasasu;

/**
 * Created by 真史 on 2016/08/27.
 */
public class Setting {
    // 名前
    private String name;
    // 内容
    private String content;

    public Setting(String name, String content){
        super();

        this.name = name;
        this.content = content;
    }

    public String getName(){
        return this.name;
    }

    public String getContent(){
        return this.content;
    }
}
