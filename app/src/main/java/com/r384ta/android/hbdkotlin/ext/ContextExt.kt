package com.r384ta.android.hbdkotlin.ext

import android.content.Context
import android.support.annotation.StringRes
import android.widget.Toast

fun Context.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
  Toast.makeText(this, text, duration).show();
}

fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
  Toast.makeText(this, resId, duration).show();
}