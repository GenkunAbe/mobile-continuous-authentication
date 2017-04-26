package org.genku.touchauth.Activity;

/**
 * Created by genku on 4/1/2017.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.genku.touchauth.Model.SVM;
import org.genku.touchauth.R;
import org.genku.touchauth.Service.SensorDataCollectingService;
import org.genku.touchauth.Service.TouchDataCollectingService;
import org.genku.touchauth.Service.TouchPredictService;
import org.genku.touchauth.Util.DataUtils;
import org.genku.touchauth.Util.TextFile;

public class MainActivity extends AppCompatActivity {

    public static SVM clickModel;
    public static SVM slideModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        TextFile.makeRootDirectory(dir + "/Auth/");
        TextFile.makeRootDirectory(dir + "/Auth/Touch/");
        TextFile.makeRootDirectory(dir + "/Auth/Sensor/");


        isGrantExternalRW(this);
    }

    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return false;
    }

    public void onCollectStartButtonClick(View view) {

        try {
            stopService(new Intent(this, TouchPredictService.class));
            stopService(new Intent(this, SensorDataCollectingService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Collecting Start...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setClass(this, TouchDataCollectingService.class);
        startService(intent);

        Intent intent1 = new Intent();
        intent1.setClass(this, SensorDataCollectingService.class);
        startService(intent1);
    }

    public void onTrainStartButtonClick(View view) {

        Toast.makeText(this, "Training Start...", Toast.LENGTH_SHORT).show();

        String dir  = Environment.getExternalStorageDirectory().getAbsolutePath();
        double[][] clickFeatures = TextFile.readFileToMatrix(dir + "/click_features.txt");
        double[][] slideFeatures = TextFile.readFileToMatrix(dir + "/slide_features.txt");

        double[] clickLabel = new double[clickFeatures.length];
        for (int i = 0; i < clickLabel.length; ++i) {
            clickLabel[i] = 1;
        }

        double[] slideLabel = new double[slideFeatures.length];
        for (int i = 0; i < slideLabel.length; ++i) {
            slideLabel[i] = 1;
        }

        clickFeatures = DataUtils.cleanData(clickFeatures, true);
        clickFeatures = DataUtils.scaleData(clickFeatures, dir + "/click_coefs.txt", false);

        slideFeatures = DataUtils.cleanData(slideFeatures, true);
        slideFeatures = DataUtils.scaleData(slideFeatures, dir + "/slide_coefs.txt", false);


        clickModel = new SVM();
        clickModel.train(clickFeatures, clickLabel);

        slideModel = new SVM();
        slideModel.train(slideFeatures, slideLabel);
        Toast.makeText(this, "Training End!", Toast.LENGTH_SHORT).show();
    }

    public void onTestStartButtonClick(View view) {

        try {
            stopService(new Intent(this, TouchDataCollectingService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(this, " Start...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setClass(this, TouchPredictService.class);
        startService(intent);
    }

    public void onStopButtonClick(View view) {

        // Kill the previous getevent process
        try {
            String[] cmd = {
                    "/system/bin/sh",
                    "-c",
                    "ps | grep getevent | awk \'{print $2}\' | xargs su am kill"
            };
            Process p = Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            stopService(new Intent(this, TouchDataCollectingService.class));
            stopService(new Intent(this, TouchPredictService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
