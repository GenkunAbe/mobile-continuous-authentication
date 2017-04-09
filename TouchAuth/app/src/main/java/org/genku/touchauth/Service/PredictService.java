package org.genku.touchauth.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.IntDef;

import org.genku.touchauth.MainActivity;
import org.genku.touchauth.Model.FeatureExtraction;
import org.genku.touchauth.Model.PostEventMethod;
import org.genku.touchauth.R;
import org.genku.touchauth.Util.TextFile;

public class PredictService extends Service {

    // Get External Storage Directory & the filename of raw data and features
    final String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
    final String clickFeatureFilename = dir + "/click_test_features.txt";
    final String slideFeatureFilename = dir + "/slide_test_features.txt";
    final String clickResultFilename = dir + "/click_result_features.txt";
    final String slideResultFilename = dir + "/slide_result_features.txt";


    public PredictService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        improvePriority();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                predict();
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    private void improvePriority() {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Touch Auth")
                .setContentText("Predicting Service Started.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        notification.contentIntent = contentIntent;
        startForeground(1, notification);
    }

    private void predict() {

        DataCollectingService.collect(new PostEventMethod(){

            private int slideNum = 0;
            private int clickNum = 0;
            private double[][] slideFeatures = new double[10][16];
            private double[][] clickFeatures = new double[10][2];

            @Override
            public Void call() throws Exception {

                double[] feature = FeatureExtraction.extract(event);
                if (feature.length < 5) {
                    clickFeatures[clickNum] = feature;
                    TextFile.writeFileFromNums(clickFeatureFilename, clickFeatures[clickNum++], true, false, 1);
                    if (clickNum >= 9) {
                        boolean ans = getPredictResult(clickFeatures, true);
                        TextFile.writeFile(clickResultFilename, ans + "\n", true);
                        clickFeatures = new double[10][2];
                        clickNum = 0;
                    }
                }
                else {
                    slideFeatures[slideNum] = feature;
                    TextFile.writeFileFromNums(slideFeatureFilename, slideFeatures[slideNum++], true, false, 1);
                    if (slideNum >= 9) {
                        boolean ans = getPredictResult(slideFeatures, false);
                        TextFile.writeFile(slideResultFilename, ans + "\n", true);
                        slideFeatures = new double[10][16];
                        slideNum = 0;
                    }
                }
                return null;
            }
        });

    }

    private boolean getPredictResult(double[][] vectors, boolean isClick) {
        double positiveSum = 0, negativeSum = 0;
        for (double[] vector : vectors) {
            double[] ans = isClick
                    ? MainActivity.clickModel.predict(vector)
                    : MainActivity.slideModel.predict(vector);
            positiveSum += ans[0];
            negativeSum += ans[1];
        }
        return positiveSum > negativeSum;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }
}
