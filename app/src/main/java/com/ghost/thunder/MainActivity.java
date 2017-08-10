package com.ghost.thunder;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.ghost.thunder.demo.R;

import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.XLTaskInfo;

public class MainActivity extends AppCompatActivity {
    EditText inputUrl;
    Button btnDownload;
    TextView tvStatus;
    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0) {
                long taskId = (long) msg.obj;
                XLTaskInfo taskInfo = XLTaskHelper.instance().getTaskInfo(taskId);
                tvStatus.setText(
                        "fileSize:" + taskInfo.mFileSize
                                + " downSize:" + taskInfo.mDownloadSize
                                + " speed:" + convertFileSize(taskInfo.mDownloadSpeed)
                                + "/s dcdnSoeed:" + convertFileSize(taskInfo.mAdditionalResDCDNSpeed)
                                + "/s filePath:" + "/sdcard/" + XLTaskHelper.instance().getFileName(inputUrl.getText().toString())
                );
                handler.sendMessageDelayed(handler.obtainMessage(0,taskId),1000);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        XLTaskHelper.init(getApplicationContext());
        inputUrl = (EditText) findViewById(R.id.input_url);
        btnDownload = (Button) findViewById(R.id.btn_down);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(inputUrl.getText())) {
                    long taskId = XLTaskHelper.instance().addThunderTask(inputUrl.getText().toString(),"/sdcard/",null);
                    handler.sendMessage(handler.obtainMessage(0,taskId));
                }
            }
        });
    }
    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f M" : "%.1f M", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f K" : "%.1f K", f);
        } else
            return String.format("%d B", size);
    }
}
