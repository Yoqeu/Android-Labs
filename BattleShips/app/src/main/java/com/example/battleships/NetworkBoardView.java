package com.example.battleships;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class NetworkBoardView extends BoardView implements NetworkInterface{
    public boolean showShips = false;
    public boolean receivesPackets = false;
    Board board;

    public static final BlockingQueue<Conditions> packetList = new ArrayBlockingQueue<Conditions>(20);
    //public static boolean isRunning = false;

    public static synchronized void sendPacket(Conditions p) {
        try {
            packetList.put(p);
        }catch (InterruptedException e){
            Log.d("Debug", "addPacket interrupted");
        }
    }

    @Override
    public boolean isPlacePainted(Place p) {
        if(showShips) {
            invalidate();
            return p.isHit() || p.hasShip();
        }
        else
            return super.isPlacePainted(p);
    }

    @Override
    public Paint getPlacePaint(Place p) {
        if(!showShips)
            return super.getPlacePaint(p);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (p.isHit() && p.hasShip())
            paint.setColor(Color.BLACK);
        else if (p.hasShip())
            paint.setColor(Color.GREEN);

        else if (p.isHit())
            paint.setColor(Color.RED);

        else
            paint.setColor(Color.MAGENTA);

        invalidate();
        return paint;
    }

    @Override
    public void onBoardTouch(int x, int y) {
        if(disableBoardTouch)
            return;

        //Place p = board.placeAt(x, y);
        ConditionsHit p = new ConditionsHit(x, y);
        sendPacket(p);
        super.onBoardTouch(x, y);
    }

    public void onCreate(Activity activity){
        board = new Board(10);
        super.setBoard(board);
    }


    @Override
    public void onReceive(Conditions p) {
        if(receivesPackets && p instanceof ConditionsHit){
            Log.d("Debug", "Board received PacketHit: " + p);
            ConditionsHit packetHit = (ConditionsHit)p;
            int x = packetHit.X;
            int y = packetHit.Y;

            if(getBoard().placeAt(x, y).isHit())
                return;

            Log.d("Debug", "Hitting");
            getBoard().hit(packetHit.X, packetHit.Y);
        }
    }

    public NetworkBoardView(Context context) {
        super(context);
    }

    public NetworkBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NetworkBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
interface NetworkInterface {
    void onReceive(final Conditions p);
}


