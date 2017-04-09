package org.genku.touchauth.Service;

/**
 * Created by genku on 4/1/2017.
 */

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.genku.touchauth.Model.FeatureExtraction;
import org.genku.touchauth.Model.PostEventMethod;
import org.genku.touchauth.Model.TouchEvent;
import org.genku.touchauth.Util.TextFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

public class DataCollectingService extends Service {

    // Get External Storage Directory & the filename of raw data and features
    final String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
    final String rawFilename = dir + "/touch.txt";
    final String clickFeatureFilename = dir + "/click_features.txt";
    final String slideFeatureFilename = dir + "/slide_features.txt";

    public DataCollectingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                collect(new PostEventMethod(){
                    @Override
                    public Void call() throws Exception {
                        double[] features = FeatureExtraction.extract(this.event);
                        String raw = this.sb.substring(this.event.start, this.event.end);
                        TextFile.writeFile(rawFilename, raw, true);
                        if (features.length < 5) {
                            TextFile.writeFileFromNums(clickFeatureFilename, features, true, false, 1);
                        }
                        else {
                            TextFile.writeFileFromNums(slideFeatureFilename, features, true, false, 1);
                        }
                        return null;
                    }
                });

            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    public static void collect(PostEventMethod postEvent) {
        try {
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


            // Start Collection
            String[] cmd = {"su", "-c", "getevent -t /dev/input/event5"};
            Process p = Runtime.getRuntime().exec(cmd);
            InputStream is = p.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            int i = 0;
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                sb.append(line).append("\n");
                ++i;
                if (i >= 10) {

                    TouchEvent event = TouchEvent.getTouchEventFromString(sb.toString());
                    if (event != null) {

                        postEvent.setParam(event, sb);
                        postEvent.call();

                        sb.delete(event.start, event.end);
                        i -= 10;

                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            collect(postEvent);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
