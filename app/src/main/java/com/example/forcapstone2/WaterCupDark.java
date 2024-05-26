package com.example.forcapstone2;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class WaterCupDark extends View {
    private Paint paint;
    private int goalAmount = 2000; // Goal water amount in ml
    private int currentAmount = 0; // Current amount of water consumed in ml

    public WaterCupDark(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaterCupDark(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(100); // Text size
        paint.setTextAlign(Paint.Align.CENTER); // Text alignment
        setLayerType(LAYER_TYPE_SOFTWARE, null); // Set software layer type for BlurMaskFilter usage
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        float startHeightAdjustment = height * 0.1f; // Adjust the start point downward

        // Background gradient
        LinearGradient backgroundGradient = new LinearGradient(0, 0, 0, height,
                new int[]{Color.parseColor("#194569"), Color.parseColor("#FF000000"), Color.parseColor("#FF000000")},
                new float[]{0, 0.5f, 1}, Shader.TileMode.CLAMP);
        paint.setShader(backgroundGradient);

        // Draw background gradient
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, width, height, paint);

        // Water drop path
        Path dropPath = new Path();
        dropPath.moveTo(width / 2f, (height * 0.2f) + startHeightAdjustment);
        dropPath.quadTo(width * 0.1f, (height * 0.6f) + startHeightAdjustment, width / 2f, (height * 0.6f) + startHeightAdjustment);
        dropPath.quadTo(width * 0.9f, (height * 0.6f) + startHeightAdjustment, width / 2f, (height * 0.2f) + startHeightAdjustment);
        dropPath.close();

        // Shadow paint
        Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setColor(Color.parseColor("#44000000")); // Lighter translucent black shadow
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setMaskFilter(new BlurMaskFilter(30, BlurMaskFilter.Blur.NORMAL)); // Set blur effect for shadow

        // Draw shadow
        canvas.save();
        canvas.translate(15, 35); // Offset shadow to the bottom right
        canvas.drawPath(dropPath, shadowPaint);
        canvas.restore();

        // Water drop gradient
        LinearGradient dropGradient = new LinearGradient(0, 0, 0, height,
                new int[]{Color.parseColor("#dbecf4"), Color.parseColor("#cadeed"), Color.parseColor("#5f84a2")},
                new float[]{0, 0.6f, 1}, Shader.TileMode.CLAMP);
        paint.setShader(dropGradient);
        paint.setMaskFilter(null); // Remove blur effect

        // Fill water drop
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(dropPath, paint);

        // Add highlight to water drop
        RadialGradient highlightGradient = new RadialGradient(width / 2.05f, (height * 0.3f) + startHeightAdjustment,
                width / 3f, new int[]{Color.WHITE, Color.TRANSPARENT}, null, Shader.TileMode.CLAMP);
        paint.setShader(highlightGradient);
        canvas.drawPath(dropPath, paint);

        // Set clipping region
        canvas.save();
        canvas.clipPath(dropPath);

        // Set path for water filling
        Path waterPath = new Path();
        waterPath.moveTo(width * 0.1f, (height * 0.6f) + startHeightAdjustment);
        waterPath.quadTo(width * 0.25f, (height * 0.5f) + startHeightAdjustment, width * 0.5f, (height * 0.55f) + startHeightAdjustment);
        waterPath.quadTo(width * 0.75f, (height * 0.6f) + startHeightAdjustment, width * 0.9f, (height * 0.5f) + startHeightAdjustment);
        waterPath.lineTo(width * 0.9f, (height * 0.6f) + startHeightAdjustment);
        waterPath.lineTo(width * 0.1f, (height * 0.6f) + startHeightAdjustment);
        waterPath.close();

        // Water gradient
        LinearGradient waterGradient = new LinearGradient(0, (height * 0.55f) + startHeightAdjustment, 0, (height * 0.6f) + startHeightAdjustment,
                new int[]{Color.parseColor("#5f84a2"), Color.parseColor("#cadeed")},
                new float[]{0, 1}, Shader.TileMode.CLAMP);
        paint.setShader(waterGradient);

        // Fill water
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(waterPath, paint);

        // Remove clipping region
        canvas.restore();

        // Set text style to bold white
        paint.setShader(null); // Remove gradient for text
        paint.setColor(Color.WHITE); // Set text color to white
        paint.setStyle(Paint.Style.FILL_AND_STROKE); // Set text style to fill and stroke
        paint.setStrokeWidth(7); // Set text stroke width

        // Calculate percentage of goal achieved
        float percentage = (float) currentAmount / goalAmount;
        String text = String.format("%.0f%%", percentage * 100); // Convert to percentage

        // Draw text, adjust position
        canvas.drawText(text, width / 2f, (height * 0.5f) + startHeightAdjustment, paint);

        // Reset paint settings
        paint.setStyle(Paint.Style.FILL); // Reset style to fill
        paint.setStrokeWidth(0); // Reset stroke width

        // Draw small water drops
        drawSmallDrop(canvas, width * 0.15f, height * 0.09f + startHeightAdjustment, width * 0.04f);
        drawSmallDrop(canvas, width * 0.20f, height * 0.04f + startHeightAdjustment, width * 0.03f);
    }

    private void drawSmallDrop(Canvas canvas, float cx, float cy, float radius) {
        // Small drop shadow
        Paint smallShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        smallShadowPaint.setColor(Color.parseColor("#21000000")); // Lighter translucent black
        smallShadowPaint.setStyle(Paint.Style.FILL);
        smallShadowPaint.setMaskFilter(new BlurMaskFilter(30, BlurMaskFilter.Blur.NORMAL)); // Set blur effect

        // Draw small drop shadow
        canvas.save();
        canvas.translate(10, 20); // Offset shadow to the bottom right
        canvas.drawCircle(cx, cy, radius, smallShadowPaint);
        canvas.restore();

        // Small drop gradient
        RadialGradient smallDropGradient = new RadialGradient(cx, cy, radius,
                new int[]{Color.parseColor("#dbecf4"), Color.parseColor("#cadeed"), Color.parseColor("#dbecf4")},
                new float[]{0, 0.6f, 1}, Shader.TileMode.CLAMP);
        paint.setShader(smallDropGradient);

        // Draw small drop
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, cy, radius, paint);

        // Add highlight to small drop
        RadialGradient smallHighlightGradient = new RadialGradient(cx, cy - radius / 3, radius / 3,
                new int[]{Color.WHITE, Color.TRANSPARENT}, null, Shader.TileMode.CLAMP);
        paint.setShader(smallHighlightGradient);
        canvas.drawCircle(cx, cy, radius, paint);

        // Reset paint settings
        paint.setShader(null);
    }

    // Method to set the current amount of water consumed
    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
        invalidate(); // Redraw view with updated water amount
    }
}
