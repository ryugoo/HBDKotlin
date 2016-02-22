package com.r384ta.android.hbdkotlin.ext

import android.content.Intent

interface IntentFeature {
  fun startActivityForResult(intent: Intent, requestCode: Int)
  fun Intent.startActivityWithResult(requestCode: Int) {
    startActivityForResult(this, requestCode)
  }
}