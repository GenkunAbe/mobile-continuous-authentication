package org.genku.touchauth.Service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.IntDef;

import org.genku.touchauth.Util.TextFile;
import org.w3c.dom.DOMConfiguration;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

public class SensorDataCollectingService extends Service implements SensorEventListener {
    public SensorDataCollectingService() {

    }

    public static double INTERVAL = 10;
    public static double WINDOW_INTERVAL = 2;

    public final String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Auth/Sensor/";
    public final String accDir = dir + "Acc/";
    public final String oriDir = dir + "Ori/";
    public final String magDir = dir + "Mag/";
    public final String gyrDir = dir + "Gyr/";
    public String accFilenameNow;
    public String oriFilenameNow;
    public String magFilenameNow;
    public String gyrFilenameNow;


    private double[] gravity = {0, 0, 9.8};

    private List<List<Double>> accRawData = new ArrayList<>();
    private List<List<Double>> oriRawData = new ArrayList<>();
    private List<List<Double>> magRawData = new ArrayList<>();
    private List<List<Double>> gyrRawData = new ArrayList<>();

    private List<List<Double>> accTempData = new ArrayList<>();
    private List<List<Double>> oriTempData = new ArrayList<>();
    private List<List<Double>> magTempData = new ArrayList<>();
    private List<List<Double>> gyrTempData = new ArrayList<>();

    private int groupCount = 0;



    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {

        TextFile.makeRootDirectory(accDir);
        TextFile.makeRootDirectory(oriDir);
        TextFile.makeRootDirectory(magDir);
        TextFile.makeRootDirectory(gyrDir);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Long startTime = System.currentTimeMillis();
                try {
                    while (true) {
                        Long currentTime = System.currentTimeMillis();
                        if (currentTime - startTime > INTERVAL * 1000) {
                            startTime = currentTime;
                            List<List<Double>> accData = accRawData;
                            accRawData = new ArrayList<>();
                            ++groupCount;
                            if (groupCount < 4) {
                                accTempData.addAll(accData);
                            }
                            else {
                                groupCount = 0;



                                accTempData = new ArrayList<>();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    public static void collect() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Long currentTime = event.timestamp;
        List<Double> data = new ArrayList<>();
        data.add(currentTime + .0);
        data.add(event.values[0] + .0);
        data.add(event.values[1] + .0);
        data.add(event.values[2] + .0);

        try {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    String a, b, c, s;
                    final double alpha = 0.8;
                    List<Double> linear_acceleration = new ArrayList<>();
                    gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                    gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                    gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
                    linear_acceleration.add(currentTime + .0);
                    linear_acceleration.add(event.values[0] - gravity[0]);
                    linear_acceleration.add(event.values[1] - gravity[1]);
                    linear_acceleration.add(event.values[2] - gravity[2]);
                    accRawData.add(linear_acceleration);
                    break;
                case Sensor.TYPE_ORIENTATION:
                    oriRawData.add(data);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magRawData.add(data);
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    gyrRawData.add(data);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
