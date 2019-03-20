package tw.tcnr01.m2003;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class M2003 extends AppCompatActivity {

    private TextView tv1;
    private ImageView imageView1;
    private Vibrator vib;
    private SensorManager sensor_manager;
    private MySensorEventListener listener;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m2003);
        setupViewComponent();
    }

    private void setupViewComponent() {
        sensor_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        tv1 = (TextView) findViewById(R.id.msg1);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        vib = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);//取得震動馬達

        // 接近傳感器
        Sensor sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        listener = new MySensorEventListener();
        sensor_manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);


    }

    @Override
    public void onPause() {
        super.onPause();
        sensor_manager.unregisterListener(listener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //------------------------------------------
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //**********************************************inner class*****************

    // 感應器事件監聽器
    private class MySensorEventListener implements SensorEventListener {

        // 感應器有了改變的回呼函示
        @Override
        public void onSensorChanged(SensorEvent event) {
            StringBuilder sb = new StringBuilder();
            sb.append("sensor感應器: " + event.sensor.getName() + "\n");
            // Android 的 Proximity Sensor 接近偵測是屬於所謂 Binary Sensor 所以並不會有實際測量距離值的回餽
            // 只能測得 0.0 接近, 5.0 遠離
            // 故 Binary Sensor 其精確度一律返回 SensorManager.SENSOR_STATUS_UNRELIABLE
            sb.append("accuracy精確度: " + getAccuracyName(event.accuracy) + "\n");
            // Android 的 Proximity Sensor 接近偵測內容只有 values[0] 有意義!
            final float proxValue = event.values[0];
            sb.append("values距離: " + proxValue + " cm\n"); // 0.0:靠近, 5.0:離開
            //------------
           // long timeInMillis = (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;
            long timeInMillis = (new Date()).getTime();
            String gettime = formatter.format(timeInMillis);
            //------------
            sb.append("timestamp時間紀錄: " + gettime);
            //			sb.append("timestamp時間紀錄: " + event.timestamp + " ns");
            final String msg = sb.toString();

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (proxValue < 1) {
                        vib.vibrate(3000);
                        tv1.setText(msg + "\n" + "靠太近了啦 !");
                        imageView1.setImageResource(R.drawable.p2);
                    } else {
                        vib.cancel();
                        tv1.setText(msg + "\n");
                        imageView1.setImageResource(R.drawable.p1);
                    }
                }

            });
        }

        // 對感應器精度的改變做出回應的回呼函示
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {


        }

        // 取得 accuracy 對應的訊息內容
        private String getAccuracyName(int accuracy) {
            String name = "";
            switch (accuracy) {
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    name = "LOW";
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    name = "MEDIUM";
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    name = "HIGH";
                    break;
                case SensorManager.SENSOR_STATUS_UNRELIABLE:
                    name = "UNRELIABLE";
                    break;
                default:
                    name = "Non";
            }
            return name;
        }

    }
}
