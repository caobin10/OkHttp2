package org.chuck.load.down;

import org.chuck.util.ThreadPool;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 16-3-18.
 */
public class DownManager {
    private static DownManager instance;

    private Map<String,DownTask> downTasksMap=new HashMap<>();
    private DownManager(){

    }

    public static DownManager getInstance(){
        if(instance==null){
            synchronized (DownManager.class){
                if(instance==null){
                    instance=new DownManager();
                }
            }
        }
        return instance;
    }


    public void addTask(DownTask downTask){
        String key=downTask.getDownEntity().getUrl();
        if(!downTasksMap.containsKey(key)){
            downTasksMap.put(key,downTask);
        }
        ThreadPool.getExecutor().execute(downTask);
    }

    public void removeTaskById(String id){
        if(downTasksMap.containsKey(id)){
            downTasksMap.remove(id);
        }
    }

    public void pauseTask(DownTask downTask){
        if(downTask!=null){
            downTask.pause();
        }
    }
    public void cancelTask(DownTask downTask){
        if(downTask!=null){
            downTask.cancel();
        }
    }

    public DownTask getTaskById(String id){
        return downTasksMap.get(id);
    }
}
