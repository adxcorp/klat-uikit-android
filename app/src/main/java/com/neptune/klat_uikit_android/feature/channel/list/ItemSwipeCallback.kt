package com.neptune.klat_uikit_android.feature.channel.list

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.core.extension.dpToPx

class ItemSwipeCallback(
    private val swipeCallbackListener: SwipeCallbackListener,
    private val context: Context
) : ItemTouchHelper.Callback() {
    private val exitButtonPaint = Paint().apply { color = Color.RED }
    private val readButtonPaint = Paint().apply { color = Color.BLUE }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = UN_USE
        val swipeFlags = ItemTouchHelper.LEFT
        return makeMovementFlags(
            dragFlags,
            swipeFlags
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView = viewHolder.itemView

            val stopDx = (-144).dpToPx(context)
            val clampedDX = if (dX < stopDx) stopDx else dX
            // 스와이프 범위 제한
//            val clampedDX = if (dX < -threshold) -threshold else dX

            // UI 커스텀 (버튼 렌더링)
            drawButtons(c, itemView, clampedDX)

            // 실제 아이템 움직임 반영
            super.onChildDraw(c, recyclerView, viewHolder, clampedDX, dY, actionState, isCurrentlyActive)
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    private fun drawButtons(c: Canvas, itemView: android.view.View, dX: Float) {
        // 읽음 버튼
        val readButton = RectF(
            itemView.right + dX,
            itemView.top.toFloat(),
            itemView.right.toFloat(),
            itemView.bottom.toFloat()
        )

        val readPaint = Paint().apply { color = Color.BLUE }
        c.drawRect(readButton, readPaint)
        drawText(c, "읽음", readButton, Color.WHITE)

        // 나가기 버튼
//        val exitButton = android.graphics.RectF(
//            readButton.right,
//            itemView.top.toFloat(),
//            readButton.right,
//            itemView.bottom.toFloat()
//        )
//        val exitPaint = Paint().apply {
//            color = Color.RED
//        }
//        c.drawRect(exitButton, exitPaint)
//        drawText(c, "나가기", exitButton, Color.WHITE)
    }

    private fun drawText(c: Canvas, text: String, button: RectF, textColor: Int) {
        val paint = Paint().apply {
            color = textColor
            textSize = 40f
            textAlign = android.graphics.Paint.Align.CENTER
        }
        c.drawText(text, button.centerX(), button.centerY() + paint.textSize / 2, paint)
    }

//    override fun onChildDraw(
//        c: Canvas,
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder,
//        dX: Float,
//        dY: Float,
//        actionState: Int,
//        isCurrentlyActive: Boolean
//    ) {
//        val stopDx = (-144).dpToPx(context)
//        val clampedDx = if (dX < stopDx) stopDx else dX
//
//        val itemView = viewHolder.itemView
//        val exitButtonWidth = 72.dpToPx(context) // 버튼 크기
//        val readButtonWidth = 72.dpToPx(context) // 버튼 크기
//
//        if (dX < stopDx) {
//            // 버튼의 배경
//            c.drawRect(
//                itemView.right + (clampedDx / 2),
//                itemView.top.toFloat(),
//                itemView.right.toFloat(),
//                itemView.bottom.toFloat(),
//                exitButtonPaint
//            )
//
//            c.drawRect(
//                itemView.right + clampedDx,
//                itemView.top.toFloat(),
//                itemView.right.toFloat(),
//                itemView.bottom.toFloat(),
//                readButtonPaint
//            )
//
//            // 버튼의 텍스트
//            val textPaint = Paint().apply {
//                color = Color.WHITE
//                textSize = 40f
//                textAlign = Paint.Align.CENTER
//            }
//
//            c.drawText(
//                "나가기",
//                itemView.right - exitButtonWidth / 2,
//                itemView.top + itemView.height / 2f + 15f,
//                textPaint
//            )
//
//            c.drawText(
//                "나가기",
//                itemView.right - readButtonWidth,
//                itemView.top + itemView.height / 2f + 15f,
//                textPaint
//            )
//        }
//        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    companion object {
        private const val UN_USE = 0
    }
}

