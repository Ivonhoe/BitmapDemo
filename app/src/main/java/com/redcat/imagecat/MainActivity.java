package com.redcat.imagecat;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;

public class MainActivity extends Activity {

    private TextView textView;
    private ImageView imageView;
    private ImageView mImageView2;

    private TextView imageViewText;
    private TextView imageView2Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.sizeScreen);
        imageView = (ImageView) findViewById(R.id.sizeImage);
        mImageView2 = (ImageView) findViewById(R.id.image2);
        imageViewText = (TextView) findViewById(R.id.sizeImageText);
        imageView2Text = (TextView) findViewById(R.id.image2Text);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(MainActivity.this, v.getWidth() + ":" + v.getHeight(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mImageView2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                activityManager.getMemoryInfo(memoryInfo);
                long totalMem = memoryInfo.totalMem / (1024 * 1024);
                long availMem = memoryInfo.availMem / (1024 * 1024);

                long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
                long freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024);

                Log.d("simply", "free memory:" + freeMemory + ",totalMemory:" + totalMemory);

                getmem_SELF();

                Toast.makeText(MainActivity.this, totalMem + "MB:" + availMem + "MB", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        Bitmap bitmap1 = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        imageViewText.setText(bitmap1.getWidth() + "*" + bitmap1.getHeight() + "," + bitmap1.getByteCount() + "byte");

        Bitmap bitmap2 = ((BitmapDrawable) mImageView2.getDrawable()).getBitmap();
        imageView2Text.setText(bitmap2.getWidth() + "*" + bitmap2.getHeight() + "," + bitmap2.getByteCount() + "byte");

        textView.setText(getDisplayMetrics());

        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, TestActivity.class);
//
//                startActivity(intent);

                clearPreloadedDrawables();
            }
        });
    }

    public long getmem_SELF() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> procInfo = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : procInfo) {
            System.out.println(runningAppProcessInfo.processName + String.format(",pid = %d", runningAppProcessInfo.pid));
            if (runningAppProcessInfo.processName.indexOf(this.getPackageName()) != -1) {
                int pids[] = {runningAppProcessInfo.pid};
                Debug.MemoryInfo self_mi[] = am.getProcessMemoryInfo(pids);
                StringBuffer strbuf = new StringBuffer();
                strbuf.append(" proccess Name:").append(runningAppProcessInfo.processName)
                        .append("\n pid:").append(runningAppProcessInfo.pid)
                        .append("\n dalvikPrivateDirty:").append(self_mi[0].dalvikPrivateDirty)
                        .append("\n dalvikPss:").append(self_mi[0].dalvikPss)
                        .append("\n dalvikSharedDirty:").append(self_mi[0].dalvikSharedDirty)
                        .append("\n nativePrivateDirty:").append(self_mi[0].nativePrivateDirty)
                        .append("\n nativePss:").append(self_mi[0].nativePss)
                        .append("\n nativeSharedDirty:").append(self_mi[0].nativeSharedDirty)
                        .append("\n otherPrivateDirty:").append(self_mi[0].otherPrivateDirty)
                        .append("\n otherPss:").append(self_mi[0].otherPss)
                        .append("\n otherSharedDirty:").append(self_mi[0].otherSharedDirty)
                        .append("\n TotalPrivateDirty:").append(self_mi[0].getTotalPrivateDirty())
                        .append("\n TotalPss:").append(self_mi[0].getTotalPss())
                        .append("\n TotalSharedDirty:").append(self_mi[0].getTotalSharedDirty());
                Log.v("simply", strbuf.toString());
            }
        }
        return 0;
    }

    private String getDisplayMetrics() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        int densityDpi = dm.densityDpi;
        float scale = dm.density;
        float fontScale = dm.scaledDensity;
        float xdpi = dm.xdpi;
        float ydpi = dm.ydpi;

        return "[screenWidth: "
                + screenWidth
                + " screenHeight: "
                + screenHeight
                + " scale: "
                + scale
                + " fontScale: "
                + fontScale
                + " xdpi: " + xdpi
                + " ydpi: " + ydpi
                + " densityDpi: "
                + densityDpi + "]";
    }

    private void clearPreloadedDrawables() {
        try {
            Field mFieldPreloadedDrawables = getField(Resources.class, "sPreloadedDrawables");
            if (mFieldPreloadedDrawables != null) {
                mFieldPreloadedDrawables.setAccessible(true);

                boolean access = mFieldPreloadedDrawables.isAccessible();
                if (!access) {
                    mFieldPreloadedDrawables.setAccessible(true);
                }

                if (Build.VERSION.SDK_INT <= 17) {
                    LongSparseArray<Drawable.ConstantState> dArray = (LongSparseArray<Drawable.ConstantState>) mFieldPreloadedDrawables.get(getResources());
                    if (dArray != null) {
                        dArray.clear();
                    }

                    // dArray.put(1, createDrawableConstant());
                } else if (Build.VERSION.SDK_INT >= 18) {
                    LongSparseArray<Drawable.ConstantState>[] dArray = (LongSparseArray<Drawable.ConstantState>[]) mFieldPreloadedDrawables.get(getResources());
                    if (dArray != null) {
                        for (int i = 0; i < dArray.length; i++) {
                            if (dArray[i] != null) {
                                dArray[i].clear();
                                Log.d("simply", "------clear");

                                dArray[i].put(1, createDrawableConstant());
                            }
                        }
                    }
                }

                mFieldPreloadedDrawables.setAccessible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Drawable.ConstantState createDrawableConstant() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.home_content_bg);
        Drawable drawable = new BitmapDrawable(bitmap);

        return drawable.getConstantState();
    }

    private Field getField(Class className, String fieldName) {
        // 获取对象obj的所有属性域
        Field[] fields = className.getDeclaredFields();

        for (Field field : fields) {
            // 对于每个属性，获取属性名
            String varName = field.getName();

            if (TextUtils.equals(varName, fieldName)) {
                return field;
            }
        }

        return null;
    }

}
