package com.app.test.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.app.test.R;
import com.google.android.material.snackbar.Snackbar;
import com.valdesekamdem.library.mdtoast.MDToast;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;


import static com.app.test.utils.MyApplication.context;

public class CommonFunctions {

    private static CommonFunctions ourInstance = new CommonFunctions();


//    public static String MAP_KEY = "AIzaSyCiV7HmmQX4Q-HVyzgGfxZ7v5xs29zy-no"; // Old
    public static String MAP_KEY = "AIzaSyAsYI3WW7caUHJjuTEtUFG1mYTsjscVa5c"; // new
    public static String MAP_KEY_PATH = "AIzaSyAoBimyGS5412B53_5GM05__J7UoiVDuJg";

    private CommonFunctions() {
    }

    public static CommonFunctions getInstance() {
        if (ourInstance == null) {
            synchronized (CommonFunctions.class) {
                if (ourInstance == null)
                    ourInstance = new CommonFunctions();
            }
        }
        return ourInstance;
    }



    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Hide actionbar from activity class
     *
     * @param actionbar
     */
    public void hideActionBar(ActionBar actionbar) {
        if (actionbar != null) {
            actionbar.hide();
        }
    }

    /**
     * @param context          Context fromactivity or fragment
     * @param bundle           Bundle of values for next Activity
     * @param destinationClass Destination Activity
     * @param isFinish         Current activity need to finish or not
     */
    public void newIntent(Context context, Class destinationClass, Bundle bundle, boolean isFinish, boolean isFlags) {
        if (!isActivityRunning(context)) {
            return;
        }
        Intent intent = new Intent(context, destinationClass);
        intent.putExtras(bundle);
        if (isFlags) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        if (isFinish) {
            ((Activity) context).finish();
        }
    }

    public void newIntent(Context context, Class destinationClass, Bundle bundle) {
        if (!isActivityRunning(context)) {
            return;
        }
        Intent intent = new Intent(context, destinationClass);
        intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);

    }

    /**
     * new intent with result get called here
     *
     * @param context
     * @param destinationClass
     * @param bundle
     * @param isFinish
     * @param requestCode
     */
    public void newIntentForResult(Context context, Class destinationClass, Bundle bundle, boolean isFinish, int requestCode) {
        Intent intent = new Intent(context, destinationClass);
        intent.putExtras(bundle);
        ((Activity) context).startActivityForResult(intent, requestCode);
        if (isFinish) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ((Activity) context).finish();
        }
    }

    public void EmptyField(Context context, String message) {
        String result = MessageFormat.format(LanguageConstants.CannotBeEmpty, message);
        Toast mdToast = Toast.makeText(context, result, Toast.LENGTH_LONG);
        mdToast.show();
    }

    /**
     * This method will help to find the
     * activity is running or not
     *
     * @param ctx
     * @return
     */
    public boolean isActivityRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }

        return false;
    }

    public static boolean CheckInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public void displayKnownError(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void displayUnKnownError(Context context) {
        Toast.makeText(context, LanguageConstants.somethingWentWrong, Toast.LENGTH_LONG).show();
    }


    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    // Mobile number validation
    public boolean isValidMobile(String mobile) {
        if (!TextUtils.isEmpty(mobile)) {
            return Patterns.PHONE.matcher(mobile).matches();
        }
        return false;
    }






    // --- TYPE_ERROR
    public void validationEmptyError(Context context, String message) {
        View parent = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar snackbar;
        snackbar = Snackbar.make(parent, message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(context.getResources().getColor(R.color.red));
        TextView textView = (TextView) snackBarView.findViewById(R.id.snackbar_text);
        textView.setTextColor(context.getResources().getColor(R.color.colorWhite));
        //snackbar.show();

        MDToast mdToast = MDToast.makeText(context, message, Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
        mdToast.show();
    }

    // --- TYPE_ERROR
    public void validationError(Context context, String message) {
        View parent = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar snackbar;
        snackbar = Snackbar.make(parent, message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(context.getResources().getColor(R.color.red));
        TextView textView = (TextView) snackBarView.findViewById(R.id.snackbar_text);
        textView.setTextColor(context.getResources().getColor(R.color.colorWhite));
        //snackbar.show();

        MDToast mdToast = MDToast.makeText(context, message, Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
        mdToast.show();
    }

    // --- TYPE_ERROR Customize
    public void validationError_Custom(Context context, String message) {

        View layoutValue = LayoutInflater.from(context).inflate(R.layout.android_custom_toast_example, null);
        TextView text = layoutValue.findViewById(R.id.CustomToastMessage);
        text.setText(message);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layoutValue);
        toast.show();

    }

    // --- TYPE_SUCCESS
    public void successResponseToast(Context context, String message) {
        View parent = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar snackbar;
        snackbar = Snackbar.make(parent, message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(context.getResources().getColor(R.color.green));
        TextView textView = (TextView) snackBarView.findViewById(R.id.snackbar_text);
        textView.setTextColor(context.getResources().getColor(R.color.colorWhite));
        //snackbar.show();

        MDToast mdToast = MDToast.makeText(context, message, Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
        mdToast.show();
    }


    public String GeoCodingAddress_details(Activity activity, double workLatitude, double workLongitude) {

        List<Address> addresses = null;
        Geocoder mGeocoder = new Geocoder(activity, Locale.getDefault());
        try {
            addresses = mGeocoder.getFromLocation(workLatitude, workLongitude,
                    // In this sample, get just a single address.
                    1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String mPlaceName = "", mSubPlace = "", mLocality = "", mState = "", mCountry = "";
        try {
            if (addresses != null && addresses.size() != 0) {
                for (int i = 0; i < addresses.size(); i++) {
                    mPlaceName = addresses.get(i).getFeatureName();
                    mSubPlace = addresses.get(i).getSubLocality();
                    mLocality = addresses.get(i).getLocality();
                    mState = addresses.get(i).getAdminArea();
                    mCountry = addresses.get(i).getCountryName();
                }
            }

        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        String addressFull = "";
        if (!TextUtils.isEmpty(mPlaceName)) {
            addressFull = addressFull + mPlaceName;
        }
        if (!TextUtils.isEmpty(mSubPlace)) {
            addressFull = addressFull + "," + mSubPlace;
        }
        if (!TextUtils.isEmpty(mLocality)) {
            addressFull = addressFull + "," + mLocality;
        }
        if (!TextUtils.isEmpty(mState)) {
            addressFull = addressFull + "," + mState;
        }
        if (!TextUtils.isEmpty(mCountry)) {
            addressFull = addressFull + "," + mCountry;
        }

        return addressFull;
    }


    public Address geoCodingAddress(Activity activity, double workLatitude, double workLongitude) {
        List<Address> addresses = null;
        Geocoder mGeocoder = new Geocoder(activity, Locale.getDefault());

        try {
            addresses = mGeocoder.getFromLocation(workLatitude, workLongitude,
                    // In this sample, get just a single address.
                    1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = null;
        try {
            if (addresses != null && addresses.size() != 0) {
                for (int i = 0; i < addresses.size(); i++) {
                    address = addresses.get(i);
                }
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        return address;
    }

    public boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean isValidPassword(String password) {
        /*String passwordCombination = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
        return password.matches(passwordCombination);*/
        return true;
    }


    public static void hideKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
    }

}
