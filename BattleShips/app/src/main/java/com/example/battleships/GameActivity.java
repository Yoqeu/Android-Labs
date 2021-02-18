package com.example.battleships;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements BattleshipGame.GameListener {
    private BattleshipGame gameMain;
    private Board boardOpponent, boardOwn;
    private int sunkShipsByUs;
    private boolean isSoundEnabled = true;
    DatabaseReference databaseReference;
    BattleshipGame game;
    private int sunkShipsByOpponent;
    private Switch switchSound;
    String opponentId, creatorId, lobbyId;
    NetworkBoardView ownBoardView, opponentBoardView;

    private enum Sound {Hit, Sink, Gameover}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("SOUND", isSoundEnabled);
        outState.putInt("SUNKCPU", sunkShipsByOpponent);
        outState.putInt("SUNKHUMAN", sunkShipsByUs);
        outState.putSerializable("DEPLOYED", boardOwn);
        outState.putSerializable("RAND", boardOpponent);
        Log.d("Debug", "Saving game");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        isSoundEnabled = savedInstanceState.getBoolean("SOUND");
        switchSound.setChecked(isSoundEnabled);

        sunkShipsByOpponent = savedInstanceState.getInt("SUNKCPU");
        sunkShipsByUs = savedInstanceState.getInt("SUNKHUMAN");

        boardOwn = (Board) savedInstanceState.getSerializable("DEPLOYED");
        ownBoardView.setBoard(boardOwn);

        boardOpponent = (Board) savedInstanceState.getSerializable("RAND");
        opponentBoardView.setBoard(boardOpponent);
    }

    public void restart() {
        Bundle extras = getIntent().getExtras();
        opponentId = extras.getString("opponentId");
        creatorId = extras.getString("creatorId");
        lobbyId = extras.getString("lobbyId");


        boolean weGoFirst = false;
//        if (extras != null) {
//            boardOwn = (Board) extras.get("OWN");
//            boardOpponent = (Board) extras.get("OPPONENT");
//            weGoFirst = extras.getBoolean("FIRST");
//        }

        boardOwn.addBoardListener(new Board.BoardListener() {
            @Override
            public void onShipHit(Ship ship) {
                opponentBoardView.invalidate();
                ownBoardView.invalidate();
                playSound(Sound.Hit);
                if (ship.isDestroyed()) {
                    playSound(Sound.Sink);
                    sunkShipsByOpponent++;

                    if (sunkShipsByOpponent >= boardOwn.getTotalShips())
                        showDialogGameover(true, true);
                }
            }

            @Override
            public void onShipMiss() {
                opponentBoardView.invalidate();
                ownBoardView.invalidate();
            }
        });


        boardOpponent.addBoardListener(new Board.BoardListener() {
            @Override
            public void onShipHit(Ship ship) {
                opponentBoardView.invalidate();
                ownBoardView.invalidate();

                playSound(Sound.Hit);

                if (ship.isDestroyed()) {
                    playSound(Sound.Sink);
                    sunkShipsByUs++;

                    if (sunkShipsByUs >= boardOpponent.getTotalShips())
                        showDialogGameover(false, true);
                }
            }

            @Override
            public void onShipMiss() {
                opponentBoardView.invalidate();
                ownBoardView.invalidate();
            }
        });


        final Player playerEnemy = new EnemyPlayer(boardOwn, true);
        Player playerHost = new Player(boardOpponent, true);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Game").child(lobbyId);

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(opponentId)) {
            opponentBoardView.setBoard(boardOpponent);
            opponentBoardView.disableBoardTouch = !weGoFirst;

        } else {
            ownBoardView.setBoard(boardOwn);
            ownBoardView.disableBoardTouch = true;
        }

        gameMain = new BattleshipGame(playerHost, playerEnemy);
        gameMain.addGameListener(this);

        switchSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSoundEnabled = isChecked;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_network_game);

        ownBoardView = (NetworkBoardView) findViewById(R.id.board_view_network_small);
        opponentBoardView = (NetworkBoardView) findViewById(R.id.board_view_network_big);

        switchSound = (Switch) findViewById(R.id.switch_network_sound);
        switchSound.setChecked(isSoundEnabled);

        if (savedInstanceState != null)
            return;

        restart();
    }

    @Override
    public void onBackPressed() {
        showDialogExit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_game_menu_sound) {
            isSoundEnabled = !isSoundEnabled;
            switchSound.setChecked(isSoundEnabled);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTurnChange(Player currentPlayer) {
        Log.d("Debug", "Turn change! " + currentPlayer);
        if (currentPlayer instanceof Player) {
            opponentBoardView.disableBoardTouch = true;
            //textCurrentPlayer.setText("Opponent");
        } else {
            opponentBoardView.disableBoardTouch = false;
            //textCurrentPlayer.setText(getString(R.string.game_turn_player));
        }
        opponentBoardView.invalidate();
        ownBoardView.invalidate();
    }

    public void playSound(Sound sound) {
        if (!isSoundEnabled)
            return;

        MediaPlayer mp;

        if (sound == Sound.Hit)
            mp = MediaPlayer.create(getApplicationContext(), R.raw.hit);
        else if (sound == Sound.Sink)
            mp = MediaPlayer.create(getApplicationContext(), R.raw.sink);
        else
            mp = MediaPlayer.create(getApplicationContext(), R.raw.gameover);

        mp.start();
    }

    public void showDialogExit() {
        AlertDialog.Builder dialogExit = new AlertDialog.Builder(this);
        dialogExit.setTitle(getString(R.string.game_exit_title));
        dialogExit.setMessage(R.string.game_exit_message);
        dialogExit.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                NetworkBoardView.sendPacket(new ConditionsGameover(false));
                Intent i = new Intent(GameActivity.this, MainMenuActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void showDialogGameover(boolean weWon, boolean sendPacket) {
        String message;
        if (weWon)
            message = getString(R.string.game_won);

        else
            message = getString(R.string.game_lost);


        boolean theyWon = !weWon;
        ConditionsGameover packet = new ConditionsGameover(theyWon);

        if (sendPacket)
            NetworkBoardView.sendPacket(packet);

        playSound(Sound.Gameover);

        AlertDialog.Builder dialogGameover = new AlertDialog.Builder(this);
        dialogGameover.setTitle(getString(R.string.game_gameover_title));
        dialogGameover.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(GameActivity.this, MainMenuActivity.class);
                startActivity(i);
                finish();
            }
        });
        dialogGameover.setIcon(android.R.drawable.ic_dialog_alert);
        dialogGameover.setMessage(message);
        dialogGameover.show();
    }
}