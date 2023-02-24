package dev.sample.foreground_example;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat.Builder;
import org.jetbrains.annotations.Nullable;

public final class SampleService extends Service {
    private static final String TAG = "SampleService";
    private static Handler handler;
    private static Runnable runnableTask;
    private static int count;

    @Override
    @Nullable
    public IBinder onBind(@Nullable Intent p0) {
        throw new RuntimeException("Don't use bindService().");
    }

    @Override
    @RequiresApi(26)
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.i(TAG, "call onStartCommand...");
        // 通知オブジェクトの構築
        NotificationManager notificationManager =
                (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        String id = "isolate_holder";
        if (notificationManager.getNotificationChannel(id) == null) {
            NotificationChannel mChannel = new NotificationChannel(id, (CharSequence)"title", 3);
            mChannel.setDescription("description");
            notificationManager.createNotificationChannel(mChannel);
        }

        Builder builder = new Builder((Context)this, id);
        builder.setContentTitle((CharSequence)"サービス");
        builder.setContentText((CharSequence)"サービス123");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = builder.build();

        // 1秒間隔でカウントアップする処理
        handler = new Handler(Looper.getMainLooper());
        runnableTask = (Runnable)(new Runnable() {
            public void run() {
                SampleService.count = SampleService.count + 1;
                Log.i(SampleService.TAG, "run action count = " + SampleService.count);
                handler.postDelayed((Runnable)this, 1000L);
            }
        });
        handler.post(runnableTask);

        this.startForeground(1, notification);
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "call onCreate...");
    }

    /**
     * タイマー停止処理
     */
    public static void stopTimer() {
        if (SampleService.runnableTask == null) {
            return;
        }
        Handler serviceHandler = SampleService.handler;
        Runnable task = SampleService.runnableTask;
        serviceHandler.removeCallbacks(task);
        SampleService.count = 0;
    }

    /**
     * 現在のカウントを取得
     * @return 現在のカウント
     */
    public static int getCount() {
        return SampleService.count;
    }
}
