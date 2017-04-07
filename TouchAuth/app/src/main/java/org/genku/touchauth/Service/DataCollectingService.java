package org.genku.touchauth.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.genku.touchauth.Model.FeatureExtraction;
import org.genku.touchauth.Model.TouchEvent;
import org.genku.touchauth.Util.TextFile;

import java.io.InputStream;

public class DataCollectingService extends Service {

    public DataCollectingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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

        // Get External Storage Directory & the filename of raw data and features
        final String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        final String rawFilename = dir + "/touch.txt";
        final String clickFeatureFilename = dir + "/click_features.txt";
        final String slideFeatureFilename = dir + "/slide_features.txt";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] cmd = {"su", "-c", "getevent -t /dev/input/event5"};
                    Process p = Runtime.getRuntime().exec(cmd);
                    InputStream is = p.getInputStream();
                    StringBuilder sb = new StringBuilder();
                    while(true) {
                        byte[] b = new byte[4096];
                        int n = is.read(b);
                        if (n == -1) break;
                        sb.append(new String(b, 0, n));
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
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
