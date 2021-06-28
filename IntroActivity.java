package kr.co.biomed.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import kr.co.biomed.R;

public class IntroActivity extends AppCompatActivity {
    ImageView logoImage;
    Drawable alpha;
    boolean isFinish;
    Context mContext;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                alpha.setAlpha(msg.arg1);
            }else if(msg.what == 1){
                alpha.setAlpha(255);
            }
        }
    };

    final String TAG = "INTRO_ACTIVITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        getID();
        setLogoImage();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent main = new Intent(mContext, MainActivity.class);
                mContext.startActivity(main);
                isFinish = true;

                finish();
            }
        }, 5000);
    }

    private void setLogoImage() {
        Thread t = new Thread(){
            @Override
            public void run() {
                int alpha = 0;
                while(true) {
                    if (isFinish) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                        return;
                    }else{
                        try {
                            sleep(5);
                            Message msg = mHandler.obtainMessage();
                            msg.what = 0 ;
                            if(alpha < 255) {
                                msg.arg1 = alpha++;
                            }else if (alpha < 510){
                                msg.arg1 = 255 - alpha++%255;
                            }else {
                                msg.arg1 = 0;
                                alpha = 0;
                            }
                            mHandler.sendMessage(msg);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        t.start();
    }

    private void getID() {
        logoImage = findViewById(R.id.logo);
        alpha = logoImage.getDrawable();
        isFinish = false;
        mContext = this;
    }
}
