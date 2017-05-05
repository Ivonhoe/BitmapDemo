package com.redcat.imagecat;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * @author Ivonhoe on 2017/4/20.
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.widget.ProgressBar;

public class TestActivity extends Activity {
    private int[] randData = new int[100];
    private int index = 0;
    private int mProgressStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        final ProgressBar bar = (ProgressBar) findViewById(R.id.bar);
        final Handler mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);

                if (msg.what == 0x111) {
                    mProgressStatus = index;
                    //设置进度条当前的完成进度
                    bar.setProgress(mProgressStatus);
                }
            }
        };

        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                while (index < 100) {
                    doWork();
                    Message msg = new Message();
                    msg.what = 0x111;
                    mHandler.sendMessage(msg);
                }
            }

        }.start();

    }

    private int doWork() {
        randData[index++] = (int) (Math.random() * 100);
        //模拟一个比较耗时的操作
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return index;
    }

}
