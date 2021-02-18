package com.example.battleships;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;


public class SpectatorBoardView extends BoardView {
    public SpectatorBoardView(Context context) {
        super(context);
    }

    public SpectatorBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SpectatorBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isPlacePainted(Place p) {
        invalidate();
        return p.isHit() || p.hasShip();
    }

    @Override
    public void onBoardTouch(int x, int y) {
        invalidate();
    }

    @Override
    public Paint getPlacePaint(Place p){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (p.isHit() && p.hasShip())
            paint.setColor(Color.BLACK);
        else if (p.hasShip())
            paint.setColor(Color.GREEN);

        else if(p.isHit())
            paint.setColor(Color.RED);

        else
            paint.setColor(Color.MAGENTA);

        invalidate();
        return paint;
    }
}
