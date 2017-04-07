package org.genku.touchauth;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.genku.touchauth.Model.TouchEvent;
import org.genku.touchauth.Service.DataCollectingService;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isGrantExternalRW(this)) {
            return;
        }

        final String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        final String filename = dir + "/touch.txt";
        TextView tv = (TextView) findViewById(R.id.tv);
        tv.setText(filename);

        Intent intent = new Intent();
        intent.setClass(this, DataCollectingService.class);
        startService(intent);


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
}
