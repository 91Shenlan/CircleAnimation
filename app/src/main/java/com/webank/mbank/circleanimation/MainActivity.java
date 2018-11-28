package com.webank.mbank.circleanimation;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.webank.mbank.circle.CircleView;
import com.webank.mbank.circle.WeBankLogger;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取屏幕的宽高
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        final int width = point.x;
        final int height = point.y;


        final CircleView circleView = findViewById(R.id.circle_view);
        final Button startAnimation = findViewById(R.id.animation_start);
        startAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int random = (int) ((Math.random() + 1) * 50);
                if (random % 2 == 0) {
                    WeBankLogger.d(TAG,"random % 2 == 0");
                    WeBankLogger.d(TAG,"传进来的radius="+(256 + random-30));
                    circleView.setCircleColor(CircleView.CircleColor.WHITE);
                    circleView.setCircleAndDraw((float) (width * 1.5 / 3 + random-70), height / 3 + random, 256 + random-30);

                } else{
                    WeBankLogger.d(TAG,"random % 2 != 0");
                    WeBankLogger.d(TAG,"传进来的radius="+(256 + random-20));
                    circleView.setCircleColor(CircleView.CircleColor.RED);
                    circleView.setCircleAndDraw((float) (width * 1.5 / 3 + random-70), height / 3 + random, 256 + random-20);

                }

            }

        });


        Button stopAnimation = findViewById(R.id.animation_stop);
        stopAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleView.stopAnimation();
            }
        });


    }


}
