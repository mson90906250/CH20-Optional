package tw.tcnr01.m2005;

import android.graphics.drawable.Drawable;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class M2005 extends AppCompatActivity implements View.OnClickListener {

    private M2005 context;
    private EditText editText1;
    private Button talkBtn;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m2005);
        setupViewComponent();
        Top_img(false);

    }

    private void setupViewComponent() {
        //--------------------------------------------
        context = this;
        editText1 = (EditText) findViewById(R.id.editText1);
        talkBtn = (Button) findViewById(R.id.talkBtn);
        editText1.setText(getText(R.string.m2005_e001));
        //------edittext  -----
        editText1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                tts.stop();
                Top_img(false);
                return false;
            }
        });;
        //--------------------------------------------
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // 設定語系
                    //setLanguage：設置語言。
                    //英語為Locale.ENGEN;
                    //法語為Locale.FRENCH;
                    //德語為Locale.GERMAN;
                    //意大利語為Locale.ITALIAN;
                    //漢語普通話為Locale.CHINA（需安裝中文引擎，如科大訊飛+）
                    int result = tts.setLanguage(Locale.CHINESE);

                    //setPitch：設置音調.1.0正常音調;低於1.0的為低音;高於1.0的為高音
                    tts.setPitch(0.5f); // 設定語音間距

                    //setSpeechRate：設置語速.1.0正常語速; 0.5慢一半的語速; 2.0;快一倍的語速
                    tts.setSpeechRate(1.5f); // 設定語音速率
                    //------------------------------------------
                    //speak : 開始對指定文本進行語音朗讀。
                    //synthesizeToFile : 把指定文本的朗讀語音輸出到文件。
                    //stop : 停止朗讀。
                    //shutdown : 關閉語音引擎。
                    //isSpeaking : 判斷是否在語音朗讀。
                    //getLanguage : 獲取當前的語言。
                    //getCurrentEngine : 獲取當前的語音引擎。
                    //getEngines : 獲取系統支持的所有語音引擎。
                    //------------------------------------------
                    // 確認手機所設定的TTS引擎是否支援該語系的語音
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context, getString(R.string.msg1),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, getString(R.string.msg2), Toast.LENGTH_SHORT).show();
                        talkBtn.setEnabled(true);
//                        Drawable top = getResources().getDrawable(R.drawable.speaker_on);
//                        talkBtn.setCompoundDrawablesWithIntrinsicBounds(
//                                null, top, null, null);
                        Top_img(false);
                    }
                } else {
                    Top_img(false);
                    Toast.makeText(context, getString(R.string.msg3),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void Top_img(boolean b_chk) {
        Drawable top;
        if (b_chk == true) {
            top = getResources().getDrawable(R.drawable.speaker_on);
        } else {
            top = getResources().getDrawable(R.drawable.speaker_off);
        }
        talkBtn.setCompoundDrawablesWithIntrinsicBounds(
                null, top, null, null);
    }

    @Override
    public void onClick(View v) {
        String talkString = editText1.getText().toString();
//        tts.speak(CharSequence text,   int queueMode,  android.os.Bundle params,   String utteranceId);
        tts.speak(talkString, TextToSpeech.QUEUE_FLUSH, null);
        Top_img(true);
    }


    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (tts != null) {
            //stop : 停止朗讀。
            //shutdown : 關閉語音引擎。
            tts.stop();
            tts.shutdown();
            Top_img(false);
        }
        super.onDestroy();
        Toast.makeText(context, getString(R.string.msg4), Toast.LENGTH_SHORT).show();
    }

    //====================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String talkString = "";
        switch (item.getItemId()) {
            case R.id.item01:
                tts.stop();
                talkString = getString(R.string.msg5);
                //speak : 開始對指定文本進行語音朗讀。
                editText1.setText(talkString);
                tts.stop();
                tts.speak(talkString, TextToSpeech.QUEUE_FLUSH, null);
                Top_img(true);
                break;
            case R.id.item02:
                tts.stop();
                talkString = getString(R.string.msg6);
                editText1.setText(talkString);
                tts.speak(talkString, TextToSpeech.QUEUE_FLUSH, null);
                Top_img(true);
                break;
            case R.id.item03:
                tts.stop();
                editText1.setText("");
                Top_img(false);
                break;

            case R.id.action_settings:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
