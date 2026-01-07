package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class DailyBpChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class BpData(
        val day: String,
        val systolic: Float,
        val diastolic: Float,
        val status: String
    )

    private var bpDataList = listOf<BpData>()

    private val systolicPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0xFFE74C3C.toInt()
    }

    private val diastolicPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0xFF3498DB.toInt()
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFE0E0E0.toInt()
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 28f
        color = 0xFF666666.toInt()
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 32f
        color = 0xFF333333.toInt()
        textAlign = Paint.Align.CENTER
    }

    private val normalLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF27AE60.toInt()
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    fun setData(data: List<BpData>) {
        bpDataList = data
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minHeight = 400
        val padding = 80f
        val groupWidth = 150f // Fixed width per data group for consistent scrolling
        
        val desiredWidth = if (bpDataList.isEmpty()) {
            suggestedMinimumWidth
        } else {
            (padding * 2 + bpDataList.size * groupWidth).toInt()
        }
        
        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(minHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val padding = 80f
        val chartHeight = height - 2 * padding
        val groupWidth = 150f
        val barWidth = groupWidth * 0.25f
        
        // Draw grid lines first (always show these)
        for (i in 0..5) {
            val y = padding + (chartHeight / 5) * i
            canvas.drawLine(padding, y, width.toFloat() - padding, y, gridPaint)
        }

        // Draw Y-axis labels (BP values)
        val maxBp = 250f
        val minBp = 0f
        for (i in 0..5) {
            val value = maxBp - (maxBp - minBp) / 5 * i
            val y = padding + (chartHeight / 5) * i
            canvas.drawText(value.toInt().toString(), padding - 70f, y + 10f, textPaint)
        }

        if (bpDataList.isEmpty()) {
            val centerTextPaint = Paint(labelPaint).apply {
                textSize = 36f
                color = 0xFFAAAAAA.toInt()
            }
            canvas.drawText("No historical data found", width / 2f, height / 2f, centerTextPaint)
            return
        }

        // Draw normal BP reference line (120 systolic)
        val normalY = padding + ((maxBp - 120f) / (maxBp - minBp)) * chartHeight
        canvas.drawLine(padding, normalY, width - padding, normalY, normalLinePaint)
        canvas.drawText("Limit (120)", width - padding + 10f, normalY + 10f, textPaint)

        // Draw bars
        bpDataList.forEachIndexed { index, data ->
            val x = padding + index * groupWidth + groupWidth / 2

            // Systolic bar
            val sysHeight = ((data.systolic - minBp) / (maxBp - minBp)) * chartHeight
            val sysTop = height - padding - sysHeight
            val sysRect = RectF(
                x - barWidth,
                sysTop,
                x - 5f, // Small gap
                height - padding
            )
            canvas.drawRect(sysRect, systolicPaint)

            // Diastolic bar
            val diaHeight = ((data.diastolic - minBp) / (maxBp - minBp)) * chartHeight
            val diaTop = height - padding - diaHeight
            val diaRect = RectF(
                x + 5f,
                diaTop,
                x + barWidth,
                height - padding
            )
            canvas.drawRect(diaRect, diastolicPaint)

            // Day label
            canvas.drawText(data.day, x, height - padding + 40f, labelPaint)

            // BP values on top of bars
            val valuePaint = Paint(textPaint).apply {
                textSize = 20f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(data.systolic.toInt().toString(), x - barWidth/2, sysTop - 10f, valuePaint)
            canvas.drawText(data.diastolic.toInt().toString(), x + barWidth/2, diaTop - 10f, valuePaint)
        }

        // Draw legend (Simple)
        val legendY = 40f
        canvas.drawRect(padding, legendY - 20f, padding + 30f, legendY, systolicPaint)
        canvas.drawText("Systolic", padding + 40f, legendY - 5f, textPaint)
        
        canvas.drawRect(padding + 200f, legendY - 20f, padding + 230f, legendY, diastolicPaint)
        canvas.drawText("Diastolic", padding + 240f, legendY - 5f, textPaint)
    }
}
