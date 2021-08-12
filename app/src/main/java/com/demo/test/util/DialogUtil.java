package com.demo.test.util;

import android.content.Context;

import com.demo.test.R;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.chuck.view.SweetDialog;

import cn.pedant.SweetAlert.ProgressHelper;

/**
 * Created by Administrator on 2021/8/11.
 */

public class DialogUtil {

    public static SweetDialog createProgressDefault(Context context, String msg) {
        final SweetDialog dialog = new SweetDialog(context);
        dialog.setContentView(R.layout.dialog_progress_default);
        dialog.setDefault();
        dialog.setText(R.id.tv_msg, msg);
        ProgressHelper helper = new ProgressHelper(context);
        helper.setProgressWheel((ProgressWheel) dialog.getView(R.id.pw_progressWheel));
        helper.setBarWidth(5);
        return dialog;
    }
}
