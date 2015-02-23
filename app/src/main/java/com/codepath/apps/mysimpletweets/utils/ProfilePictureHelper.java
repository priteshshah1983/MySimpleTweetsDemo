package com.codepath.apps.mysimpletweets.utils;

import android.graphics.Color;

import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Transformation;

public class ProfilePictureHelper {

    public static Transformation roundedCornersTranformation() {
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.TRANSPARENT)
                .borderWidthDp(0)
                .cornerRadiusDp(5)
                .oval(false)
                .build();

        return transformation;
    }
}
