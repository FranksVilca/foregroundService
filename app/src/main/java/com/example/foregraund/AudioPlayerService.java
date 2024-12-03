package com.example.foregraund;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class AudioPlayerService extends Service {
    private MediaPlayer mediaPlayer;
    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_STOP = "ACTION_STOP";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        switch (action) {
            case ACTION_PLAY:
                playAudio();
                break;
            case ACTION_PAUSE:
                pauseAudio();
                break;
            case ACTION_STOP:
                stopAudio();
                break;
        }

        return START_STICKY;
    }

    private void playAudio() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.song); // Reemplaza con tu archivo de audio
            if (mediaPlayer == null) {
                Log.e("AudioPlayerService", "Error al cargar el archivo de audio");
                return;
            } else {
                Log.d("AudioPlayerService", "Archivo de audio cargado correctamente");
                mediaPlayer.setOnCompletionListener(mp -> stopAudio());
                mediaPlayer.setOnPreparedListener(mp -> {
                    Log.d("AudioPlayerService", "MediaPlayer preparado");
                    mediaPlayer.start();
                });
                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    Log.e("AudioPlayerService", "Error en MediaPlayer: " + what + ", " + extra);
                    return true;
                });
            }
        }

        // Verifica si ya está reproduciendo, si no, inicia la reproducción
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Log.d("AudioPlayerService", "Reproducción iniciada");
        }
        startForeground(1, createNotification());
    }

    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Log.d("AudioPlayerService", "Reproducción pausada");
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d("AudioPlayerService", "Reproducción detenida y MediaPlayer liberado");
        }
        stopForeground(true);
        stopSelf();
    }

    private Notification createNotification() {
        String channelId = "AudioPlayerChannel";
        NotificationChannel channel = new NotificationChannel(
                channelId, "Audio Player", NotificationManager.IMPORTANCE_LOW
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }

        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Reproduciendo audio")
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
