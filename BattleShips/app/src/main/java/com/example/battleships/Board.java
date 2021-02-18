package com.example.battleships;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Board implements Serializable {
    public interface BoardListener {
        void onShipHit(Ship ship);

        void onShipMiss();
    }

    private final List<BoardListener> boardListeners = new ArrayList<>();

    public void addBoardListener(BoardListener listener) {
        if (!boardListeners.contains(listener))
            boardListeners.add(listener);
    }

    private void notifyShipHit(Ship ship) {
        for (BoardListener listener : boardListeners)
            listener.onShipHit(ship);
    }

    private void notifyShipMiss() {
        for (BoardListener listener : boardListeners)
            listener.onShipMiss();
    }

    private final int size;

    private Place[][] boardPlaces;
    private Random rand;
    private List<Ship> boardShips;


    public Board(int size) {
        this.size = size;

        boardPlaces = new Place[size][size];
        for (int i = 0; i < boardPlaces.length; i++)
            for (int j = 0; j < boardPlaces[i].length; j++)
                boardPlaces[i][j] = new Place(i, j);

        rand = new Random(System.nanoTime());
        boardShips = new ArrayList<>();
    }

    public void addRandomShips() {
        List<Ship> randomShips = Ship.getShips();
        while (randomShips.size() > 0) {
            Ship ship = randomShips.get(0);
            Direction dir = getRandomDirection();
            ship.direction = dir;
            int x = getRandomCoordinate();
            int y = getRandomCoordinate();

            boolean placedShip = placeShip(ship, x, y);
            if (placedShip)
                randomShips.remove(0);
        }
    }

    private Direction getRandomDirection() {
        return rand.nextBoolean() ? Direction.Horizontal : Direction.Vertical;
    }

    private int getRandomCoordinate() {
        return rand.nextInt(size());
    }

    public int size() {
        return size;
    }

    public int getTotalShips() {
        return boardShips.size();
    }

    public boolean removeShip(Ship ship) {
        if (ship == null || ship.placesOwned.isEmpty())
            return false;

        for (Vector2 pos : ship.placesOwned) {
            placeAt(pos).setShip(null);
            placeAt(pos).setHit(false);
        }

        ship.placesOwned.clear();
        boardShips.remove(ship);
        return true;
    }

    public boolean placeShip(Ship ship, Vector2 pos) {
        return placeShip(ship, pos.x, pos.y);
    }

    public boolean placeShip(Ship ship, int x, int y) {
        List<Vector2> placesForShip = new ArrayList<Vector2>();

        for (int i = 0; i < ship.size; i++) {
            Vector2 nextPlace;
            if (ship.direction == Direction.Horizontal)
                nextPlace = new Vector2(x + i, y);
            else
                nextPlace = new Vector2(x, y + i);

            if (!isValidPlace(nextPlace) || placeAt(nextPlace).hasShip())
                return false;

            placesForShip.add(nextPlace);
        }

        for (Vector2 p : placesForShip) {
            placeAt(p).setShip(ship);
            placeAt(p).setHit(false);
            ship.placesOwned.add(p);
        }

        boardShips.add(ship);
        return true;
    }

    public synchronized boolean hit(Vector2 pos) {
        return hit(pos.x, pos.y);
    }

    public synchronized boolean hit(int x, int y) {
        return hit(placeAt(x, y));
    }

    public synchronized boolean hit(Place place) {
        if (!isValidPlace(place) || place.isHit())
            return false;

        place.setHit(true);
        if (place.hasShip())
            notifyShipHit(place.getShip());
        else
            notifyShipMiss();

        return true;
    }

    public synchronized Place placeAt(Vector2 pos) {
        return placeAt(pos.x, pos.y);
    }

    public synchronized Place placeAt(int x, int y) {
        if (x < 0 || y < 0 || x >= size || y >= size)
            return null;
        return boardPlaces[y][x];
    }

    public boolean isValidPlace(Vector2 p) {
        if (p == null) return false;

        return !(p.x < 0 || p.x >= size || p.y < 0 || p.y >= size);
    }

    public boolean isValidPlace(Place p) {
        return isValidPlace(p.getPosition());
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }

    public static Board fromJSON(String boardJSON) {
        try {
            return new Gson().fromJson(boardJSON, Board.class);
        } catch (JsonParseException ex) {
            return null;
        }
    }

    public String boardToString() {
        String result = "Hit\n";
        for (int i = 0; i < boardPlaces.length; i++) {
            for (int j = 0; j < boardPlaces.length; j++) {
                result += (boardPlaces[i][j].isHit()) ? "O" : "#";
            }
            result += "\n";
        }
        result += "Ships\n";
        for (int i = 0; i < boardPlaces.length; i++) {
            for (int j = 0; j < boardPlaces.length; j++) {
                result += (boardPlaces[i][j].getShip() == null) ? "#" : "O";
            }
            result += "\n";
        }
        return result;
    }
}
