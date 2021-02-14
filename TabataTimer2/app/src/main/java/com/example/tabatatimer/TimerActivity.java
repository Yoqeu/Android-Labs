package com.example.tabatatimer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;

public class TimerActivity extends AppCompatActivity {
    public final static String  TIMER = "timer";
    public final static String INDEX = "index";
    private boolean running, is_service_active;
    private long  mTimeLeft;
    private int index;
    String workTime;
    ArrayList<Integer> time_list;
    ArrayList<String> task_list;

    private Timer timer;
    private CountDownTimer countDownTimer;
    TextView taskTextView, timeTextView;
    ImageView pauseImageView;
    ListView timerListView;
    Database db;
    ImageView btnPrev, btnNext, btnBack, btnPause;
    Intent serviceIntent;
    ServiceConnection mConnection;
    TimerService timerService;
    MediaPlayer mediaPlayer, finishMediaPlayer, workMediaPlayer;
    BroadcastReceiver broadcastReceiver;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        workMediaPlayer = MediaPlayer.create(this, R.raw.whistle);
        finishMediaPlayer = MediaPlayer.create(this, R.raw.finish);
        mediaPlayer = MediaPlayer.create(this, R.raw.sound1);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.stop();
            }
        });
        Intent intent = getIntent();
        final Timer timer;
        BroadcastReceiver notificationReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, @NonNull Intent intent) {
                switch (intent.getAction()) {
                    case "previous": {
                        Toast.makeText(getApplicationContext(), "prev", Toast.LENGTH_SHORT).show();
                        btnPrev.performClick();
                        break;
                    }
                    case "pause": {
                        btnPause.performClick();
                        Toast.makeText(getApplicationContext(), "pause", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "next": {
                        btnNext.performClick();
                        Toast.makeText(getApplicationContext(), "next", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        };

        is_service_active = false;
        index = 0;
        btnPrev = findViewById(R.id.imageViewPrevious);
        btnNext = findViewById(R.id.imageViewNext);
        btnPause = findViewById(R.id.imageViewPause);

        timeTextView = findViewById(R.id.textViewTime);
        taskTextView = findViewById(R.id.textViewTimerTask);

        timerListView = findViewById(R.id.listViewTimer);

        IntentFilter filters = new IntentFilter();
        filters.addAction("previous");
        filters.addAction("next");
        filters.addAction("pause");


        mConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                is_service_active = false;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                timerService = ((TimerService.LocalBinder) service).getService();
                is_service_active = true;
            }
        };

        Bundle args = getIntent().getExtras();
        registerReceiver(notificationReceiver, filters);
        IntentFilter intentFilter = new IntentFilter(TimerService.BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
        serviceIntent = new Intent(getBaseContext(), TimerService.class);
        db = new Database(this);
        db.open();
        int id = args.getInt("id");
        timer = db.getTimer(id);
        Log.i("title", timer.getTitle());
        TextView textView = findViewById(R.id.textViewTimerTitle);
        textView.setText(timer.getTitle());



        task_list = new ArrayList<String>();
        time_list = new ArrayList<Integer>();
        task_list.add(this.getString(R.string.Prep) + ": " + timer.getPrepTime());

        mTimeLeft = timer.getPrepTime() * 1000;
        time_list.add((int)mTimeLeft);
        for (int j = 0; j < timer.getSetsAmount(); j++) {
            for (int i = 0; i < timer.getCyclesAmount(); i++) {
                task_list.add(this.getString(R.string.Work) + ": " + timer.getWorkTime());
                time_list.add(timer.getWorkTime() * 1000);
                task_list.add(this.getString(R.string.Rest) + ": " + timer.getRestTime());
                time_list.add(timer.getRestTime() * 1000);
            }
            if (timer.getSetsAmount() > 1) {
                task_list.add(this.getString(R.string.SetsRest) + ": " + timer.getRest_sets());
                time_list.add(timer.getRest_sets() * 1000);
            }
        }
        workTime = getString(R.string.Work) + ": " + timer.getWorkTime();
        task_list.add(this.getString(R.string.Finish));
        time_list.add(1000);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, task_list);
        timerListView.setAdapter(adapter);
        db.close();

        mediaPlayer.start();


        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running) {
                    pauseTimer();
                    running = false;
                } else {
                    startTimer();
                    running = true;
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index > 0) {
                    Log.i("index", String.valueOf(index));
                    changePos(index - 1);
                    taskTextView.setText(task_list.get(index));
                    mTimeLeft = time_list.get(index);
                    countDownTimer.cancel();
                    startTimer();

                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index < time_list.size() - 1) {
                    changePos(index + 1);
                    taskTextView.setText(task_list.get(index));
                    mTimeLeft = time_list.get(index);
                    countDownTimer.cancel();
                    startTimer();
                }
            }

        });

    }

    public Timer getTimer()
    {
        return timer;
    }

    public int getIndex()
    {
        return index;
    }


    public int changePos(int position){
        if(position <  0){
            position = 0;
        }
        else if(position > time_list.size() - 1){
            position = time_list.size() - 1;
        }
        index = position;
        return index;

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle(this.getString(R.string.MsgTitle))
                .setMessage(this.getString(R.string.Msg))
                .setPositiveButton(this.getString(R.string.MsgBtnPos), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        countDownTimer.cancel();
                        finish();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(this.getString(R.string.MsgBtnNeg), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void startTimer() {
        countDownTimer = new CountDownTimer(mTimeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeft = millisUntilFinished;
                if (index == time_list.size() - 1) {
                    timeTextView.setText(String.valueOf(0));
                }
                else {
                    timeTextView.setText(String.valueOf(mTimeLeft / 1000 + 1));
                }

                Log.i("time", String.valueOf(mTimeLeft / 1000 + 1));
            }


            @Override
            public void onFinish() {
                running = false;
                index++;
                if (index < time_list.size()) {
                    timerListView.setSelection(index);
                    taskTextView.setText(task_list.get(index));
                    mTimeLeft = time_list.get(index);
                    mTimeLeft--;
                    mediaPlayer.stop();
                    if(taskTextView.getText().equals("Finish"))
                    {
                        finishMediaPlayer.start();
                    }
                    else if(taskTextView.getText().equals(workTime))
                    {
                        workMediaPlayer.start();
                    }
                    else {
                        try {
                            mediaPlayer.prepare();
                            mediaPlayer.seekTo(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                    }
                    startTimer();
                }

            }
        }.start();
        running = true;
    }

    public void pauseTimer() {
        countDownTimer.cancel();
        running = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (running) {
            serviceIntent = new Intent(this, TimerService.class);
            serviceIntent.putExtra("index", index);
            serviceIntent.putExtra("time_list", time_list);
            serviceIntent.putExtra("mTimeLeft", mTimeLeft);
            countDownTimer.cancel();
            startService(serviceIntent);
            bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (is_service_active) {
            mTimeLeft = timerService.getmTimeLeft();
            index = getIndex();
            stopService(serviceIntent);
            unbindService(mConnection);
            is_service_active = false;

        }
        if (index < task_list.size()) {
            taskTextView.setText(task_list.get(index));
            startTimer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("serviceactive", String.valueOf(is_service_active));
        if (is_service_active) {
            unbindService(mConnection);
            stopService(serviceIntent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(running)
        {
            pauseTimer();
        }
        new AlertDialog.Builder(this).setTitle(this.getString(R.string.MsgTitle))
                .setMessage(this.getString(R.string.Msg))
                .setPositiveButton(this.getString(R.string.MsgBtnPos), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        countDownTimer.cancel();
                        finish();
                        dialog.dismiss();

                    }

                })
                .setNegativeButton(this.getString(R.string.MsgBtnNeg), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startTimer();
                    }
                })
                .show();
        return true;
    }



}