package com.example.audioplayer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayMusicActivity extends AppCompatActivity {
    ImageButton Play, Next, Prev, Stop;
    TextView name, start, textstop;
    SeekBar seekBar;
    Runnable runnable;
    Handler handler;

    String sname;
    static MediaPlayer mediaPlayer;
    int position;
    Boolean p=true;
    ArrayList<File> mysong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setCustomView(R.layout.actionbar2);

        setContentView(R.layout.activity_play_music);

        Prev = findViewById(R.id.prev);
        Next = findViewById(R.id.next);
        Play = findViewById(R.id.play);
        Stop = findViewById(R.id.stop);
        name = findViewById(R.id.judul);
        start = findViewById(R.id.posisi);
        textstop = findViewById(R.id.durasi);
        seekBar = findViewById(R.id.seekbar);

        handler = new Handler();

        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mysong = (ArrayList) bundle.getParcelableArrayList("songs");
        String songName = i.getStringExtra("songname");
        position = bundle.getInt("pos",0);
        name.setSelected(true);
        Uri uri = Uri.parse(mysong.get(position).toString());
        sname = mysong.get(position).getName();
        name.setText(sname);

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        SongProperties();
        mediaPlayer.start();

        Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(p==true){
                    mediaPlayer.start();
                    Play.setImageResource(R.drawable.ic_baseline_pause_24);
                    p = false;
                }else{
                    mediaPlayer.pause();
                    p = true;
                    Play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                }
                SongProperties();
            }
        });

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%mysong.size());

                Uri u = Uri.parse(mysong.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mysong.get(position).getName();
                name.setText(sname);
                mediaPlayer.start();
                Play.setImageResource(R.drawable.ic_baseline_pause_24);
                SongProperties();
            }
        });

        Prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1)<0)?(mysong.size()-1):(position-1);

                Uri u = Uri.parse(mysong.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mysong.get(position).getName();
                name.setText(sname);
                mediaPlayer.start();
                Play.setImageResource(R.drawable.ic_baseline_pause_24);
                SongProperties();
            }
        });

        Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    mediaPlayer.stop();
                    p = true;
                    mediaPlayer.prepare();
                    mediaPlayer.seekTo(0);
                    Play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        });
    }
    private void UpdateSeekBar(){
        int currentPosition = mediaPlayer.getCurrentPosition();

        seekBar.setProgress(currentPosition);

        runnable = new Runnable() {
            @Override
            public void run() { UpdateSeekBar(); }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void SongProperties(){
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                seekBar.setMax(mediaPlayer.getDuration());
                textstop.setText(seekBarTimeFormat(mediaPlayer.getDuration()));
                UpdateSeekBar();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
                start.setText(seekBarTimeFormat(mediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    @SuppressLint("DefaultLocale")
    private String seekBarTimeFormat(int durationInMs){
        long minutesDuration = TimeUnit.MILLISECONDS.toMinutes(durationInMs);
        long secondsDuration = TimeUnit.MILLISECONDS.toSeconds(durationInMs);

        return String.format("%02d:%02d",
                minutesDuration,
                secondsDuration - TimeUnit.MINUTES.toSeconds(minutesDuration));
    }
}