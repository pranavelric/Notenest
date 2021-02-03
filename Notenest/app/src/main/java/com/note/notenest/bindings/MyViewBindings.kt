package com.note.notenest.bindings

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.note.notenest.utils.gone
import com.note.notenest.utils.visible
import java.io.File


@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String?) {

    Log.d("RRR", "loadImage: " + url)
if(url!=null) {

    Glide.with(view).load(url).into(view)

    view.visible()
}
    else{
        view.gone()
    }

}