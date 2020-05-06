package com.picovr.androidcollection.entity;

public abstract class TestAction {
    private String name;
    private int position;

    public TestAction(String name, int position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public abstract void action();

}
