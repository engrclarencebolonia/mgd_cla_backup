package dev.dungeons.dragons.gems;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    private  static final int COMBONUMBER = 7;
    private int coef1 = 72;
    private int coef2 = 142;
    private int coef3 = 212;
    private int position1 = 5;
    private int position2 = 5;
    private int position3 = 5;
    private int[] slot = {1, 2, 3, 4, 5, 6, 7};

    private RecyclerView rv1;
    private RecyclerView rv2;
    private RecyclerView rv3;
    private CustomLayoutManager layoutManager1;
    private CustomLayoutManager layoutManager2;
    private CustomLayoutManager layoutManager3;

    private TextView jackpot;
    private TextView myCoins;
    private TextView bet;
    int myCoinsVal;
    int betVal;
    int jackpotVal;

    private GameLogic gameLogic;
    private GameSetting gameSetting;




    private int videoPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_main);
        gameSetting = new GameSetting(this);
        int playsoundValue = gameSetting.getPlaysound();

        ImageButton spinButton;
        ImageButton plusButton;
        ImageButton minusButton;
        ImageView settingsButton;

        AppCompatButton btnBack;

        SpinnerAdapter adapter;




        gameLogic = new GameLogic();
        settingsButton = findViewById(R.id.settings);
        spinButton = findViewById(R.id.spinButton);
        plusButton = findViewById(R.id.plusButton);
        minusButton = findViewById(R.id.minusButton);
        btnBack = findViewById(R.id.btnBack);
        jackpot = findViewById(R.id.jackpot);
        myCoins = findViewById(R.id.myCoins);
        bet = findViewById(R.id.bet);
        adapter = new SpinnerAdapter();

        rv1 = findViewById(R.id.spinner1);
        rv2 = findViewById(R.id.spinner2);
        rv3 = findViewById(R.id.spinner3);
        rv1.setHasFixedSize(true);
        rv2.setHasFixedSize(true);
        rv3.setHasFixedSize(true);

        layoutManager1 = new CustomLayoutManager(this);
        layoutManager1.setScrollEnabled(false);
        rv1.setLayoutManager(layoutManager1);
        layoutManager2 = new CustomLayoutManager(this);
        layoutManager2.setScrollEnabled(false);
        rv2.setLayoutManager(layoutManager2);
        layoutManager3 = new CustomLayoutManager(this);
        layoutManager3.setScrollEnabled(false);
        rv3.setLayoutManager(layoutManager3);

        rv1.setAdapter(adapter);
        rv2.setAdapter(adapter);
        rv3.setAdapter(adapter);
        rv1.scrollToPosition(position1);
        rv2.scrollToPosition(position2);
        rv3.scrollToPosition(position3);

        setText();
        updateText();

        rv1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    rv1.scrollToPosition(gameLogic.getPosition(0));
                    layoutManager1.setScrollEnabled(false);
                }
            }
        });

        rv2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    rv2.scrollToPosition(gameLogic.getPosition(1));
                    layoutManager2.setScrollEnabled(false);
                }
            }
        });

        rv3.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    rv3.scrollToPosition(gameLogic.getPosition(2));
                    layoutManager3.setScrollEnabled(false);
                    updateText();

                    if (gameLogic.getHasWon()) {
                        if (gameSetting.getPlaysound() == 1) {
                            gameSetting.playWinSound();
                        }

                        View rootView = findViewById(android.R.id.content);
                        Snackbar snackbar = Snackbar.make(rootView, "", 1800);

                        View customSnackbarView = getLayoutInflater().inflate(R.layout.win_splash, null);

                        // Get the winning pattern
                        int winningPattern = gameLogic.getWinningPattern();

                        // Identify the view in the RecyclerView corresponding to the winning pattern
                        View winningView = layoutManager3.findViewByPosition(winningPattern);

                        if (winningView != null) {
                            // Enlarge the winning pattern image by 10%
                            ImageView winningImageView = winningView.findViewById(R.id.spinner_item);
                            ViewGroup.LayoutParams layoutParams = winningImageView.getLayoutParams();
                            layoutParams.width = (int) (layoutParams.width * 1.1);
                            layoutParams.height = (int) (layoutParams.height * 1.1);
                            winningImageView.setLayoutParams(layoutParams);
                        }


                        TextView winCoins = customSnackbarView.findViewById(R.id.win_coins);
                        winCoins.setText(gameLogic.getPrize());

                        snackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.transparent, getTheme()));

                        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
                        snackbarLayout.addView(customSnackbarView, 0);

                        snackbar.show();

                        gameLogic.setHasWon(false);
                    }
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBack = new Intent(MainActivity.this, MainMenu.class);
                startActivity(intentBack);
            }
        });

        spinButton.setOnClickListener(v -> {
            spinButton.setEnabled(false);

            if (playsoundValue == 1) {
                gameSetting.playSpinSound();
            }

            layoutManager1.setScrollEnabled(true);
            layoutManager2.setScrollEnabled(true);
            layoutManager3.setScrollEnabled(true);

            gameLogic.getSpinResults();
            position1 = gameLogic.getPosition(0) + coef1;
            position2 = gameLogic.getPosition(1) + coef2;
            position3 = gameLogic.getPosition(2) + coef3;

            rv1.smoothScrollToPosition(position1);
            rv2.smoothScrollToPosition(position2);
            rv3.smoothScrollToPosition(position3);

            new android.os.Handler().postDelayed(() ->  spinButton.setEnabled(true), 4000);
        });

        plusButton.setOnClickListener(v -> {
            if(playsoundValue == 1){
                gameSetting.playSpinSound();
            }
            gameLogic.betUp();
            updateText();

        });

        minusButton.setOnClickListener(v -> {
            if(playsoundValue == 1){
                gameSetting.playSpinSound();
            }
            gameLogic.betDown();
            updateText();

        });

        settingsButton.setOnClickListener(v -> {
            if(playsoundValue == 1){
                gameSetting.playSpinSound();
            }
            GameSetting.showSettingsDialog(this);
        });
    }

    private void setText(){
        if(gameSetting.isFirstRun()){
            gameLogic.setTheCoins(995);
            gameLogic.setBet(5);
            gameLogic.setJackpot(100000);

            SharedPreferences.Editor editor = GameSetting.pref.edit();
            editor.putBoolean("firstRun", false);
            editor.apply();

        }else {
            String coins = GameSetting.pref.getString("coins", String.valueOf(995));
            String betString = GameSetting.pref.getString("bet", String.valueOf(5));
            String  jackpotString = GameSetting.pref.getString("jackpot", String.valueOf(100000));
//            Log.d("COINS",coins);
            myCoinsVal = Integer.valueOf(coins);
            betVal = Integer.valueOf(betString);
            jackpotVal = Integer.valueOf(jackpotString);
            gameLogic.setTheCoins(myCoinsVal);
            gameLogic.setBet(betVal);
            gameLogic.setJackpot(jackpotVal);
        }
    }

    private void updateText() {
        jackpot.setText(gameLogic.getJackpot());
        myCoins.setText(gameLogic.getTheCoins());
        bet.setText(gameLogic.getBet());

        SharedPreferences.Editor editor = GameSetting.pref.edit();
        editor.putString("coins",gameLogic.getTheCoins());
        editor.putString("bet",gameLogic.getBet());
        editor.putString("jackpot",gameLogic.getJackpot());
        editor.apply();
    }


    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView pic;

        public ItemViewHolder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.spinner_item);
        }
    }

    private class SpinnerAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            View view = layoutInflater.inflate(R.layout.spinner_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            int i = position < 7 ? position : position % COMBONUMBER;
            switch (slot[i]) {
                case 1:
                    holder.pic.setImageResource(R.drawable.combination_1);
                    break;
                case 2:
                    holder.pic.setImageResource(R.drawable.combination_2);
                    break;
                case 3:
                    holder.pic.setImageResource(R.drawable.combination_3);
                    break;
                case 4:
                    holder.pic.setImageResource(R.drawable.combination_4);
                    break;
                case 5:
                    holder.pic.setImageResource(R.drawable.combination_5);
                    break;
                case 6:
                    holder.pic.setImageResource(R.drawable.combination_6);
                    break;
                case 7:
                    holder.pic.setImageResource(R.drawable.combination_7);
                    break;
                default:
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameSetting.stopSounds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameSetting.resumeSounds();

    }

}

