package org.chuck.imgsector;

/**
 * Created by Administrator on 16-1-5.
 */
public class Album {
    private String id;
    private String name;
    private int count;
    private String recent;
    private boolean isChecked;

    public Album(String id, String name, int count, String recent) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.recent = recent;
    }

    public Album(String id, String name, int count, String recent, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.recent = recent;
        this.isChecked = isChecked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getRecent() {
        return recent;
    }

    public void setRecent(String recent) {
        this.recent = recent;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public void increaseCount(){
        count++;
    }
}
