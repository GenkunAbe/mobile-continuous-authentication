package org.genku.touchauth.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.genku.touchauth.Model.FeatureExtraction;
import org.genku.touchauth.Model.TouchEvent;
import org.genku.touchauth.Util.TextFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
                collect();
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    private void collect() {
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
                        double[] features = FeatureExtraction.extract(event);
                        String raw = sb.substring(event.start, event.end);
                        TextFile.writeFile(rawFilename, raw, true);
                        if (features.length < 5) {
                            TextFile.writeFileFromNums(clickFeatureFilename, features, true);
                        }
                        else {
                            TextFile.writeFileFromNums(slideFeatureFilename, features, true);
                        }

                        sb.delete(event.start, event.end);
                        i -= 10;

                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            collect();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
