package org.chuck.imgsector;

/**
 * Created by Administrator on 16-1-5.
 */
public class SectorItem {
    private String path;
    private boolean isChecked;

    public SectorItem() {
    }

    public SectorItem(String path) {
        this.path = path;
    }

    public SectorItem(String path, boolean isChecked) {
        this.path = path;
        this.isChecked = isChecked;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
