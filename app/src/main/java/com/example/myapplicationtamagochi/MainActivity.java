package com.example.myapplicationtamagochi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Button eat, sleep, play, fun;
    Handler handler;
    int setHunger = 5, setFatigue = 5, setBoredom = 5, setHappiness = 5;
    AnimationDrawable mAnimationDrawable;
    ImageView imageView;
    int dayTime = 0;
    int seconds = 0;
    ProgressBar showHunger;
    ProgressBar showFatigue;
    ProgressBar showBoredom;
    ProgressBar showHappiness;
    Drawable drawableHunger, drawableFatigue, drawableBoredom, drawableHappiness;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        imageView = findViewById(R.id.imageView);
        eat = findViewById(R.id.fixHunger);
        sleep = findViewById(R.id.fixFatigue);
        play = findViewById(R.id.fixBoredom);
        fun = findViewById(R.id.fixHappiness);
        drawableHunger = ContextCompat.getDrawable(this, R.drawable.custom_progressbar_hunger);
        drawableFatigue = ContextCompat.getDrawable(this, R.drawable.custom_progressbar_fatigue);
        drawableBoredom = ContextCompat.getDrawable(this, R.drawable.custom_progressbar_boredom);
        drawableHappiness = ContextCompat.getDrawable(this, R.drawable.custom_progressbar_happiness);
        showHunger = findViewById(R.id.progressBarHunger);
        showHunger.setProgressDrawable(drawableHunger);
        showHunger.setMax(100);

        showFatigue = findViewById(R.id.progressBarFatigue);
        showFatigue.setProgressDrawable(drawableFatigue);
        showFatigue.setMax(100);

        showBoredom = findViewById(R.id.progressBarBoredom);
        showBoredom.setProgressDrawable(drawableBoredom);
        showBoredom.setMax(100);

        showHappiness = findViewById(R.id.progressBarHappiness);
        showHappiness.setProgressDrawable(drawableHappiness);
        showHappiness.setMax(100);
        updateStatus();
        updateLevelGame();
        eat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tamagochi.hunger += setHunger;
                Tamagochi.fatigue -= setFatigue;
                Tamagochi.boredom -= setBoredom;
                updateLevelGame();
                handleTamagochiDeath();
                updateStatus();
                checkAlive();
            }
        });

        sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tamagochi.hunger -= setHunger;
                Tamagochi.fatigue += setFatigue;
                Tamagochi.boredom -= setBoredom;
                Tamagochi.happiness -= setHappiness;
                updateLevelGame();
                handleTamagochiDeath();
                updateStatus();
                checkAlive();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tamagochi.hunger -= setHunger;
                Tamagochi.fatigue -= setFatigue;
                Tamagochi.boredom += setBoredom;
                updateLevelGame();
                handleTamagochiDeath();
                updateStatus();
                checkAlive();
            }
        });

        fun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tamagochi.hunger -= setHunger;
                Tamagochi.fatigue += setFatigue;
                Tamagochi.boredom += setBoredom;
                Tamagochi.happiness += setHappiness;
                updateLevelGame();
                handleTamagochiDeath();
                updateStatus();
                checkAlive();
            }
        });

        Thread life = new Thread(new Runnable() {
            @Override
            public void run() {
                while (Tamagochi.alive) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (checkAlive()) {
                                Tamagochi.day++;
                            } else {
                                Tamagochi.alive = false;
                            }
                        }
                    });
                    handler.post(() -> {
                        updateStatus();
                        changeAnimationAndMessageByStatus();
                        updateLevelGame();
                    });
                    try {
                        Thread.sleep(dayTime);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        life.start();
        startTimer();
    }

    private void startTimer() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        seconds++;
                        if (checkAlive()) {
                            Tamagochi.hunger -= 3;
                            Tamagochi.fatigue -= 3;
                            Tamagochi.boredom -= 3;
                            Tamagochi.happiness -= 3;
                            updateStatus();
                        } else {
                            eat.setClickable(false);
                            sleep.setClickable(false);
                            play.setClickable(false);
                            fun.setClickable(false);
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    public void updateLevelGame() {
        if (Tamagochi.day <= 5) {
            dayTime = 5000;
        }
        if (Tamagochi.day > 5) {
            dayTime = 3000;
            setHunger = 10;
            setFatigue = 10;
            setBoredom = 10;
            setHappiness = 10;
        }
    }

    public boolean checkAlive() {
        if (Tamagochi.hunger == 0 || Tamagochi.boredom == 0 || Tamagochi.fatigue == 0 || Tamagochi.happiness == 0) {
            updateStatus();
            return false;
        }
        return true;
    }

    @SuppressLint("SetTextI18n")
    public void updateStatus() {
        showHunger.setProgress(Tamagochi.hunger);
        showFatigue.setProgress(Tamagochi.fatigue);
        showBoredom.setProgress(Tamagochi.boredom);
        showHappiness.setProgress(Tamagochi.happiness);
    }

    public void changeAnimationAndMessageByStatus() {
        // смена анимации в зависимости от статуса показателей
        if (Tamagochi.hunger >= 50 || Tamagochi.fatigue >= 50 ||
                Tamagochi.boredom >= 50 || Tamagochi.happiness >= 50) {
            imageView.setBackgroundResource(R.drawable.allprocentanimation);
            mAnimationDrawable = (AnimationDrawable) imageView.getBackground();
            mAnimationDrawable.start();
        }
        if (Tamagochi.hunger < 50 || Tamagochi.fatigue < 50 ||
                Tamagochi.boredom < 50 || Tamagochi.happiness < 50) {
            imageView.setBackgroundResource(R.drawable.halfprocentanimation);
            mAnimationDrawable = (AnimationDrawable) imageView.getBackground();
            mAnimationDrawable.start();
        }
        if (Tamagochi.hunger == 0 || Tamagochi.fatigue == 0 ||
                Tamagochi.boredom == 0 || Tamagochi.happiness == 0) {
            imageView.setBackgroundResource(R.drawable.dead);
        }
    }

    private void handleTamagochiDeath() {
        if (Tamagochi.hunger <= 0 || Tamagochi.fatigue <= 0 || Tamagochi.boredom <= 0 || Tamagochi.happiness <= 0) {
            Tamagochi.hunger = 0;
            Tamagochi.fatigue = 0;
            Tamagochi.boredom = 0;
            Tamagochi.happiness = 0;
            Toast.makeText(MainActivity.this, "Тамагочи умер! Жил он: " + Tamagochi.day + " дней", Toast.LENGTH_SHORT).show();
            Tamagochi.alive = false;
            Intent intent = new Intent(MainActivity.this, DeadActivity.class);
            intent.putExtra("TIME", seconds);
            startActivity(intent);
            mAnimationDrawable.stop();
            imageView.setBackgroundResource(R.drawable.dead);
        }
    }
}