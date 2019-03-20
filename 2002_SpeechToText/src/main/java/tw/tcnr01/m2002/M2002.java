package tw.tcnr01.m2002;

import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class M2002 extends AppCompatActivity {

    private M2002 context;
    private GridView gridView;
    private Intent recognizerIntent;
    private ArrayList<Object> messageList;
    private ArrayAdapter<Object> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m2002);
        setupViewComponent();
    }

    private void setupViewComponent() {
        context = this;
        gridView = (GridView) findViewById(R.id.gridView);

        //-------- 語音辨識檢查服務是否存在----------
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        if (!hasRecognizer()) {
            Toast.makeText(context, "無語音辨識服務", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //------------------------------------------
        messageList = new ArrayList<>();
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, messageList);
        gridView.setNumColumns(3);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new MyOnItemClickListener());

    }

    //--------啟動Intent 語音辨識----------
    private boolean hasRecognizer() {
        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(recognizerIntent,
                PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() != 0) { //檢查該Intent 是否有註冊
            return true;
        } else {
            return false;
        }
    }

    public void btn_b001_onClick(View view) {
/*--------------
RecognizerIntent.EXTRA_LANGUAGE_MODEL根據所選語音模型識別
RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
設定辨識語系如RecognizerIntent.EXTRA_LANGUAGE,Locale.ENGLISH.toString()
是以英文固定辨識

RecognizerIntent.EXTRA_PROMPT提示文字
RecognizerIntent.EXTRA_MAX_RESULTS最多答案筆數
RecognizerIntent.EXTRA_RESULTS辨識結果
--------------*/

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "請說...");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        startActivityForResult(recognizerIntent, 1);//啟動語音辨識服務 Intent
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent it) {
        messageList.clear();
        if (requestCode != 1) {
            return;
        }
        if (resultCode != RESULT_OK) {
            return;
        }
        List<String> list = it.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        //---語音辨識結果列表
        for (String s : list) {
            messageList.add(s);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String keyword = parent.getItemAtPosition(position).toString();

            Intent web = new Intent(Intent.ACTION_WEB_SEARCH);
            web.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //-- 帶入參數到URL
            web.putExtra(SearchManager.QUERY, keyword);
            startActivity(web);

            Toast.makeText(context, keyword, Toast.LENGTH_SHORT).show();
        }
    }




}
