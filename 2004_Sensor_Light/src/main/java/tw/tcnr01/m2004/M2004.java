package tw.tcnr01.m2004;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class M2004 extends AppCompatActivity {

    private TextView tv1;
    private ImageView imageView1;
    private SensorManager sensor_manager;
    private MySensorEventListener listener; //subclass
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private CameraManager mCameraManager;
    private String mCameraId;
    int onoffswitch = 0;

    private int flash_type = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m2004);
        setupViewComponent();
    }

    private void setupViewComponent() {
        sensor_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        tv1 = (TextView) findViewById(R.id.msg1);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView1.setVisibility(View.INVISIBLE);

//----------------------------------------
        // Light傳感器
        Sensor sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_LIGHT);
        listener = new MySensorEventListener();
        sensor_manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        //設定相機管理物件，處理閃光燈
//         camera = Camera.open();
//         parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
//         parameters = camera.getParameters();
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

    }

    //閃光燈打開
    private void turn_on_flash() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //閃光燈關閉
    private void turn_off_flash() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        sensor_manager.unregisterListener(listener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        turn_off_flash();

    }

    //******************************************menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item01:
                flash_type = 1;
                break;
            case R.id.item02:
                flash_type = 2;
                break;
            case R.id.main_finish:
//                turn_off_flash(); //關閉閃燈
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    //***************************************************************subclass

    // 感應器事件監聽器---sub class
    private class MySensorEventListener implements SensorEventListener {

        // 監控感應器改變
        @Override
        public void onSensorChanged(SensorEvent event) {
            StringBuilder sb = new StringBuilder();
            sb.append("偵測器sensor: " + event.sensor.getName() + "\n");
            // Android 的 Light Sensor 照度偵測內容只有 values[0] 有意義!
            final float lux = event.values[0];
            sb.append("流明照度值: " + lux + " Lux\n");
            //------------
            //long timeInMillis = (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;
            long timeInMillis = (new Date()).getTime();
            String gettime = formatter.format(timeInMillis);
            //------------
            sb.append("timestamp時間紀錄: " + gettime);
//            sb.append("timestamp : " + event.timestamp + " ns");
            //event.timesamp表示當前的時間，單位是納秒(1百萬分之一毫秒)

            final String msg = sb.toString();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv1.setText(msg);
                    if (lux <= 20) {
                        tv1.setTextColor(Color.RED);
                        imageView1.setVisibility(View.VISIBLE);
                        //
                        try {
                            mCameraId = mCameraManager.getCameraIdList()[0];
                            //---------------------
                            switch (flash_type) {
                                case 1: //閃爍模式
                                    if (onoffswitch == 0) {
                                        turn_on_flash();
                                        //Thread.sleep(1000);
                                        onoffswitch = 1;

                                    } else {
                                        turn_off_flash();
                                       // Thread.sleep(1000);
                                        onoffswitch = 0;
                                    }
                                case 2: //照明模式
                                    turn_on_flash();
                                    break;
                            }
                            //-----------------------
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        imageView1.setVisibility(View.INVISIBLE);
                        tv1.setTextColor(Color.YELLOW);
                        //關閉閃燈
                        try {
                            mCameraId = mCameraManager.getCameraIdList()[0];
                            turn_off_flash();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        // 對感應器精度的改變做出回應
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }



}
