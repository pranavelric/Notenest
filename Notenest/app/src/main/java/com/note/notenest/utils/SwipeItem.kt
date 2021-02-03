package com.note.notenest.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.note.notenest.R
import com.note.notenest.utils.Constants.SWIPE_ARCHIVE
import com.note.notenest.utils.Constants.SWIPE_DELETE


abstract class SwipeItem(
    private val action: Int,
    private val background: Drawable,
    private val icon: Drawable,
    private val context: Context
) : ItemTouchHelper.SimpleCallback(0, action) {

    // Necessary empty method.
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        // Get current item view.
        val itemView = viewHolder.itemView

        /** ============================= Draw background. ============================= **/
        when (action) {
            SWIPE_DELETE -> background.setBounds(
                itemView.left + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            SWIPE_ARCHIVE -> background.setBounds(
                itemView.left,
                itemView.top,
                itemView.right + dX.toInt(),
                itemView.bottom
            )
        }

        // Draw.
        background.draw(canvas)

        /** ============================= Draw Icon. ============================= **/
        // Calculate position.
        val itemHeight = itemView.bottom - itemView.top
        val iconMargin = 55
        val iconTop = itemView.top + (itemHeight - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight

        when (action) {
            SWIPE_DELETE -> {
                val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            }
            SWIPE_ARCHIVE -> {
                val iconLeft = itemView.left + iconMargin
                val iconRight = itemView.left + iconMargin + icon.intrinsicWidth
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            }
        }

        // Temporary change icon color for swipe.
        icon.setTint(ActivityCompat.getColor(context, R.color.swipeIconColor))
        // Draw.
        icon.draw(canvas)
        // Restore icon color after swipe.
        icon.setTint(ActivityCompat.getColor(context, R.color.iconColor))

        super.onChildDraw(
            canvas,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }
}