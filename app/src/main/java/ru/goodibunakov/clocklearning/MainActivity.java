package ru.goodibunakov.clocklearning;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements AsyncTaskListener {

    /**
     * Creating a Kotlin Android rotary knob
     * https://medium.com/androiddevelopers/dependency-injection-on-android-with-hilt-67b6031e62d
     */

    @BindView(R.id.clock_seek_bar_hour)
    CircularClockSeekBarHour circularClockSeekBarHour;
    @BindView(R.id.clock_seek_bar_minute)
    CircularClockSeekBarMinute circularClockSeekBarMinute;
    @BindView(R.id.status)
    TextView tvStatus;
    @BindView(R.id.tv_hour)
    TextView tvHour;
    @BindView(R.id.tv_minute)
    TextView tvMinute;

    private final String[] minutesArray = {"00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"};
    int indexHour = 12, indexMinute = 0;
    private Animation in, out;
    MediaPlayer mp;
    private final int MAX_STREAMS = 5;
    private float volume;
    private SoundPool soundPool;
    private int soundIDYes, soundIDNo;
    private boolean loaded = false;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        circularClockSeekBarHour.setProgress(0);
        circularClockSeekBarMinute.setProgress(0);

        in = AnimationUtils.loadAnimation(this, R.anim.in);
        out = AnimationUtils.loadAnimation(this, R.anim.out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initAudioAndSounds();
    }

    private void generateNewTime() {
        if (random == null) random = new Random();
        indexHour = random.nextInt(11) + 1;
        int indexForMinArray = random.nextInt(minutesArray.length);
        indexMinute = Integer.parseInt(minutesArray[indexForMinArray]);
        tvHour.setText(String.valueOf(indexHour));
        tvMinute.setText(minutesArray[indexForMinArray]);
        tvStatus.startAnimation(out);
        tvStatus.postOnAnimationDelayed(() -> {
            tvStatus.setText("");
            tvStatus.setVisibility(View.INVISIBLE);
        }, 1000);
    }


    @OnClick(R.id.submit)
    void onClick() {
        int hourSelected = ((int) Math.round((double) circularClockSeekBarHour.getValue() / 10));
        if (hourSelected == 0) hourSelected = 12;
        int minuteSelected = circularClockSeekBarMinute.getValue() / 2;

        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.startAnimation(in);

        if (hourSelected == indexHour
                && (minuteSelected == indexMinute
                || minuteSelected > indexMinute ? minuteSelected - indexMinute <= 1 : indexMinute - minuteSelected <= 1)) {
            tvStatus.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorRight));
            tvStatus.setText(R.string.right);
            playSound(soundIDYes);
            new PauseTask(this).execute();
        } else {
            tvStatus.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
            tvStatus.setText(R.string.wrong);
            playSound(soundIDNo);
        }
    }

    private void initAudioAndSounds() {
        mp = MediaPlayer.create(MainActivity.this, R.raw.plash);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setLooping(true);
        mp.start();

        float currentVolumeIndex = 0f;
        float maxVolumeIndex = 0f;
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioManager != null) {
            currentVolumeIndex = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            maxVolumeIndex = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }
        volume = currentVolumeIndex / maxVolumeIndex;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (Build.VERSION.SDK_INT >= 21) {
            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);
            soundPool = builder.build();
        } else {
            soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }
        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> loaded = true);
        soundIDYes = soundPool.load(this, R.raw.yes, 1);
        soundIDNo = soundPool.load(this, R.raw.no, 1);
    }

    @Override
    public void pauseFinished() {
        generateNewTime();
    }

    static class PauseTask extends AsyncTask<Void, Void, Void> {

        private AsyncTaskListener listener;

        PauseTask(Context context) {
            listener = (AsyncTaskListener) context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listener.pauseFinished();
        }
    }

    private void playSound(int soundID) {
        if (loaded) {
            soundPool.play(soundID, volume, volume, 1, 0, 1f);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }
}