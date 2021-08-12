package com.demo.test;

import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.demo.test.model.GetDataParam;
import com.demo.test.util.HttpUtil;
import com.demo.test.util.LogUtils;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView ipTextView = null;
    private TextView nameTextView = null;

    private ConnectivityManager mConnectivityManager = null;
    private NetworkInfo mActiveNetInfo = null;
    private EditText userNameEt;
    private EditText passwordEt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userNameEt = (EditText) findViewById(R.id.et_user_name);
        passwordEt = (EditText) findViewById(R.id.et_password);
        Button button = (Button) findViewById(R.id.button);

        nameTextView = (TextView) findViewById(R.id.nametextview);
        ipTextView = (TextView) findViewById(R.id.ipTextView);

        mConnectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE) ;//获取系统的连接服务
        mActiveNetInfo = mConnectivityManager.getActiveNetworkInfo();//获取网络连接的信息

        if(mActiveNetInfo==null)
            myDialog();
        else
            setUpInfo();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = userNameEt.getText().toString().trim();
                final String password = passwordEt.getText().toString().trim();
                final Map<String, String> args = new HashMap<>();

                args.put("username",username);
                args.put("password",password);
                args.put("param", GetDataParam.Get_User_Record.name());

                LogUtils.LogMap(args);

                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            HttpUtil.getInstance().doPost(args);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AppManager.getAppManager().checkUpdate(MainActivity.this);
//            }
//        });
    }

    public String getIPAddress() {
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if ((info.getType() == ConnectivityManager.TYPE_MOBILE) || (info.getType() == ConnectivityManager.TYPE_WIFI) ){//当前使用2G/3G/4G网络
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                }
                catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        }
        else { //当前无网络连接,请在设置中打开网络
            return null;
        }
        return null;
    }

    public void setUpInfo()  {
        if(mActiveNetInfo.getType()==ConnectivityManager.TYPE_WIFI)  {
            nameTextView.setText("网络类型：WIFI");
            ipTextView.setText("IP地址："+getIPAddress());
        }
        else if(mActiveNetInfo.getType()==ConnectivityManager.TYPE_MOBILE)  {
            nameTextView.setText("网络类型：3G/4G");
            ipTextView.setText("IP地址："+getIPAddress());
        }
        else  {
            nameTextView.setText("网络类型：未知");
            ipTextView.setText("IP地址：");
        }
    }

    private void myDialog()  {
        AlertDialog mDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("注意")
                .setMessage("当前网络不可用，请检查网络！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener()  {
                    @Override
                    public void onClick(DialogInterface dialog, int which)  {
                        dialog.dismiss();
                        MainActivity.this.finish();
                    }
                })
                .create();//创建这个对话框
        mDialog.show();//显示这个对话框
    }
}
