package com.example.tabatatimer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jaredrummler.android.colorpicker.ColorShape;


public class AddActivity extends AppCompatActivity implements ColorPickerDialogListener{
    private Database db;
    Button btnColor;
    Button btnAdd;
    private TextView editTitle;
    private TextView editPrep;
    private TextView editWork;
    private TextView editRest;
    private TextView editCycles;
    private TextView editSets;
    private TextView editRestSets;
    CardView colorView;

    private int colorId = 0;
    private int Id = 0;


    @SuppressLint({"ResourceAsColor"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        colorView = findViewById(R.id.currentColorView);
        db = new Database(this);
        editTitle = findViewById(R.id.editTitle);
        editPrep = findViewById(R.id.editPrep);
        editWork = findViewById(R.id.editWork);
        editRest = findViewById(R.id.editRest);
        editCycles = findViewById(R.id.editCycles);
        editSets = findViewById(R.id.editSets);
        editRestSets = findViewById(R.id.editRestSets);
        Bundle args = getIntent().getExtras();
        if (args != null) {
            Id = args.getInt("id");
        }
        if (Id > 0) {
            db.open();
            Timer timer = db.getTimer(Id);
            editTitle.setText(timer.getTitle());
            editPrep.setText(String.valueOf(timer.getPrepTime()));
            editWork.setText(String.valueOf(timer.getWorkTime()));
            editRest.setText(String.valueOf(timer.getRestTime()));
            editCycles.setText(String.valueOf(timer.getCyclesAmount()));
            editSets.setText(String.valueOf(timer.getSetsAmount()));
            editRestSets.setText(String.valueOf(timer.getRest_sets()));
            colorId = timer.getColor();
        }
        db.close();

        colorView = findViewById(R.id.currentColorView);
        btnColor = findViewById(R.id.button_color);
        btnAdd = findViewById(R.id.button_add);

        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createColorPickerDialog();
                colorView.setCardBackgroundColor(colorId);
            }
    });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                String title = editTitle.getText().toString();
                int prep = Integer.parseInt(editPrep.getText().toString());
                int work = Integer.parseInt(editWork.getText().toString());
                int rest = Integer.parseInt(editRest.getText().toString());
                int cycles = Integer.parseInt(editCycles.getText().toString());
                int sets = Integer.parseInt(editSets.getText().toString());
                int relax_sets = Integer.parseInt(editRestSets.getText().toString());
                Timer timer = new Timer(Id, title, prep, work, rest, cycles, sets, relax_sets,
                        colorId);

                db.open();
                if (Id > 0) {
                    db.update(timer);
                }
                else {
                    db.add(timer);
                }
                db.close();
                setResult(RESULT_OK, intent);
                startActivity(intent);

            }
        });
    }

    private void createColorPickerDialog() {
        ColorPickerDialog.newBuilder()
                .setColor(Color.RED)
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowCustom(true)
                .setAllowPresets(true)
                .setColorShape(ColorShape.SQUARE)
                .show(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onColorSelected(int dialogId, int color) {
        colorId = color;
        colorView.setCardBackgroundColor(colorId);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }





}
