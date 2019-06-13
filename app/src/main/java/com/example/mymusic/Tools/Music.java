package com.example.mymusic.Tools;

import java.io.Serializable;

public class Music implements Serializable {
    private String name;
    private String path;

    public Music(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public Music() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
