package com.upv.magicplace;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.upv.magicplace.search.entities.Location;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Helper {

    public static String getDistanceFromQueryLocation(Location location) {

        android.location.Location locationA = new android.location.Location("Place");
        locationA.setLatitude(location.getLat());
        locationA.setLongitude(location.getLng());

        if (CommonHelper.SEARCH_QUERY_LOCATION != null) {
            double d = locationA.distanceTo(CommonHelper.SEARCH_QUERY_LOCATION);
            return String.format("%.2fkm", d / 1000);
        } else
            return "NO LOCATION";
    }

    public static String getDistanceFromMyLocation(Location location) {
        android.location.Location locationA = new android.location.Location("Place");
        locationA.setLatitude(location.getLat());
        locationA.setLongitude(location.getLng());

        if (CommonHelper.MY_LOCATION != null) {
            double d = locationA.distanceTo(CommonHelper.MY_LOCATION);
            return String.format("%.2fkm", d / 1000);
        } else
            return "NO LOCATION";
    }

    public static String generateUrl(String photoReference) {
        String url = String.format("https://maps.googleapis.com/maps/api/place/photo?photoreference=%s&key=AIzaSyCwmYvGIV7owfcc7muneajVaIz6cXKA8Wg" +
                "&maxheight=800", photoReference);

        return url;
    }

    public static void hideKeyboard(Context context, IBinder iBinder) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(iBinder, 0);
    }

    public static String getCurrentDate() {
        DateFormat df = new SimpleDateFormat("EEE, MMM d, ''yy", Locale.getDefault());
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

    public static ArrayList<String> getAllShownImagesPath(Context context) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = context.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;

    }

    public static void openKeyboard(Context context, EditText editText) {
        /*InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);*/

        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static int generateRandomCodeColor() {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return color;
    }
}
