package uz.alien.facemesheasy


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.common.Triangle
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetectorOptions
import com.google.mlkit.vision.facemesh.FaceMeshPoint


class FaceMeshView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var w = 0
    private var h = 0
    private var bitmap: Bitmap
    private var points: List<FaceMeshPoint> = emptyList()
    private var triangles: List<Triangle<FaceMeshPoint>> = emptyList()
    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val linePaint = Paint().apply {
        strokeWidth = 0.1f
        color = Color.WHITE
        style = Paint.Style.STROKE
    }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.FaceMeshView, 0, 0).apply {
            val resourceId = getResourceId(R.styleable.FaceMeshView_src, 0)
            bitmap = if (resourceId != 0) BitmapFactory.decodeResource(resources, resourceId) else Bitmap.createBitmap(720, 1555, Bitmap.Config.ARGB_8888)
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.w = w
        this.h = h
        setBitmap(bitmap)
    }

    fun setBitmap(bitmap: Bitmap) {
        val scaledHeight = bitmap.height * w / bitmap.width
        val scaledWidth = bitmap.width * h / bitmap.height

        this.bitmap = if (scaledHeight < h) Bitmap.createScaledBitmap(bitmap, w, scaledHeight, true)
        else Bitmap.createScaledBitmap(bitmap, scaledWidth, h, true)

        val options = FaceMeshDetectorOptions.Builder().setUseCase(FaceMeshDetectorOptions.FACE_MESH).build()
        val detector = FaceMeshDetection.getClient(options)

        this.points = emptyList()
        this.triangles = emptyList()

        detector.process(InputImage.fromBitmap(this.bitmap, 0))
            .addOnSuccessListener { faces ->
                for (face in faces) {
                    this.points = face.allPoints
                    this.triangles = face.allTriangles
                }
                invalidate()
            }
            .addOnFailureListener { e -> Log.d("FaceMeshInfo", "Failed $e") }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val left = (width - bitmap.width) / 2f
        val top = (height - bitmap.height) / 2f
        canvas.drawBitmap(bitmap, left, top, null)
        for (point in points) canvas.drawCircle(point.position.x + left, point.position.y + top, 1.6f, paint)
        for (triangle in triangles) {
            val points = triangle.allPoints
            if (points.size == 3) {
                val path = Path().apply {
                    moveTo(points[0].position.x + left, points[0].position.y + top)
                    lineTo(points[1].position.x + left, points[1].position.y + top)
                    lineTo(points[2].position.x + left, points[2].position.y + top)
                    close()
                }
                canvas.drawPath(path, linePaint)
            }
        }
    }
}