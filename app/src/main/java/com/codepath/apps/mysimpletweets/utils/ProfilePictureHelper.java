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

    public static Transformation roundedCornersTranformationWithBorder(int color) {
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(color)
                .borderWidthDp(5)
                .cornerRadiusDp(5)
                .oval(false)
                .build();

        return transformation;
    }
}
