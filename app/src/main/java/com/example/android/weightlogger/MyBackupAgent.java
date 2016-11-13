package com.example.android.weightlogger;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.BackupManager;
import android.app.backup.FileBackupHelper;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.IOException;


public class MyBackupAgent extends BackupAgentHelper {
    private static final String LOG_TAG = "BackUp";
    // The name of the SharedPreferences file
    //static final String HIGH_SCORES_FILENAME = "scores";

    // A key to uniquely identify the set of backup data
    private String FILES_BACKUP_KEY = DB.getDatabaseName();

    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {
        FileBackupHelper helper = new FileBackupHelper(this,
                getDatabasePath(DB.getDatabaseName()).getAbsolutePath());
        addHelper(FILES_BACKUP_KEY, helper);
    }

    @Override
    public void onRestoreFile(ParcelFileDescriptor data, long size, File destination, int type, long mode, long mtime) throws IOException {
        super.onRestoreFile(data, size, destination, type, mode, mtime);
        Log.v(LOG_TAG,"onRestoreFile");
    }

    @Override
    public void onRestoreFinished() {
        super.onRestoreFinished();
        Log.v(LOG_TAG,"onRestoreFile");
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        super.onRestore(data, appVersionCode, newState);
        Log.v(LOG_TAG,"onRestoreFile");
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        super.onBackup(oldState, data, newState);
        Log.v(LOG_TAG,"onRestoreFile");
    }

    public static void requestBackup(Context context){
        BackupManager bm = new BackupManager(context);
        bm.dataChanged();
        Log.v(LOG_TAG,"requestBackup");
    }
}