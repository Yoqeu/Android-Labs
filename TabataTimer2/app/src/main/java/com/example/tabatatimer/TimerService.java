package com.example.tabatatimer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class TimerService extends Service {
    private static final long START_TIME_IN_MILLIS = 600000;
    public final static String TIME_LEFT = "result";
    public final static String BROADCAST_ACTION = "ru.startandroid.develop.p0961servicebackbroadcast";
    public final static String POSITION = "position";
    int index;
    private boolean timerRunning;
    private TextView mTextViewCountDown;
    private long mTimeLeft = START_TIME_IN_MILLIS;
    ArrayList<Integer> time_list;
    CountDownTimer countDownTimer;
    MediaPlayer mediaPlayer;
    Timer timer;

    IBinder mBinder = new LocalBinder();


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = MediaPlayer.create(this, R.raw.sound1);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle args = intent.getExtras();
        mTimeLeft = args.getLong("mTimeLeft");
        index = args.getInt("index");
        time_list = args.getIntegerArrayList("time_list");
        Log.i("servindex", String.valueOf(index));
        startTimer();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

    public void startTimer() {
        countDownTimer = new CountDownTimer(mTimeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeft = millisUntilFinished;
                updateCountDownText();
                Log.i("servtime", String.valueOf(mTimeLeft / 1000 + 1));
            }

            @Override
            public void onFinish() {
                index++;
                if (index < time_list.size()) {
                    mTimeLeft = time_list.get(index);
                    mediaPlayer.start();
                    mTimeLeft--;
                    startTimer();
                }
            }
        }.start();
    }

    public void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
    }

    public long getmTimeLeft() {
        return mTimeLeft;
    }

    public int getIndex() {
        return index;
    }

    public class LocalBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    private void updateCountDownText() {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(TIME_LEFT, mTimeLeft);
        intent.putExtra(POSITION, index);
        sendBroadcast(intent);
    }
}
