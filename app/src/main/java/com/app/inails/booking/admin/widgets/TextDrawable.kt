import android.graphics.*
import android.graphics.drawable.Drawable


class TextDrawable(private val text: String) : Drawable() {
    private val paint: Paint = Paint()
    override fun draw(canvas: Canvas) {
        canvas.drawText(text, 0F, 0F, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.setAlpha(alpha)
    }

    override fun setColorFilter(cf: ColorFilter?) {
        paint.setColorFilter(cf)
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    init {
        paint.setColor(Color.WHITE)
        paint.setTextSize(22f)
        paint.setAntiAlias(true)
        paint.setFakeBoldText(true)
        paint.setShadowLayer(6f, 0F, 0F, Color.BLACK)
        paint.setStyle(Paint.Style.FILL)
        paint.setTextAlign(Paint.Align.LEFT)
    }
}