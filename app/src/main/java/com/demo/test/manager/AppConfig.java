package com.demo.test.manager;

/**
 * Created by Administrator on 2021/8/6.
 */

public class AppConfig {
    public static final boolean DEBUG = true;//测试

    public static final String APK_DOWN_URI=DEBUG?"http://www.xxx.com:xxx/":"http://xx.xxx.xxx.xxx:xxx/";

    public static final String APK_NAME=DEBUG?"xxx_test":"xxx";

    public static final String URI_BASE = DEBUG?"http://www.xxx.com:xxx/":"http://xx.xxx.xxx.xxx:xxx/";;
    public static final String URL = "http://198.168.1.162:5317/Handler1.ashx/";
    public static final boolean IS_LOG = DEBUG;

    private static final String SERVER_MOBILE_DIR="MobileOnServer/";
    public static final String SERVER_MOBILE_CONFIG_DIR=SERVER_MOBILE_DIR+"config/";
    public static final String SERVER_MOBILE_APK_DIR=SERVER_MOBILE_DIR+"apk/dzlf/";

    private static final String APP_ROOT_EXTERNAL_DIR="hblf/";

    private static final String APP_DOWNLOAD_DIR=APP_ROOT_EXTERNAL_DIR+"Download/";
    public static final String APP_APK_DIR=APP_DOWNLOAD_DIR+"Apk/";
}
