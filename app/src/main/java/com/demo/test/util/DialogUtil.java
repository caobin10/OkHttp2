package com.demo.test.util;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.demo.test.R;
import com.demo.test.view.SweetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DialogUtil {
    public static SweetDialog createDateDefault(Context context, final OnResultCallback<Date> callback) {
        final SweetDialog dialog = new SweetDialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog.setContentView(R.layout.dialog_date_default);
        DatePicker datePicker = dialog.getView(R.id.dp_date);
        datePicker.setMaxDate(new Date().getTime());
        final TextView dateTv = (TextView) dialog.findViewById(R.id.tv_date);
        Button cancelBtn = (Button) dialog.findViewById(R.id.btn_cancel);
        Button ensureBtn = (Button) dialog.findViewById(R.id.btn_ensure);
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        dateTv.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
        datePicker.init(year, monthOfYear, dayOfMonth, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateTv.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ensureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                try {
                    callback.onResult(new SimpleDateFormat("yyyy-MM-dd").parse(dateTv.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        return dialog;
    }
    public interface OnResultCallback<T> {
        public void onResult(T t);
    }
}
