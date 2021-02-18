package com.example.battleships;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class DeploymentBoardView extends SpectatorBoardView {

    public interface DeploymentListener {
        void onShipDeployed(Ship ship);
    }

    private final List<DeploymentListener> listeners = new ArrayList<>();

    public void addListener(DeploymentListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    private void notifyShipDeployed(Ship ship) {
        for (DeploymentListener listener : listeners)
            listener.onShipDeployed(ship);
    }


    public DeploymentBoardView(Context context) {
        super(context);
        onCreate();
    }

    public DeploymentBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public DeploymentBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreate();
    }

    private Board board;
    private List<Ship> listShips;

    public boolean rotationMode = false;

    public void onCreate() {
        listShips = Ship.getShips();

        board = new Board(10);
        super.setBoard(board);
    }


    public int getRemainingShips() {
        return listShips.size();
    }

    @Override
    public void onBoardTouch(int x, int y) {
        if (rotationMode) {
            Place p = board.placeAt(x, y);

            if (p.hasShip()) {
                Ship s = p.getShip();
                Vector2 topPosition = s.placesOwned.get(0);
                Direction originalDir = s.direction;
                board.removeShip(s);

                s.flipDirection();
                boolean validRotation = board.placeShip(s, topPosition);

                if (!validRotation) {
                    board.removeShip(s);
                    s.direction = originalDir;
                    board.placeShip(s, topPosition);
                }
            }
        } else {
            if (listShips.size() > 0) {
                Ship ship = listShips.get(0);
                boolean successfulDeploy = board.placeShip(ship, x, y);
                if (successfulDeploy) {
                    listShips.remove(0);
                    notifyShipDeployed(ship);
                }
            }
        }
        invalidate();
    }

    @Override
    public Paint getPlacePaint(Place p) {
        Ship ship = p.getShip();
        if (ship == null)
            return super.getPlacePaint(p);
        else {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(ship.preferredColor);
            return paint;
        }
    }

    @Override
    public boolean isPlacePainted(Place p) {
        return p.hasShip();
    }
}
