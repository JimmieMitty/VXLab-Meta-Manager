package com.example.rmitmanagerjava;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

public class MainActivity extends AppCompatActivity {

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
}
