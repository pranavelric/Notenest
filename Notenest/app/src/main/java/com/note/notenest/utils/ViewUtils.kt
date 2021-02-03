package com.note.notenest.utils

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.color.colorChooser
import com.google.android.material.snackbar.Snackbar
import com.note.notenest.BuildConfig
import com.note.notenest.R


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.longToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun View.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { sb ->
        sb.setAction("Ok") {
            sb.dismiss()
        }
    }.show()
}

fun ProgressBar.show() {
    visibility = View.VISIBLE
}

fun ProgressBar.hide() {
    visibility = View.GONE
}

fun Activity.setFullScreen() {

    this.window.decorView.setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    )

}


fun Activity.setFullScreenWithBtmNav() {

    this.window.decorView.setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_IMMERSIVE
    )

}

fun Context.share() {


    try {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Notenest")
        var shareMessage = "\nLet me recommend you this amazing application\n"
        shareMessage =
            """
            ${shareMessage} https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
            
            
            """.trimIndent()
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        this.startActivity(Intent.createChooser(shareIntent, "choose one"))
    } catch (e: Exception) {
        //e.toString();
    }


//    this.startActivity(Intent.createChooser(shareIntent, "Share to"))

}


fun Context.shareNotes(title: String, text: String) {

    try {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Notenest")
        var shareMessage = "\n${title}\n"
        shareMessage =
            """
            ${shareMessage} ${text}
            """.trimIndent()
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        this.startActivity(Intent.createChooser(shareIntent, "choose one"))
    } catch (e: Exception) {
        //e.toString();
    }

}


fun Activity.setFullScreenForNotch() {
    this.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        this.window.attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
    }
}

fun Context.transitionAnimationBundle(): Bundle {

    return ActivityOptions.makeCustomAnimation(
        this,
        android.R.anim.fade_in,
        android.R.anim.fade_out
    ).toBundle()
}


fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.inVisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}


fun ViewPager2.setShowSideItems2(pageMarginPx: Int, offsetPx: Int) {

    clipToPadding = false
    clipChildren = false
    offscreenPageLimit = 3


    setPageTransformer { page, position ->

        val myOffset: Float = position * (-(2 * offsetPx + pageMarginPx))
        if (position < -1) {
            page.translationX = -myOffset

        } else if (position <= 1) {
            val scaleFactor = Math.max(0.7f, 1 - Math.abs(position - 0.14285715f))
            page.translationX = myOffset
            page.scaleY = scaleFactor
            page.alpha = scaleFactor
        } else {
            page.alpha = 0F;
            page.translationX = myOffset;
        }

    }


}


fun Activity.hideSoftKeyboard() {
    val inputMethodManager =
        this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    val currentFocusedView = this.currentFocus

    currentFocusedView?.let {
        inputMethodManager.hideSoftInputFromWindow(
            currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

}


fun Context.changeNoteBackgroundColor(view: View) {

    // Get colors.
    val colors = intArrayOf(
        ActivityCompat.getColor(this, R.color.amber_dark),
        ActivityCompat.getColor(this, R.color.amber),
        ActivityCompat.getColor(this, R.color.orange),
        ActivityCompat.getColor(this, R.color.purple_200),
        ActivityCompat.getColor(this, R.color.purple_500),
        ActivityCompat.getColor(this, R.color.blue),
        ActivityCompat.getColor(this, R.color.purple_700),
        ActivityCompat.getColor(this, R.color.white),
        ActivityCompat.getColor(this, R.color.gray),
        ActivityCompat.getColor(this, R.color.pink),
        ActivityCompat.getColor(this, R.color.green),
        ActivityCompat.getColor(this, R.color.brown)
    )

    // Create dialog to choose color.
    MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
        title(R.string.dialog_choose_color)
        colorChooser(colors) { _, color ->
            view.setBackgroundColor(color)

        }
        negativeButton(R.string.dialog_negative)
        positiveButton(R.string.dialog_select)
    }

}


fun Context.discardDialog(from:String, view: View) {
    MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
        title(R.string.dialog_discard)
        message(R.string.dialog_discard_confirmation)
        positiveButton(R.string.dialog_confirmation) {
            when (from) {
                Constants.NOTE_ADD_DISCARD -> view.findNavController()
                    .navigateUp()

                Constants.NOTE_DISCARD -> view.findNavController().navigateUp()
                Constants.ARCHIVE_DISCARD -> view.findNavController().navigateUp()

            }
            toast("Changes not saved")

        }
        negativeButton(R.string.dialog_negative)
    }

}