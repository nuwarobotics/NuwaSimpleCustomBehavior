package com.nuwarobotics.example.custombehavior.welcome;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.nuwarobotics.example.custombehavior.R;
import com.nuwarobotics.service.custombehavior.BaseBehaviorService;
import com.nuwarobotics.service.custombehavior.CustomBehavior;

/**
 * This example present how to customize welcome sentence when "自動会話" wes enabled.
 */

public class CustomService extends BaseBehaviorService {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate.");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand: intent=" + intent + " flags=" + flags + " startId=" + startId);

        //BaseBehaviorService should keep alive to keep connect to nuwa Behavior Service
        //Android O should startForeground with Notification
        SetForeground();

        return START_STICKY;
    }

    private void SetForeground() {
        String NOTIFICATION_CHANNEL_ID = getApplicationContext().getPackageName();
        String channelName = getClass().getSimpleName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.NotificationChannel chan = new android.app.NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            chan.setShowBadge(false);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
        } else {
            Log.e(TAG, "system below Oreo, no NotificationChannel");
            return;
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("ExtService is running in background") // notification text
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1, notification);
    }

    //Basic behavior
    @Override
    public void onInitialize() {
        try {
            Log.d(TAG, "onInitialize: regist welcome sentence");
            //EXAMPLE : developer can customize welcome sentence when onInitialize
            //sentence example 1 (no name) : いらっしゃいませ。ご用件は何でしょうか？
            //sentence example 2 (with name) : %s, いらっしゃいませ。ご用件は何でしょうか？
            mSystemBehaviorManager.setWelcomeSentence(getResources().getStringArray(R.array.welcom_sentence));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CustomBehavior createCustomBehavior() {
        return new CustomBehavior.Stub() {
            @Override
            public void onWelcome(String name, long faceid) {
                Log.d(TAG, "onWelcome: name=" + name + ", faceid=" + faceid);

            }

            @Override
            public void prepare(final String parameter) {
                Log.d(TAG, "prepare: " + parameter);

            }

            @Override
            public void process(final String parameter) {
                Log.d(TAG, "process: " + parameter);

            }

            @Override
            public void finish(final String parameter) {
                Log.d(TAG, "finish: " + parameter);
            }
        };
    }


}
