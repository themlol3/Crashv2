package com.example.test2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.test2.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private int speed = 12;
    private int range = 400;
    private int health, start, delay;
    private int distance, outline;
    private static int width, height;
    private int leftPersentage;
    private int rightPersentage;
    private float xValue, yValue;
    private TextView kms, gameover;
    private ImageView car, rock, rock2, rock3, hp, hp2, hp3, play, instructions;
    private RelativeLayout background;
    private Handler handle;
    private Handler handler;
    private boolean pushingDown = false;
    private Runnable repeater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        car = findViewById(R.id.car);
        rock = findViewById(R.id.rock);
        rock2 = findViewById(R.id.rock2);
        rock3 = findViewById(R.id.rock3);
        kms = findViewById(R.id.kms);
        hp = findViewById(R.id.hp);
        hp2 = findViewById(R.id.hp2);
        hp3 = findViewById(R.id.hp3);
        play = findViewById(R.id.play);
        gameover = findViewById(R.id.gameover);
        instructions = findViewById(R.id.instructions);
        background = (RelativeLayout) findViewById(R.id.background);


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

        leftPersentage = (width) * 20 / 100;
        rightPersentage = 1 - (width) * 20 / 100;

        handler = new Handler();

        rock.setY(-height);
        rock2.setY(-height);
        rock3.setY(-height);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(start == 0)
                {
                    instructions.setVisibility(View.VISIBLE);
                }
                else
                {
                    Play();
                }
            }
        });

        instructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instructions.setVisibility(View.INVISIBLE);
                Play();
            }
        });

        gameover.setVisibility(View.INVISIBLE);
        instructions.setVisibility(View.INVISIBLE);


        HandleCar();

        MainTimer();

    }

    private void Play()
    {
        gameover.setVisibility(View.INVISIBLE);
        play.setVisibility(View.INVISIBLE);
        play.setImageResource(R.drawable.replay);
        Restart();
    }


    private void Restart()
    {
        rock.setY(-height);
        rock2.setY(-height);
        rock3.setY(-height);
        distance = 0;
        health = 3;
        start = 1;
        hp.setVisibility(View.VISIBLE);
        hp2.setVisibility(View.VISIBLE);
        hp3.setVisibility(View.VISIBLE);
    }


    private void MainTimer()
    {
        Timer mainTimer = new Timer();
        mainTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(start > 0) {
                            distance += speed/10; //In order to create a realistic speed number, I divide it.
                            kms.setText(String.valueOf(distance));
                            Rock();
                        }
                        if(start == 1) {
                            ColDetect();
                            delay++;
                        }
                    }
                });
            }

        }, 0, 10);
    }


    private void ColDetect()
    {
        if(Collision(car, rock) || Collision(car, rock2) || Collision(car, rock3))
        {
            rock.setY(-height);
            rock2.setY(-height);
            rock3.setY(-height);
            //I added a delay, so a rock wont hit the player more than once.
            if(delay > 10) {
                CarBlink();
                Toast toast = Toast.makeText(this, "Yo've hit a rock!", Toast.LENGTH_SHORT);
                toast.show();
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(400);
                SpawnRock(rock, 100);
                SpawnRock(rock2, 400);
                SpawnRock(rock3, 700);
                LowerHealth();
                delay = 0;
            }
        }
    }

    private void LowerHealth()
    {
        health --;
        if(health < 1)
        {
            hp.setVisibility(View.INVISIBLE);
            GameOver();
        }
        else if(health == 2)
        {
            hp3.setVisibility(View.INVISIBLE);
        }
        else if(health == 1)
        {
            hp2.setVisibility(View.INVISIBLE);
        }
    }

    private void GameOver()
    {
        start = 2;
        gameover.setVisibility(View.VISIBLE);
        play.setVisibility(View.VISIBLE);
    }

    private void CarBlink()
    {
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(100);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(5);
        animation.setRepeatMode(Animation.REVERSE);
        car.startAnimation(animation);
    }

    private void Rock()
    {
        rock.setY(rock.getY() + speed);
        rock2.setY(rock2.getY() + speed);
        rock3.setY(rock3.getY() + speed);
        int loc[] = new int[2];
        int loc2[] = new int[2];
        int loc3[] = new int[2];
        rock.getLocationOnScreen(loc);
        rock2.getLocationOnScreen(loc2);
        rock3.getLocationOnScreen(loc3);

        if (loc[1] > height+(height/10)) {
            SpawnRock(rock, 100);
        }
        if (loc2[1] > height+(height/10)) {
            SpawnRock(rock2, 400);
        }
        if (loc3[1] > height+(height/10)) {
            SpawnRock(rock3, 700);
        }
    }

    private void SpawnRock(ImageView theRock, int delay)
    {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                theRock.setY(-height);
                int random = new Random().nextInt(((width-(width/5)) - width/5) + 1) + width/5;
                theRock.setX(random);
            }
        }, delay);

    }

    private void HandleCar()
    {
        repeater = new Runnable() {
            @Override
            public void run() {
                if (pushingDown && start == 1) {
                    handler.postDelayed(this, 10);
                    if (xValue <= leftPersentage) {
                        if (outline != 1) {
                            car.setX(car.getX() - width / 60);
                        }
                    } else if (xValue >= rightPersentage) {
                        if (outline != 2) {
                            car.setX(car.getX() + width / 60);
                        }
                    }


                    int loc[] = new int[2];
                    car.getLocationOnScreen(loc);

                    if (loc[0] < width / 35) {
                        outline = 1;
                    } else if (loc[0] + car.getWidth() > width - (width / 30)) {
                        outline = 2;
                    } else {
                        outline = 0;
                    }
                }
            }
        };
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            pushingDown = true;
            handler.post(repeater);
            xValue = event.getX();
            yValue = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            pushingDown = false;
        }

        return super.onTouchEvent(event);
    }

    private boolean Collision(ImageView Object, ImageView Object2)
    {
        Rect BallRect = new Rect();
        Object.getHitRect(BallRect);
        Rect NetRect = new Rect();
        Object2.getHitRect(NetRect);
        return BallRect.intersect(NetRect);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}