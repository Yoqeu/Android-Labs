package com.example.tabatatimer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

public class SetsAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Timer> timers;
    ImageView imageViewSettings;
    Database db;
    LinearLayout linearLayout;

    public SetsAdapter(Context context, ArrayList<Timer> timers) {
        this.context = context;
        this.timers = timers;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = new Database(context);
    }

    Timer getTimer(int position) {
        return (Timer) getItem(position);
    }

    @Override
    public int getCount() {
        return timers.size();
    }

    @Override
    public Object getItem(int position) {
        return timers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item, parent, false);
        }
        Timer timer = getTimer(position);
        imageViewSettings = view.findViewById(R.id.imageViewSettings);
        TextView textViewTimer = view.findViewById(R.id.textView);

        linearLayout = view.findViewById(R.id.linearLayout1);
        linearLayout.setBackgroundColor(timer.getColor());
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, TimerActivity.class);
                intent.putExtra("id", timer.getId());
                context.startActivity(intent);
            }
        });
        imageViewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                Intent intent = new Intent(context, AddActivity.class);
                                intent.putExtra("id", timer.getId());
                                context.startActivity(intent);
                                return true;
                            case R.id.menu2:
                                db.open();
                                db.delete(timer.getId());
                                db.close();
                                return true;
                            default:
                                return false;

                        }
                    }
                });
            }
        });
        textViewTimer.setText(timer.getTitle());
        return view;
    }

    }

