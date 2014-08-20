package com.codebrahma.kamesh.todo;

/**
 * Created by kamesh on 19/8/14.
 */

public class Todo {
    String text;
    Boolean status;
    Integer position;

    public Todo(String text, Boolean status, Integer position){
        setText(text);
        setStatus(status);
        setPosition(position);
    }

    public void setText(String text){
        this.text = text;
    }

    public void setStatus(Boolean status){
        this.status = status;
    }

    public void setPosition(Integer position){
        this.position = position;
    }

}

