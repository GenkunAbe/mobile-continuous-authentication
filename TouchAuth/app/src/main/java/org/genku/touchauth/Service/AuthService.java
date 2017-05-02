package org.genku.touchauth.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import org.genku.touchauth.Util.FileUtils;

public class AuthService extends Service {

    public static final int INTERVAL = 4;

    private static final String authResultFilename = Environment.getExternalStorageDirectory().getAbsolutePath() + "Auth/AuthResult.txt";


    public AuthService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            stopService(new Intent(this, TouchDataCollectingService.class));
            stopService(new Intent(this, SensorDataCollectingService.class));
            stopService(new Intent(this, TouchPredictingService.class));
            stopService(new Intent(this, SensorPredictingService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        startService(new Intent(this, TouchPredictingService.class));
        startService(new Intent(this, SensorPredictingService.class));

        new Thread(new Runnable() {
            @Override
            public void run() {
                predict();
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void predict() {
        Long start = System.currentTimeMillis();
        try {
            while (true) {
                Long now = System.currentTimeMillis();
                if (now - start < 4 * 1000) continue;

                double touchConfidence = TouchPredictingService.confidence;
                double sensorConfidence = SensorPredictingService.confidence;
                if (touchConfidence > 2 && sensorConfidence > 0) {
                    Toast.makeText(getApplicationContext(), "True", Toast.LENGTH_SHORT).show();
                    FileUtils.writeFile(authResultFilename, "True\r\n", true);
                } else {
                    Toast.makeText(getApplicationContext(), "False", Toast.LENGTH_SHORT).show();
                    FileUtils.writeFile(authResultFilename, "False\r\n", true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
