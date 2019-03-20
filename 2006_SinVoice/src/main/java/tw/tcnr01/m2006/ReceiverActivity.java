package tw.tcnr01.m2006;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tw.tcnr01.m2006.sinvoice.LogHelper;
import tw.tcnr01.m2006.sinvoice.SinVoiceRecognition;

public class ReceiverActivity extends AppCompatActivity
        implements SinVoiceRecognition.Listener {

    private final static String TAG = "tcnr01=>";

    // 設定顯示辨識內容常數
    private final static int MSG_SET_RECG_TEXT = 1;
    // 設定辨識進行常數
    private final static int MSG_RECG_START = 2;
    // 設定結束辨識常數
    private final static int MSG_RECG_END = 3;

    private final static String CODEBOOK = "12345";

    private Handler mHanlder;
    private SinVoiceRecognition mRecognition;

    private static ImageView imageView1;
    private static HashMap<String, Integer> map;
    private Context context;
    //    ====================
    //所需要申請的權限數組
    private static final String[] permissionsArray = new String[]{
            Manifest.permission.RECORD_AUDIO };

    private List<String> permissionsList = new ArrayList<String>();

    //申請權限後的返回碼
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m2006);
        checkRequiredPermission(this);
        context = this;

        map = new HashMap<String, Integer>();
        map.put("123", R.drawable.line1);
        map.put("234", R.drawable.line2);
        map.put("345", R.drawable.line3);
        map.put("456", R.drawable.line4);

        setTitle("聲音控制-接收端");

        mRecognition = new SinVoiceRecognition(CODEBOOK);
        mRecognition.setListener(this);

        imageView1 = (ImageView) findViewById(R.id.imageView1);
        TextView recognisedTextView = (TextView) findViewById(R.id.regtext);
        mHanlder = new RegHandler(recognisedTextView);

        ToggleButton toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);

        toggleButton1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(context, isChecked + "", Toast.LENGTH_SHORT).show();
                if (isChecked) {
                    mRecognition.start();
                } else {
                    mRecognition.stop();
                }
            }

        });

    }

    private void checkRequiredPermission(final Activity activity){
        for (String permission : permissionsArray) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
        if (permissionsList.size()!=0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new
                    String[permissionsList.size()]), REQUEST_CODE_ASK_PERMISSIONS);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int i=0; i<permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), permissions[i]+"權限申請成功!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "權限被拒絕： "+permissions[i], Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private static class RegHandler extends Handler {
        private TextView mRecognisedTextView;
        private static StringBuilder mTextBuilder = new StringBuilder();
        public RegHandler(TextView textView) {
            mRecognisedTextView = textView;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 顯示辨識內容
                case MSG_SET_RECG_TEXT:
                    char ch = (char) msg.arg1;
                    mTextBuilder.append(ch);
                    if (null != mRecognisedTextView) {
                        mRecognisedTextView.setText(mTextBuilder.toString());
                    }
                    break;
                // 清除辨識字串內容
                case MSG_RECG_START:
                    mTextBuilder.delete(0, mTextBuilder.length());
                    break;
                // 結束辨識
                case MSG_RECG_END:
                    // 得到最新編碼key。
                    String key = mTextBuilder.toString();
                    Log.d(TAG,""+key);
                    // 將key對應到map中。
                    final Integer value = map.get(key);
                    Log.d(TAG,""+value);
                    if (value != null) {
                        imageView1.setImageResource(value);
                    }
                    LogHelper.d(TAG, "recognition end");
                    break;
            }
            super.handleMessage(msg);
        }
    }

    // 辨識開始
    @Override
    public void onRecognitionStart() {
        mHanlder.sendEmptyMessage(MSG_RECG_START);
    }

    // 辨識進行中
    @Override
    public void onRecognition(char ch) {
        mHanlder.sendMessage(mHanlder.obtainMessage(MSG_SET_RECG_TEXT, ch, 0));
    }

    // 辨識完成
    @Override
    public void onRecognitionEnd() {

        mHanlder.sendEmptyMessage(MSG_RECG_END);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent();
        i.setClass(this, SendActivity.class);
        startActivity(i);
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("傳送端");
        return super.onCreateOptionsMenu(menu);
    }
}
