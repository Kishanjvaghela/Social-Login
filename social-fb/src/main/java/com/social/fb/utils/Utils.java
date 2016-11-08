package com.social.fb.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by CS02 on 11/8/2016.
 */

public class Utils {
  public static void makeToast(Context context, String text) {
    if (context != null) Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
  }

  public static void makeToast(Context context, int stringRes) {
    if (context != null) Toast.makeText(context, stringRes, Toast.LENGTH_SHORT).show();
  }
}
