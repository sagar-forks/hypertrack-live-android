
/*
The MIT License (MIT)

Copyright (c) 2015-2017 HyperTrack (http://hypertrack.com)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package io.hypertrack.sendeta.util.images;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by suhas on 21/01/16.
 */
class EasyImageFiles {

    public static String DEFAULT_FOLDER_NAME = "EasyImage";
    public static String TEMP_FOLDER_NAME = "Temp";


    public static String getFolderName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(BundleKeys.FOLDER_NAME, DEFAULT_FOLDER_NAME);
    }

    public static File tempImageDirectory(Context context) {
        boolean publicTemp = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(BundleKeys.PUBLIC_TEMP, false);
        File dir = publicTemp ? publicTemplDir(context) : privateTemplDir(context);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static File publicRootDir(Context context) {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    }

    public static File publicAppExternalDir(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    public static File publicTemplDir(Context context) {
        File cameraPicturesDir = new File(EasyImageFiles.getFolderLocation(context), EasyImageFiles.getFolderName(context));
        File publicTempDir = new File(cameraPicturesDir, TEMP_FOLDER_NAME);
        if (!publicTempDir.exists()) publicTempDir.mkdirs();
        return publicTempDir;
    }

    private static File privateTemplDir(Context context) {
        return new File(context.getApplicationContext().getCacheDir(), getFolderName(context));
    }

    public static void writeToFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File pickedExistingPicture(Context context, Uri photoUri) throws IOException {
        InputStream pictureInputStream = context.getContentResolver().openInputStream(photoUri);
        File directory = tempImageDirectory(context);
        File photoFile = new File(directory, UUID.randomUUID().toString());
        photoFile.createNewFile();
        writeToFile(pictureInputStream, photoFile);
        return photoFile;
    }

    /**
     * Default folder location will be inside app public directory. That way write permissions after SDK 18 aren't required and contents are deleted if app is uninstalled.
     *
     * @param context context
     */
    public static String getFolderLocation(Context context) {
        String defaultFolderLocation = publicAppExternalDir(context).getPath();
        return PreferenceManager.getDefaultSharedPreferences(context).getString(BundleKeys.FOLDER_LOCATION, defaultFolderLocation);
    }

    public static File getCameraPicturesLocation(Context context) throws IOException {
        File dir = new File(EasyImageFiles.getFolderLocation(context), EasyImageFiles.getFolderName(context));
        if (!dir.exists()) dir.mkdirs();
        File imageFile = File.createTempFile(UUID.randomUUID().toString(), ".jpg", dir);
        return imageFile;
    }
}
