package com.r384ta.android.hbdkotlin

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import butterknife.bindView
import com.eaglesakura.android.oari.ActivityResult
import com.eaglesakura.android.oari.OnActivityResult
import com.r384ta.android.hbdkotlin.ext.IntentFeature
import com.r384ta.android.hbdkotlin.ext.toast
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.toObservable
import rx.schedulers.Schedulers
import timber.log.Timber

class MainActivity : AppCompatActivity(), IntentFeature {
  companion object {
    const val VOICE_INPUT_REQUEST = 200
  }

  val logo: ImageView by bindView(R.id.main_kotlin_logo)
  val button: Button by bindView(R.id.main_hbd_button)
  var mediaPlayer: MediaPlayer? = null

  //region Lifecycle
  override fun onCreate(savedInstanceState: Bundle?) {
    Timber.d("onCreate")
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    button.setOnClickListener {
      try {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
          .putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
          )
          .startActivityWithResult(VOICE_INPUT_REQUEST)
      } catch (e: ActivityNotFoundException) {
        toast(R.string.failed_start_hbd)
      }
    }

    logo.setOnClickListener {
      mediaPlayer?.let {
        if (it.isPlaying) {
          it.stop()
          releaseMediaPlayer()
        }
      }
    }
  }

  override fun onStop() {
    Timber.d("onStop")
    releaseMediaPlayer()
    super.onStop()
  }
  //endregion

  //region Private functions
  private fun releaseMediaPlayer() {
    Timber.d("releaseMediaPlayer")
    mediaPlayer?.let {
      try {
        it.reset()
        it.release()
      } catch(ignore: IllegalStateException) {
      } finally {
        mediaPlayer = null
      }
    }
  }
  //endregion

  //region onActivityResult
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    Timber.d("onActivityResult")
    super.onActivityResult(requestCode, resultCode, data)
    if (!ActivityResult.invoke(this, requestCode, resultCode, data)) {
      Timber.e("Failed onActivityResult")
    }
  }

  @OnActivityResult(VOICE_INPUT_REQUEST)
  fun onVoiceInput(requestCode: Int, data: Intent) {
    Timber.d("onVoiceInput")
    val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
    results?.let {
      if (it.isEmpty()) return@let
      it.toObservable()
        .reduce { s1: String?, s2: String? -> "${s1 ?: ""}${s2 ?: ""}" }
        .map { text ->
          text.contains(getString(R.string.hbd_type_1)) || text.contains(getString(R.string.hbd_type_2))
        }
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ success ->
          if (success) {
            Timber.d("(∩´∀｀)∩ﾜｰｲ")

            // Play music
            assets.openFd("hbd_kotlin.m4a").use { fd ->
              releaseMediaPlayer()
              mediaPlayer = MediaPlayer()
              mediaPlayer?.let { mp ->
                mp.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
                mp.setOnSeekCompleteListener { releaseMediaPlayer() }
                mp.prepare()
                mp.start()
              }
            }
          } else {
            Timber.d("(´・ω・｀)")
            toast(R.string.more_happiness)
          }
        })
    }
  }
  //endregion
}
