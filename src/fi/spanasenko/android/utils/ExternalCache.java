package fi.spanasenko.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.*;
import java.net.URLEncoder;

/**
 * ExternalCache
 * Provides utility methods to cache files in external storage.
 */
public class ExternalCache {

    private static final String DIR_SUFFIX = "/images";

    private static final int CACHE_MAX_SIZE = 7500000;    // 7.5 MegaBytes

    private Context mContext;
    private long mCurExternalCacheSize;
    private String mDirectory = "";

    public ExternalCache(Context context) {
        mContext = context;
        mDirectory = mContext.getExternalFilesDir(null) + DIR_SUFFIX;
        mCurExternalCacheSize = getExternalCacheSize(new File(mDirectory));
    }

    /**
     * Adds this bitmap to the external cache.
     *
     * @param url    The url link to the image
     * @param bitmap The newly downloaded bitmap.
     */
    public void addBitmapToExternalCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            try {
                String filePath = createFilePath(url);
                if (filePath != null) {
                    // Write the image to file
                    writeToExternalCache(filePath, bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if the image is in the external cache (sd card)
     *
     * @param url The url of the image to locate it in memory
     * @return File if the file is found, otherwise returns null
     */
    public File checkExternalCache(String url) {

        File dir = new File(mDirectory);

        if (dir != null) {
            // Check if cache needs to be cleared
            if (mCurExternalCacheSize >= CACHE_MAX_SIZE) {
                clearCache(dir);
                mCurExternalCacheSize = 0;
                return null;
            }

            String imageFile;
            try {
                imageFile = URLEncoder.encode(url, "UTF-8");
                File file = new File(mDirectory, imageFile);
                if (file != null && file.exists()) {
                    // Found the file in the external cache
                    return file;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Creates a file path to store the image
     *
     * @param url The unique image url path on the server
     * @return String of the file path, null if a file path couldn't be created
     */
    private String createFilePath(String url) {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            // Has external storage to save image            
            String dirPath = createDirectory();

            if (dirPath != null && dirPath.length() > 0) {
                String imageFile;
                try {
                    // Create the file path
                    imageFile = URLEncoder.encode(url, "UTF-8");
                    File file = new File(dirPath, imageFile);
                    if (file != null) {
                        return file.toString();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * Creates a directory on the externalFilesDir
     *
     * @return String of the directory path
     */
    private String createDirectory() {
        File directory = new File(mDirectory);
        // have the object build the directory structure, if needed.
        if (directory != null) {
            directory.mkdirs();
            return directory.toString();
        }
        return null;
    }

    /**
     * Returns the size of all the files in the given directory
     *
     * @param dir The directory you want to know the size of
     * @return The size of all the files in the directory
     */
    private long getExternalCacheSize(File dir) {
        long size = 0;
        if (dir != null) {
            File[] fileList = dir.listFiles();
            if (fileList != null) {
                for (int i = 0; i < fileList.length; i++) {
                    // Recursive call if it's a directory
                    if (fileList[i].isDirectory()) {
                        size += getExternalCacheSize(fileList[i]);
                    } else {
                        // Sum the file size in bytes
                        size += fileList[i].length();
                    }
                }
            }
        }
        return size;
    }

    /**
     * Clears all files in the given directory
     *
     * @param dir The file path to the directory to clear
     */
    private void clearCache(File dir) {
        if (dir != null) {
            File[] fileList = dir.listFiles();
            if (fileList != null) {
                for (int i = 0; i < fileList.length; i++) {
                    // Recursive call if it's a directory
                    if (fileList[i].isDirectory()) {
                        clearCache(fileList[i]);
                    } else {
                        // Sum the file size in bytes
                        fileList[i].delete();
                    }
                }
            }
        }
    }

    /**
     * Start a thread to write the bitmap to the external cache
     *
     * @param filePath The filepath string to write the image to
     * @param bitmap   The image itself
     */
    private void writeToExternalCache(final String filePath, final Bitmap bitmap) {
        new Thread(new Runnable() {
            public void run() {
                FileOutputStream out;
                if (filePath != null) {
                    try {
                        out = new FileOutputStream(filePath);
                        if (out != null) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                            File file = new File(filePath);
                            if (file != null) {
                                mCurExternalCacheSize += file.length();
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
