package com.example.djdan.cochesgo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by djdan on 07/02/2018.
 */

public class Success extends AppCompatActivity {
    private TextView welcome, success;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.SplashSuccess);
        setContentView(R.layout.activity_success);

        welcome=(TextView)findViewById(R.id.welcome);
        welcome.setText(welcome.getText()+" "+Sesion.getUsername());
        logo=(ImageView) findViewById(R.id.logo);
        success=(TextView)findViewById(R.id.success);

        Animation myAnim= AnimationUtils.loadAnimation(this, R.anim.anim_success);
        welcome.startAnimation(myAnim);
        logo.startAnimation(myAnim);
        success.startAnimation(myAnim);

        final Intent i=new Intent(this, Main.class);
        Thread timer=new Thread(){
            @Override
            public void run() {
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(i);
                    finish();
                }
            }
        };
        timer.start();
    }
}
