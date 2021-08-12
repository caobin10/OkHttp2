package com.demo.test.util;

import android.os.Environment;

import org.chuck.util.StorageUtil;

import java.io.File;

/**
 * Created by Administrator on 2021/8/11.
 */

public class LfStorageUtil extends StorageUtil {

    public static final File EXTERNAL_STORAGE= Environment.getExternalStorageDirectory();
}
