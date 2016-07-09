package com.lightconewritesdcardlist;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String TAG = "MEDIA";
    private TextView tv;
    private int recursionLevel = 0;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.TextView01);
        checkExternalMedia();
        writeToSDFile();
        readRaw();
        listFilesInDirectory(Environment.getExternalStorageDirectory());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Method to check whether external media available and writable. This is adapted from
     * http://developer.android.com/guide/topics/data/data-storage.html#filesExternal
     */

    private void checkExternalMedia() {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        tv.append("\n\nExternal Media: readable="
                + mExternalStorageAvailable + " writable=" + mExternalStorageWriteable);
    }

    /**
     * Method to write ascii text characters to file on SD card. Note that you must add a
     * WRITE_EXTERNAL_STORAGE permission to the manifest file or this method will throw
     * a FileNotFound Exception because you won't have write permission.
     */

    private void writeToSDFile() {

        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-storage.html#filesExternal

        File root = android.os.Environment.getExternalStorageDirectory();
        tv.append("\nExternal file system root: " + root);

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        File dir = new File(root.getAbsolutePath() + "/download");
        dir.mkdirs();
        File file = new File(dir, "myData.txt");

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println("Howdy do to you.");
            pw.println("Here is a second line.");
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
        tv.append("\n\nFile written to " + file);
    }

    /**
     * Method to read in a text file placed in the res/raw directory of the application. The
     * method reads in all lines of the file sequentially.
     */

    private void readRaw() {
        tv.append("\nData read from res/raw/textfile.txt:");
        InputStream is = this.getResources().openRawResource(R.raw.textfile);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr, 8192);    // 2nd arg is buffer size

        // More efficient (less readable) implementation of above is the composite expression
        /*BufferedReader br = new BufferedReader(new InputStreamReader(
				this.getResources().openRawResource(R.raw.textfile)), 8192);*/

        try {
            String test;
            while (true) {
                test = br.readLine();
                // readLine() returns null if no more lines in the file
                if (test == null) break;
                tv.append("\n" + "    " + test);
            }
            isr.close();
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tv.append("\n\nThat is all");
    }

    /**
     * Recursive method to list all files and their complete paths in dir and its subdirectories.
     * Adapted from java at http://www.javamex.com/tutorials/techniques/recursion.shtml
     */

    private void listFilesInDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                // Absolute path to file or directory and number of recursions
                filePath = f.getAbsolutePath();
                recursionLevel = numberOfOccurrences(filePath, File.separator) - 1;
                String printString = printSpacer(recursionLevel) + filePath;
                if (f.isDirectory()) {
                    Log.i(TAG, printString);
                    listFilesInDirectory(f);  // Recursion if it is a directory
                } else {
                    Log.i(TAG, printString + " " + f.length() + " bytes");  // If a file
                }
            }
        }
    }

    /**
     * Utility method to return the number of occurrences of "substring" in "string"
     */

    private int numberOfOccurrences(String string, String substring) {
        int len = string.length();
        int num = 0;
        int c = -1;
        while (c < len) {
            c = string.indexOf(substring, c + 1);
            if (c == -1) break;
            num++;
        }
        return num;
    }

    /**
     * Utility method to add spaces before printed path according to recursion level
     */

    private String printSpacer(int recursions) {
        String spacer = "";
        String increment = "  ";
        for (int i = 0; i < recursions; i++) {
            spacer = increment + spacer;
        }
        return spacer;
    }

}
