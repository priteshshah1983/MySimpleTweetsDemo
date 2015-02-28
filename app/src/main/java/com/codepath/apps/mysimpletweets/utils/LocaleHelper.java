package com.codepath.apps.mysimpletweets.utils;

import android.content.Context;

import java.text.NumberFormat;
import java.util.Locale;

public class LocaleHelper {

    public static String localizedNumber(Context context, int number) {
        Locale locale = context.getResources().getConfiguration().locale;
        return NumberFormat.getNumberInstance(locale).format(number);
    }
}
