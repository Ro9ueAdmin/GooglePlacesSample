package com.webonise.download;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.webonise.util.UIUtil;
import com.webonise.util.ValidationUtil;

import java.io.File;

import io.reactivex.functions.Consumer;

public class DownloadHelper {

    public interface Listener {
        void onDownloadCompleted();

        void onDownloadFailed();
    }

    private Context mContext;
    private Listener mDownloadListener;
    private DownloadManager mDownloadManager;
    private long mEnqueue;
    private RxPermissions rxPermissions;


    public DownloadHelper(Context context, RxPermissions rxPermissions) {
        mContext = context.getApplicationContext();
        this.rxPermissions = rxPermissions;
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public void startDownloading(final String downloadUrlPath, final String downloadName) {
        if (ValidationUtil.isStringEmpty(downloadUrlPath)) {
            Toast.makeText(mContext, "Invalid download URL", Toast.LENGTH_LONG).show();
            return;
        }

        rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            registerDownloadCompleteReceiver();

                            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + downloadName;
                            File outputFile = new File(path);
                            if (outputFile.exists()) {
                                outputFile.delete();
                            }

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext.getApplicationContext(), "Download started", Toast.LENGTH_SHORT).show();
                                }
                            });

                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrlPath));
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadName);
                            mEnqueue = mDownloadManager.enqueue(request);

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            Toast.makeText(mContext, "Write Storage permission denied. Please enable from settings", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "Write Storage permission denied.", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(mContext, "Write Storage permission denied.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void unregisterForUpdates() {
        unregisterDownloadCompleterReceiver();
        mDownloadListener = null;
    }

    private BroadcastReceiver mDownloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (mDownloadListener != null) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(mEnqueue);
                    Cursor cursor = mDownloadManager.query(query);
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                            mDownloadListener.onDownloadCompleted();
                        } else if (DownloadManager.STATUS_FAILED == cursor.getInt(columnIndex)){
                            mDownloadListener.onDownloadFailed();
                        }
                    }
                    unregisterDownloadCompleterReceiver();
                }
            }
        }
    };

    private void registerDownloadCompleteReceiver() {
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mContext.registerReceiver(mDownloadCompleteReceiver, intentFilter);
    }

    private void unregisterDownloadCompleterReceiver() {
        try {
            mContext.unregisterReceiver(mDownloadCompleteReceiver);
        } catch (Exception ex) {
            Log.e("DownloadFilesHelper", ex.getMessage());
        }
    }

}
