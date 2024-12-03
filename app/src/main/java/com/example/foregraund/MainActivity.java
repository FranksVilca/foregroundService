package com.example.foregraund;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnPlay, btnPause, btnStop;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private boolean isUserSeeking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        seekBar = findViewById(R.id.seekBar);

        btnPlay.setOnClickListener(v -> sendServiceCommand(AudioPlayerService.ACTION_PLAY));
        btnPause.setOnClickListener(v -> sendServiceCommand(AudioPlayerService.ACTION_PAUSE));
        btnStop.setOnClickListener(v -> sendServiceCommand(AudioPlayerService.ACTION_STOP));

        // Inicializa el MediaPlayer para controlar el SeekBar
        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserSeeking = false;
            }
        });

        handler.post(updateSeekBar);
    }

    private void sendServiceCommand(String action) {
        Intent intent = new Intent(this, AudioPlayerService.class);
        intent.setAction(action);
        startService(intent);
    }

    private final Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (!isUserSeeking && mediaPlayer.isPlaying()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekBar);
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
