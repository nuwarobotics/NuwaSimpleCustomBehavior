package com.nuwarobotics.example.custombehavior.behavior;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.nuwarobotics.example.custombehavior.R;
import com.nuwarobotics.service.custombehavior.BaseBehaviorService;
import com.nuwarobotics.service.custombehavior.CustomBehavior;

public class CustomService extends BaseBehaviorService {
    //private final String TAG = this.getClass().getSimpleName();
    private final String TAG = "Behavior.CustomService";

    private Handler mHandler;
    private HandlerThread mThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate.");

        //initial
        mThread = new HandlerThread("BehaviorThread");
        mThread.start();

        mHandler = new Handler(mThread.getLooper());
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
        Log.d(TAG, "onInitialize: you can register welcome sentence here.");
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
                //When words of user speaking match with the sentence setup on TrainingKit
                //Behavior Service will trigger callback process(string) to notify 3rd app  (the String parameter was the json which developer setup on TrainingKit)
                //TODO : Developer allow implement something here  (By content of parameter)
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //EXAMPLE : This example just always call Toast.  (developer can check content of parameter, to implement other logic)
                        Toast.makeText(getApplication(), "Do any thing you want", Toast.LENGTH_LONG).show();
                        //EXAMPLE : when developer customization finished, please must call  notifyBehaviorFinished();
                        notifyBaseServiceFinish();
                    }
                });

            }

            @Override
            public void finish(final String parameter) {
                Log.d(TAG, "finish: " + parameter);
            }
        };
    }

    private void notifyBaseServiceFinish() {
        try {
            //if nothing need to do, call notifyBehaviorFinished to finish custombehavior
            notifyBehaviorFinished();
        } catch (RemoteException RE) {
            Log.e(TAG, "handleMessage, RemoteException", RE);
        }
    }

}
