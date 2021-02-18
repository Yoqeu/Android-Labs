package com.example.battleships;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class DeploymentActivity extends AppCompatActivity implements DeploymentBoardView.DeploymentListener {

    private TextView textGamemode;

    private TextView textDeployStatus;
    private DeploymentBoardView myBoardViewDeployment;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchRotation;
    String opponentId, creatorId, lobbyId;
    ArrayList<BattleshipGame> games;
    protected TextView roomId;
    protected Button btnCopy, btnDeploy;

    private boolean isFragmentReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_deployment);

        myBoardViewDeployment = (DeploymentBoardView) findViewById(R.id.myBoardViewDeployment);
        switchRotation = (Switch) findViewById(R.id.switch_rotation);
        btnCopy = findViewById(R.id.btnCopy);
        roomId = findViewById(R.id.roomIdTextView);
        //roomId.setText(room.getId());
        Bundle extras = getIntent().getExtras();
        opponentId = extras.getString("opponentId");
        creatorId = extras.getString("creatorId");
        lobbyId = extras.getString("lobbyId");

        textDeployStatus = (TextView) findViewById(R.id.textDeployStatus);
        games = new ArrayList<BattleshipGame>();

        myBoardViewDeployment.addListener(this);

        btnDeploy = findViewById(R.id.btnDeploy);
        btnDeploy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeploymentActivity.this, GameActivity.class);
                intent.putExtra("BOARD", myBoardViewDeployment.getBoard());
                startActivity(intent);
                finish();
            }
        });

        switchRotation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               myBoardViewDeployment.rotationMode = isChecked;
            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) DeploymentActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("tag_output", roomId.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(DeploymentActivity.this, "Copied!", Toast.LENGTH_SHORT).show();
            }
        });
        updateDeployStatus();
    }

    @Override
    public void onShipDeployed(Ship ship) {
        updateDeployStatus();
    }

    @SuppressLint("SetTextI18n")
    public void updateDeployStatus() {
        int shipTotal = myBoardViewDeployment.getRemainingShips();

        if (shipTotal == 0)
            textDeployStatus.setText(getString(R.string.deployment_all_ships));
        else
            textDeployStatus.setText(getString(R.string.deployment_remaining_ships) + shipTotal);

        checkReady();
    }

    public void onFragmentUpdate(boolean isFragmentReady){
        this.isFragmentReady = isFragmentReady;
        checkReady();
    }

    public void checkReady() {
        int shipTotal = myBoardViewDeployment.getRemainingShips();
        boolean isReady = (shipTotal == 0 && isFragmentReady);
        //buttonDeploy.setEnabled(isReady);
    }

    public void onClickReset(View v) {
        myBoardViewDeployment.onCreate();
        updateDeployStatus();
        checkReady();
    }

}

