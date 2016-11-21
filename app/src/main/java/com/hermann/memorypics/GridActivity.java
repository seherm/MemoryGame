package com.hermann.memorypics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by OkanO on 11/3/2016.
 */

public class GridActivity extends AppCompatActivity {

    int level;
    int firstClick = -1, seccondClick = -1;
    boolean clickedOnce;
    ArrayList<String> opened;
    float scale;
    int numberOfPlayers;
    String[] listOfPlayers;
    int[] counters;
    int[] successes;
    int[] colors;
    int currentPlayer = 0;
    TextView[] playerNames;
    TextView score;
    long startTime;

    Thread t;

    ContainerView container;

    ArrayList<String> resources;

    GridView gridview;

    String[] list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_super_view);

        startTime = System.currentTimeMillis();

        t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                setPlayer();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

        final ImageView sound = (ImageView) findViewById(R.id.sound);

        score = (TextView) findViewById(R.id.score);

        if (Main.player.isPlaying()){
            sound.setImageURI(Main.resourceToUri(GridActivity.this, R.mipmap.speaker));
        } else {
            sound.setImageURI(Main.resourceToUri(GridActivity.this, R.mipmap.speaker_off));
        }

        final ImageView home = (ImageView) findViewById(R.id.home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Main.player.isPlaying()){
                    Main.player.pause();
                    sound.setImageURI(Main.resourceToUri(GridActivity.this, R.mipmap.speaker_off));
                } else {
                    Main.player.start();
                    sound.setImageURI(Main.resourceToUri(GridActivity.this, R.mipmap.speaker));
                }
            }
        });

        setInitialProperties();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("my_array", resources);
        outState.putIntArray("counters", counters);
        outState.putIntArray("successes", successes);
        outState.putInt("level", level);
        outState.putIntArray("colors", colors);
        outState.putInt("currentPlayer", currentPlayer);
        outState.putStringArrayList("opened", opened);
        outState.putBoolean("clickedOnce", clickedOnce);
        outState.putInt("firstClick", firstClick);
        outState.putInt("secondClick", seccondClick);
        outState.putBoolean("hidden", gridview.getAlpha() == 0.0f);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        resources = savedInstanceState.getStringArrayList("my_array");
        counters = savedInstanceState.getIntArray("counters");
        successes = savedInstanceState.getIntArray("successes");
        level = savedInstanceState.getInt("level");
        colors = savedInstanceState.getIntArray("colors");
        currentPlayer = savedInstanceState.getInt("currentPlayer");
        opened = savedInstanceState.getStringArrayList("opened");
        clickedOnce = savedInstanceState.getBoolean("clickedOnce");
        firstClick = savedInstanceState.getInt("firstClick");
        seccondClick = savedInstanceState.getInt("secondClick");
        boolean hidden = savedInstanceState.getBoolean("hidden");
        gridview.setAlpha(hidden?0.0f:1.0f);
        super.onRestoreInstanceState(savedInstanceState);

        setInitialProperties();
    }

    void setPlayer(){
        if (numberOfPlayers == 1){
            score.setText(listOfPlayers[currentPlayer]);
            long timeGoneMillis = System.currentTimeMillis() - startTime;
            long timeGoneSecs = timeGoneMillis / 1000;
            playerNames[currentPlayer].setText("Clicks: "+ counters[currentPlayer] + "\nSuccesses: " + successes[currentPlayer] + "\nTime: " + timeGoneSecs);
        } else {
            playerNames[currentPlayer].setText(listOfPlayers[currentPlayer] + ": "+ successes[currentPlayer]);
        }
    }

    void setInitialProperties(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            list = extras.getStringArray("list");
            numberOfPlayers = extras.getInt("numberOfPlayers");
            listOfPlayers = extras.getStringArray("listOfPlayers");
            level = extras.getInt("level");
            //The key argument here must match that used in the other activity
        }

        container = (ContainerView) findViewById(R.id.container);

        gridview = (GridView) findViewById(R.id.gridview);

        if (resources == null){
            opened = new ArrayList<>();
//            container.removeAllViews();

            int numberOfImages;
            switch (level){
                case 1:
                    numberOfImages = 8;
                    break;

                case 2:
                    numberOfImages = 12;
                    break;

                case 3:
                    numberOfImages = 20;
                    break;

                case 4:
                    numberOfImages = 30;
                    break;

                default:
                    numberOfImages = 0;
                    break;
            }

            resources = new ArrayList<>();
            currentPlayer = 0;
            counters = new int[numberOfPlayers];
            successes = new int[numberOfPlayers];
            colors = new int[numberOfPlayers];
            playerNames = new TextView[numberOfPlayers];

            for (int i = 0; i < numberOfPlayers; i++){
                counters[i] = 0;
                successes[i] = 0;
                switch (i){
                    case 0:
                        colors[i] = Color.BLUE;
                        break;
                    case 1:
                        colors[i] = Color.RED;
                        break;
                    case 2:
                        colors[i] = Color.GREEN;
                        break;
                    default:
                        break;
                }

                TextView temp = new TextView(GridActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                temp.setTextColor(Color.parseColor("#324A5E"));
                temp.setLayoutParams(lp);
                temp.setTextSize(20);
                if (numberOfPlayers == 1){
                    score.setText(listOfPlayers[i]);
                    long timeGoneMillis = System.currentTimeMillis() - startTime;
                    long timeGoneSecs = timeGoneMillis / 1000;
                    temp.setText("Clicks: "+ counters[i] + "\nSuccesses: " + successes[i] + "\nTime: " + timeGoneSecs);
                } else {
                    temp.setText(listOfPlayers[i] + ": "+ successes[i]);
                }
                container.addView(temp);

                playerNames[i] = temp;
            }

            for (int i = 0; i<numberOfImages/2; i++){
                resources.add(list[i]);
                resources.add(list[i]);
            }

            resources = shufle(resources, 4);
        }

        scale = getResources().getDisplayMetrics().density;

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if (!opened.contains(resources.get(position))){
                    final GridAdapter adapter1 = (GridAdapter) gridview.getAdapter();
                    ArrayList<String> temp = adapter1.mThumbIds;

                    if (clickedOnce && position != firstClick){
                        counters[currentPlayer]++;

                        clickedOnce = false;
                        seccondClick = position;

                        if (temp.get(firstClick).equals(temp.get(seccondClick)) && firstClick != seccondClick){
                            opened.add(temp.get(firstClick));
                            adapter1.opened = opened;
                            successes[currentPlayer]++;
                        } else {
                            if (numberOfPlayers > 1){
                                currentPlayer++;
                                if (currentPlayer == numberOfPlayers){
                                    currentPlayer = 0;
                                }
                                Toast.makeText(getApplicationContext(), "Wrong! " + listOfPlayers[currentPlayer] + " is up next!", Toast.LENGTH_SHORT).show();
                            }

                            gridview.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (seccondClick != -1){
                                        firstClick = -1;
                                        seccondClick = -1;
                                        adapter1.firstClick = firstClick;
                                        adapter1.seccondClick = seccondClick;
                                        adapter1.notifyDataSetChanged();
                                    }
                                }
                            }, 1000);
                        }

                        adapter1.seccondClick = seccondClick;
                        adapter1.notifyDataSetChanged();

                        if (opened.size() == temp.size()/2){
                            t.interrupt();
                            gridview.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    // change image

                                    Display display = getWindowManager().getDefaultDisplay();
                                    Point size = new Point();
                                    display.getSize(size);

                                    float parentCenterX = size.x/2;
                                    float parentCenterY = size.y/2;

                                    int originalPos[] = new int[2];
                                    container.getLocationOnScreen( originalPos );

                                    float temHeight = container.getHeight();
                                    float tempWidth = container.getWidth();

                                    float aspectRatio = temHeight/tempWidth;
                                    long desiredWidth = Math.round(size.x*0.8);

                                    ResizeAnimation resize = new ResizeAnimation(container, tempWidth, temHeight, desiredWidth, aspectRatio*desiredWidth);
                                    resize.setDuration(1000);
//                                    resize.setInterpolator(new DecelerateInterpolator());
                                    resize.setFillEnabled(true);
                                    resize.setFillAfter(true);

                                    Rect rectangle = new Rect();
                                    Window window = getWindow();
                                    window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
                                    int statusBarHeight = rectangle.top;

                                    TranslateAnimation anim = new TranslateAnimation( 0, parentCenterX - desiredWidth/2 - originalPos[0] + (desiredWidth - tempWidth) , 0, parentCenterY - aspectRatio*desiredWidth/2 - originalPos[1] + statusBarHeight );
                                    anim.setDuration(1300);
//                                    anim.setInterpolator(new DecelerateInterpolator());
                                    anim.setFillAfter(true);
                                    anim.setFillEnabled(true);

                                    AnimationSet set = new AnimationSet(true);
                                    set.setFillEnabled(true);
//                                    set.setInterpolator(new DecelerateInterpolator());
                                    set.setFillEnabled(true);

                                    set.addAnimation(anim);
                                    set.addAnimation(resize);

                                    container.refresh(container);
                                    container.startAnimation(set);

                                    gridview.animate().alpha(0).setDuration(600);
                                }

                            }, 1000);
                        }

                    } else {
                        if (!clickedOnce) {
                            counters[currentPlayer]++;
                        }

                        clickedOnce = true;
                        firstClick = position;
                        seccondClick = -1;

                        adapter1.firstClick = firstClick;
                        adapter1.seccondClick = -1;
                        adapter1.notifyDataSetChanged();

                    }

                    setPlayer();
                }
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int numberOfColumns;
        switch (level){
            case 1:
                numberOfColumns = 4;
                break;

            case 2:
                numberOfColumns = 4;
                break;

            case 3:
                numberOfColumns = 4;
                break;

            case 4:
                numberOfColumns = 5;
                break;

            default:
                numberOfColumns = 1;
                break;
        }

        Integer dp = convertDpToPixel(10, this);

        Integer dimension = (Math.min(size.x, size.y-2*dp) - (numberOfColumns + 1)*dp)/numberOfColumns;

        GridAdapter adapter = new GridAdapter(this);
        adapter.mThumbIds = resources;
        adapter.opened = opened;
        adapter.level = level;
        adapter.firstClick = firstClick;
        adapter.seccondClick = seccondClick;
        gridview.setAdapter(adapter);

        gridview.setColumnWidth(dimension);
    }

    private ArrayList<String> shufle(ArrayList<String> arrayList, int numberOfLoops){
        for (int j = 0; j < numberOfLoops; j++){
            Random rnd = ThreadLocalRandom.current();
            for (int i = arrayList.size() - 1; i > 0; i--)
            {
                int index = rnd.nextInt(i + 1);
                // Simple swap
                String a = arrayList.get(index);
                arrayList.set(index, arrayList.get(i));
                arrayList.set(i, a);
            }
        }
        return arrayList;
    }

    public static int convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return Math.round(px);
    }

}
