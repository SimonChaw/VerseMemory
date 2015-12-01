package org.orithoncore.versememory;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataBaseBundler extends AppCompatActivity {
    public DataBaseBundler(){ }

    public void bundleDataBase(String getPackageName, Context getBaseContext, String dbName){
        try {

            // build the full path to the database in the databases folder (where our db goes!)
            String destPath = "/data/data/" + getPackageName + "/databases/" + dbName;
            // construct a file object
            File f = new File(destPath);

            // does the database file exist?
            if (!f.exists()) {
                // we have to bundle the database with app - first run!
                Log.d("db", "Bundling database!");

                // manually make the databases folder
                File directory = new File("/data/data/" + getPackageName + "/databases");
                directory.mkdir();

                copyDB(getBaseContext.getAssets().open(dbName), new FileOutputStream(destPath));
            }
        } catch (IOException e) {
            Log.d("db", "IOException: " + e.getMessage());
        }
    }

    private void copyDB(InputStream inputStream, FileOutputStream fileOutputStream) throws IOException {
        // array of 1024 bytes of data (1K)
        byte[] buffer = new byte[1024];

        int length;
        // read the first 1K of data from inputStream
        length = inputStream.read(buffer);
        while (length > 0){
            // write the data to the outputstream
            fileOutputStream.write(buffer, 0, length);
            // read the next 1K of data
            length = inputStream.read(buffer);
        }

        // close the streams
        inputStream.close();
        fileOutputStream.close();

        Log.d("db","Database has been bundled :)");
    }
}
