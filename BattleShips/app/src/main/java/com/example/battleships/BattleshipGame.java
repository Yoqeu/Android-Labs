// Author: Jose Perez <josegperez@mail.com> and Diego Reynoso
package com.example.battleships;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BattleshipGame implements Serializable {
    public interface GameListener {
        void onTurnChange(Player currentPlayer);
    }

    private final List<GameListener> listeners = new ArrayList<>();

    public void addGameListener(GameListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    private void notifyTurnChange(Player player) {
        for (GameListener listener : listeners)
            listener.onTurnChange(player);
    }

    private Player playerHost;
    private Player playerOpponent;
    private int currentTurn = 0;

    public BattleshipGame(Player playerHost, Player playerOpponent) {
        this.playerHost = playerHost;
        this.playerOpponent = playerOpponent;

        Board.BoardListener listener = new Board.BoardListener() {
            @Override
            public void onShipHit(Ship ship) {
                if (!getCurrentPlayer().isAllowedMultipleShots) {
                    Log.d("Debug", "Player not allowed multiple shots");
                    nextTurn();
                }
                else {// Player is allowed more shots so let them know
                    Log.d("Debug", "Player allowed multiple shots");
                    notifyTurnChange(getCurrentPlayer());
                }
            }

            @Override
            public void onShipMiss() {
                // If nothing was hit change turns
                nextTurn();
            }
        };

        playerHost.board.addBoardListener(listener);
        playerOpponent.board.addBoardListener(listener);
    }

    public void nextTurn() {
        currentTurn++;
        if (currentTurn > 1)
            currentTurn = 0;

        notifyTurnChange(getCurrentPlayer());
    }

    private Player getCurrentPlayer() {
        if (currentTurn == 0)
            return playerHost;
        else
            return playerOpponent;
    }
}
