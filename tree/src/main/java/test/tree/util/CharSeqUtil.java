package test.tree.util;

import android.text.TextUtils;

/**
 * Created by Administrator on 2021/6/24.
 */

public class CharSeqUtil {
    public static boolean isEmpty(CharSequence str){
        if(TextUtils.isEmpty(str)|| TextUtils.getTrimmedLength(str)==0){
            return true;
        }else {
            return false;
        }
    }
}
