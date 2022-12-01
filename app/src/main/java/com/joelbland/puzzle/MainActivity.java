package com.joelbland.puzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    public static int STATUS_BAR_HEIGHT = 24;
    public static int ACTION_BAR_HEIGHT = 56;
    private PuzzleView puzzleView;
    private Puzzle puzzle;

    private int statusBarHeight;
    private int actionBarHeight;
    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        puzzle = new Puzzle();

        // Determine height of action and status bar
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screentHeight = size.y;
        int puzzleWidth = size.x;

        Resources res = getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();
        float pixelDensity = metrics.density;

        actionBarHeight = (int) (pixelDensity * ACTION_BAR_HEIGHT);
        TypedValue tv = new TypedValue();
        if(getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, metrics);
        }

        statusBarHeight = (int) (pixelDensity * STATUS_BAR_HEIGHT);
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if(resourceId != 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }

        // Remove action and status bar height from game screen
        int puzzleHeight = screentHeight - actionBarHeight - statusBarHeight;
        puzzleView = new PuzzleView(this, puzzleWidth, puzzleHeight, puzzle.getNumberOfParts());

        // Init touch handlers
        DoubleTapHandler dth = new DoubleTapHandler();
        detector = new GestureDetector(this, dth);
        detector.setOnDoubleTapListener(dth);

        // Call Puzzle class to scramble words and PuzzleView to build GUI
        String [] scrambled = puzzle.scramble();
        puzzleView.fillGui(scrambled);
        puzzleView.enableListener(this);
        setContentView(puzzleView);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int index = puzzleView.indexOfTextView(v);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // initialize data before move
                puzzleView.updateStartPositions(index, (int) event.getY());
                // bring v to front
                puzzleView.bringChildToFront(v);
                break;
            case MotionEvent.ACTION_MOVE:
                // update y position of TextView being moved
                puzzleView.moveTextViewVertically(index, (int) event.getY());
                break;
            case MotionEvent.ACTION_UP:
                // move is complete, swap the 2 TextViews
                int newPosition = puzzleView.tvPosition(index);
                puzzleView.placeTextViewAtPosition(index, newPosition);
                // if user just won, disable listener to stop the game
                if(puzzle.solved(puzzleView.currentSolution())) {
                    puzzleView.disableListener();
                    break;
                }

        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return true;
    }

    // Doubletap on "Mobile" changes to "Android" once game is complete
    private class DoubleTapHandler extends GestureDetector.SimpleOnGestureListener {
        public boolean onDoubleTapEvent(MotionEvent event) {
            int touchY = (int) event.getRawY();

            int index = puzzleView.indexOfTextView(touchY - actionBarHeight - statusBarHeight);
            if(puzzleView.getTextViewText(index)
                    .equals(puzzle.wordToChange())) {
                puzzleView.setTextViewText(index, puzzle.replacementWord());
            }
            return true;
        }
    }
}