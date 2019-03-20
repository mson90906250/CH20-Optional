package tw.tcnr01.m2006;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import tw.tcnr01.m2006.sinvoice.LogHelper;
import tw.tcnr01.m2006.sinvoice.SinVoicePlayer;

public class SendActivity extends AppCompatActivity
        implements SinVoicePlayer.Listener {

    private final static String TAG = "tcnr01=>";
    private final static String CODEBOOK = "12345";
    private SinVoicePlayer mSinVoicePlayer;
    private ImageView imageView1, imageView2, imageView3,
            imageView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send);
        setTitle("聲音控制-發送端");
        mSinVoicePlayer = new SinVoicePlayer(CODEBOOK);
        mSinVoicePlayer.setListener(this);

        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.imageView1:
            case R.id.imageView2:
            case R.id.imageView3:
            case R.id.imageView4:
                String text = "";
                ImageView iv = (ImageView)view;
                iv.setVisibility(View.INVISIBLE);
                switch(view.getId()) {
                    case R.id.imageView1:
                        text = "123";
                        break;
                    case R.id.imageView2:
                        text = "234";
                        break;
                    case R.id.imageView3:
                        text = "345";
                        break;
                    case R.id.imageView4:
                        text = "456";
                        break;
                }
                mSinVoicePlayer.play(text, false, 500);
                mSinVoicePlayer.stop();
                break;
            case R.id.button1:
                imageView1.setVisibility(View.VISIBLE);
                imageView2.setVisibility(View.VISIBLE);
                imageView3.setVisibility(View.VISIBLE);
                imageView4.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onPlayStart() {
        LogHelper.d(TAG, "start play");
    }

    @Override
    public void onPlayEnd() {
        LogHelper.d(TAG, "stop play");
    }
}