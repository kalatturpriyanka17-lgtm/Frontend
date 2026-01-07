package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class BpTrendBarChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val sbpPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0xFFE74C3C.toInt() // Red for SBP
    }

    private val dbpPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0xFF3498DB.toInt() // Blue for DBP
    }
    
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF666666.toInt()
        textSize = 30f
        textAlign = Paint.Align.CENTER
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFEEEEEE.toInt()
        strokeWidth = 2f
    }

    private val referenceLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFF0000.toInt() // Red for reference line
        strokeWidth = 3f
        pathEffect = android.graphics.DashPathEffect(floatArrayOf(10f, 10f), 0f)
        alpha = 100 // Transparent like alpha=0.3 in python
    }

    private var data: List<BpData> = emptyList()

    data class BpData(val label: String, val sbp: Float, val dbp: Float, val severity: String)

    fun setData(newData: List<BpData>) {
        data = newData
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minHeight = 400
        val padding = 120f
        val groupWidth = 200f // Fixed width per data group for consistent scrolling
        
        val desiredWidth = if (data.isEmpty()) {
            suggestedMinimumWidth
        } else {
            (padding * 2 + data.size * groupWidth).toInt()
        }
        
        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(minHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data.isEmpty()) return

        val padding = 60f
        val chartHeight = height - padding * 2
        
        // Fixed group width to match onMeasure
        val groupWidth = 200f
        val barWidth = groupWidth * 0.3f
        
        // Scale: Max BP is usually around 200, let's fix max to 200 or find max in data
        val maxBp = 250f // Updated to match python range [50-250]

        // Draw baseline
        canvas.drawLine(padding, height - padding, width - padding, height - padding, linePaint)

        // Draw Normal Systolic Limit (120 mmHg) Reference Line
        val refLineY = height - padding - (120f / maxBp) * chartHeight
        canvas.drawLine(padding, refLineY, width - padding, refLineY, referenceLinePaint)
        canvas.drawText("Normal Limit (120)", padding + 80f, refLineY - 10f, textPaint.apply { textSize = 20f })

        data.forEachIndexed { index, bpData ->
            val groupCenter = padding + index * groupWidth + groupWidth / 2
            
            // SBP Bar (Left of center)
            val sbpLeft = groupCenter - barWidth - 5f // 5f spacing
            val sbpTop = height - padding - (bpData.sbp / maxBp) * chartHeight
            val sbpRight = sbpLeft + barWidth
            val bottom = height - padding
            
            canvas.drawRoundRect(RectF(sbpLeft, sbpTop, sbpRight, bottom), 8f, 8f, sbpPaint)

            // DBP Bar (Right of center)
            val dbpLeft = groupCenter + 5f
            val dbpTop = height - padding - (bpData.dbp / maxBp) * chartHeight
            val dbpRight = dbpLeft + barWidth
            
            canvas.drawRoundRect(RectF(dbpLeft, dbpTop, dbpRight, bottom), 8f, 8f, dbpPaint)

            // Draw Month Label
            canvas.drawText(bpData.label, groupCenter, height - padding / 2 + 30f, textPaint)
            
            // Draw Values (Optional, maybe skip if too crowded or small)
             canvas.drawText("${bpData.sbp.toInt()}", sbpLeft + barWidth/2, sbpTop - 10f, textPaint)
             canvas.drawText("${bpData.dbp.toInt()}", dbpLeft + barWidth/2, dbpTop - 10f, textPaint)
        }
        
        // Legend (Simple)
        // Can be done in XML or drawn here. Let's draw minimal legend at top
        // canvas.drawRect(...) for SBP and DBP legend
    }
}
