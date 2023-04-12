package ru.mirea.smelkin.mireaproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyServicePlayer extends Service {
    private MediaPlayer mediaPlayer;
    public static final String CHANNEL_ID = "smelkin_nikita";



    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)//TODO: закрытие уведомления
                .setContentText("идёт воспроизведение")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("текущий трек - комарово"))
                .setContentTitle("мой плеер");


        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Смелкин Н.Д. Notification", importance);
        channel.setDescription("smelkin");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.createNotificationChannel(channel);

        startForeground(1, builder.build());

        mediaPlayer = MediaPlayer.create(this, R.raw.komarovo);
        mediaPlayer.setLooping(false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                stopForeground(true);
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        mediaPlayer.stop();
    }
}