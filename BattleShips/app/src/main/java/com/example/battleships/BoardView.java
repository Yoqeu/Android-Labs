package com.example.battleships;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BoardView extends View {
    private final int boardColor = Color.rgb(102, 163, 255);
    private final int boardLineColor = Color.WHITE;
    private final int shipHitColor = Color.RED;
    private final int shipMissColor = Color.GREEN;

    private final Paint boardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    {
        boardPaint.setColor(boardColor);
    }

    private final Paint boardLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    {
        boardLinePaint.setColor(boardLineColor);
        boardLinePaint.setStrokeWidth(2);
    }

    private final Paint shipHitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    {
        shipHitPaint.setColor(shipHitColor);
        shipHitPaint.setStrokeWidth(1);
    }

    private final Paint shipMissPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    {
        shipMissPaint.setColor(shipMissColor);
        shipMissPaint.setStrokeWidth(1);
    }

    private Board board;

    public BoardView(Context context) {
        super(context);
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
        invalidate();
    }

    public boolean disableBoardTouch = false;

    public void onBoardTouch(int x, int y) {
        if (!disableBoardTouch) {
            board.hit(x, y);
        }
        invalidate();
    }

    public Paint getPlacePaint(Place p) {
        Ship s = p.getShip();
        return s == null ? shipHitPaint : shipMissPaint;
    }

    public boolean isPlacePainted(Place p) {
        return p.isHit();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                int xy = locatePlace(event.getX(), event.getY());
                if (xy >= 0) {
                    int x = xy / 100;
                    int y = xy % 100;
                    onBoardTouch(x, y);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawPlaces(canvas);
        drawGridLines(canvas);
    }

    private void drawPlaces(Canvas canvas) {
        if (board == null)
            return;

        for (int x = 0; x < board.size(); x++) {
            for (int y = 0; y < board.size(); y++) {
                Place place = board.placeAt(x, y);
                if (isPlacePainted(place)) {
                    // Fill with paint
                    float left = x * lineGap(); // X1
                    float top = y * lineGap(); // Y1
                    float right = left + lineGap(); // X2 = X1 + Width
                    float bottom = top + lineGap(); // Y2 = Y1 + Height
                    Paint paint = getPlacePaint(place);
                    canvas.drawRect(left, top, right, bottom, paint);
                }
            }
        }

    }

    private void drawBackground(Canvas canvas) {
        final float maxCoord = maxCoord();
        canvas.drawRect(0, 0, maxCoord, maxCoord, boardPaint);
    }

    private void drawGridLines(Canvas canvas) {
        final float maxCoord = maxCoord();
        final float placeSize = lineGap();
        for (int i = 0; i < numOfLines(); i++) {
            float xy = i * placeSize;
            canvas.drawLine(0, xy, maxCoord, xy, boardLinePaint); // horizontal line
            canvas.drawLine(xy, 0, xy, maxCoord, boardLinePaint); // vertical line
        }
    }

    protected float lineGap() {
        if (board == null)
            return Math.min(getMeasuredHeight(), getMeasuredWidth());

        return (Math.min(getMeasuredWidth(), getMeasuredHeight()) / (float) board.size());
    }

    private int numOfLines() {
        if (board == null)
            return 1;

        return board.size() + 1;
    }

    protected float maxCoord() {
        return lineGap() * (numOfLines() - 1);
    }

    private int locatePlace(float x, float y) {
        if (board == null)
            return -1;

        if (x <= maxCoord() && y <= maxCoord()) {
            final float placeSize = lineGap();
            int ix = (int) (x / placeSize);
            int iy = (int) (y / placeSize);
            return ix * 100 + iy;
        }
        return -1;
    }
}
