package com.note.notenest.bindings

import android.view.View
import android.view.animation.AnimationUtils
import androidx.databinding.BindingAdapter
import com.note.notenest.R


@BindingAdapter("animate")
    fun setToAnimate(view: View, doAnimate: Boolean) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.fade_in)
        if (doAnimate) {
            view.startAnimation(animation)
        }
    }




