package com.example.rmitmanagerjava;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.IOException;


import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//import com.aditya.filebrowser.Constants;
//import com.aditya.filebrowser.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    static MainActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to the XML layout
        setContentView(R.layout.layout);

        // Reference the TextView and Button from the layout
        TextView log = findViewById(R.id.operation_log);
        Button downloadButton = findViewById(R.id.Download);

        // Set an OnClickListener for the button
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                // Change the text when the button is clicked
                log.setText("Downloading Files");
                downloadFile("https://filesampleshub.com/download/document/txt/sample2.txt", "testText.txt", MainActivity.this, Environment.DIRECTORY_DOWNLOADS);
            }
        });
    }

    public void downloadFile(String url, String fileName, Context context, String appDirectory) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("Downloading " + fileName);
        request.setDescription("File is downloading...");

        // Set the destination path
        request.setDestinationInExternalPublicDir(appDirectory, fileName);

        // Make the notification visible in the status bar
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // Start the download
        downloadManager.enqueue(request);
    }

    private boolean installPackage(String apkPath)
            throws IOException {
        String package_name = null;
        PackageInfo packageInfo = getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            package_name = appInfo.packageName;
        }

        if (package_name == null) {
            Toast.makeText(this, "Install Failed : Can't get the package name", Toast.LENGTH_LONG).show();
            return false;
        }

        Toast.makeText(getApplicationContext(), "Start Installation", Toast.LENGTH_SHORT).show();

        PackageInstaller packageInstaller = getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        params.setAppPackageName(package_name);

        // set params
        int sessionId = packageInstaller.createSession(params);
        PackageInstaller.Session session = packageInstaller.openSession(sessionId);

        OutputStream out = session.openWrite(package_name, 0, -1);

        long sizeBytes = 0;
        File file = new File(apkPath);
        if (file.isFile())
            sizeBytes = file.length();

        int total = 0;
        FileInputStream fis = new FileInputStream(apkPath);
        byte[] buffer = new byte[65536];
        int c;
        while ((c = fis.read(buffer)) != -1) {
            total += c;
            out.write(buffer, 0, c);
        }
        session.fsync(out);
        fis.close();
        out.close();

        session.commit(createIntentSender(getApplicationContext(), sessionId));

        return true;
    }

    private static IntentSender createIntentSender(Context context, int sessionId) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                sessionId,
                new Intent(context, InstallReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent.getIntentSender();
    }

    public void onInstallComplete() {
        Toast.makeText(this, "Install Succeed!", Toast.LENGTH_LONG).show();
    }

    public void onInstallFailed() {
        Toast.makeText(this, "Install Failed!", Toast.LENGTH_LONG).show();
    }


}