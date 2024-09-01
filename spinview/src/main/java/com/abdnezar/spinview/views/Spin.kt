package com.abdnezar.spinview.views

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.TimeInterpolator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.IntDef
import androidx.core.graphics.ColorUtils
import com.abdnezar.spinview.model.SpinItem
import com.abdnezar.spinview.utils.Utils.drawableToBitmap
import java.util.Random
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class Spin : View {
    private var mRange = RectF()
    private var mRadius = 0

    private var mArcPaint: Paint? = null
    private var mBackgroundPaint: Paint? = null
    private var mTextPaint: TextPaint? = null

    private val mStartAngle = 0f
    private var mCenter = 0
    private var mPadding = 0
    private var mTopTextPadding = 0
    private var mTopTextSize = 0
    private var mSecondaryTextSize = 0
    private var mRoundOfNumber = 4
    private var mEdgeWidth = -1
    private var isRunning = false

    private var borderColor = 0
    private var defaultBackgroundColor = 0
    private var drawableCenterImage: Drawable? = null
    private var textColor = 0

    private var predeterminedNumber = -1

    private var viewRotation: Float = 0f
    private var fingerRotation: Double = 0.0
    private var downPressTime: Long = 0
    private var upPressTime: Long = 0
    private var newRotationStore: DoubleArray = DoubleArray(3)


    private var mSpinItemList: List<SpinItem>? = null

    private var mPieRotateListener: PieRotateListener? = null

    val spinItemListSize: Int
        get() = mSpinItemList!!.size

    interface PieRotateListener {
        fun rotateDone(index: Int)
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    fun setPieRotateListener(listener: PieRotateListener?) {
        this.mPieRotateListener = listener
    }

    private fun init() {
        mArcPaint = Paint()
        mArcPaint!!.isAntiAlias = true
        mArcPaint!!.isDither = true

        mTextPaint = TextPaint()
        mTextPaint!!.isAntiAlias = true


        if (textColor != 0) mTextPaint!!.color = textColor
        mTextPaint!!.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 14f,
            resources.displayMetrics
        )

        mRange = RectF(
            mPadding.toFloat(),
            mPadding.toFloat(),
            (mPadding + mRadius).toFloat(),
            (mPadding + mRadius).toFloat()
        )
    }

    fun setData(spinItemList: List<SpinItem>?) {
        this.mSpinItemList = spinItemList
        invalidate()
    }

    fun setPieBackgroundColor(color: Int) {
        defaultBackgroundColor = color
        invalidate()
    }

    fun setBorderColor(color: Int) {
        borderColor = color
        invalidate()
    }

    fun setTopTextPadding(padding: Int) {
        mTopTextPadding = padding
        invalidate()
    }


    fun setPieCenterImage(drawable: Drawable?) {
        drawableCenterImage = drawable
        invalidate()
    }

    fun setTopTextSize(size: Int) {
        mTopTextSize = size
        invalidate()
    }

    fun setSecondaryTextSizeSize(size: Int) {
        mSecondaryTextSize = size
        invalidate()
    }


    fun setBorderWidth(width: Int) {
        mEdgeWidth = width
        invalidate()
    }

    fun setPieTextColor(color: Int) {
        textColor = color
        invalidate()
    }

    private fun drawPieBackgroundWithBitmap(canvas: Canvas, bitmap: Bitmap) {
        canvas.drawBitmap(
            bitmap, null, Rect(
                mPadding / 2, mPadding / 2,
                measuredWidth - mPadding / 2,
                measuredHeight - mPadding / 2
            ), null
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (mSpinItemList == null) {
            return
        }

        drawBackgroundColor(canvas, defaultBackgroundColor)

        init()

        var tmpAngle = mStartAngle
        val sweepAngle = 360f / mSpinItemList!!.size

        for (i in mSpinItemList!!.indices) {
            if (mSpinItemList!![i].color != 0) {
                mArcPaint!!.style = Paint.Style.FILL
                mArcPaint!!.color = mSpinItemList!![i].color
                canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint!!)
            }

            if (borderColor != 0 && mEdgeWidth > 0) {
                mArcPaint!!.style = Paint.Style.STROKE
                mArcPaint!!.color = borderColor
                mArcPaint!!.strokeWidth = mEdgeWidth.toFloat()
                canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint!!)
            }

            val sliceColor =
                if (mSpinItemList!![i].color != 0) mSpinItemList!![i].color else defaultBackgroundColor

            if (!TextUtils.isEmpty(mSpinItemList!![i].topText)) drawTopText(
                canvas,
                tmpAngle,
                sweepAngle,
                mSpinItemList!![i].topText,
                sliceColor
            )
            if (!TextUtils.isEmpty(mSpinItemList!![i].secondaryText)) drawSecondaryText(
                canvas,
                tmpAngle,
                mSpinItemList!![i].secondaryText,
                sliceColor
            )

            if (mSpinItemList!![i].icon != 0) drawImage(
                canvas,
                tmpAngle,
                BitmapFactory.decodeResource(
                    resources,
                    mSpinItemList!![i].icon
                )
            )

            tmpAngle += sweepAngle
        }

        drawCenterImage(canvas, drawableCenterImage)
    }

    private fun drawBackgroundColor(canvas: Canvas, color: Int) {
        if (color == 0) return
        mBackgroundPaint = Paint()
        mBackgroundPaint!!.color = color
        canvas.drawCircle(
            mCenter.toFloat(),
            mCenter.toFloat(),
            (mCenter - 5).toFloat(),
            mBackgroundPaint!!
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = min(measuredWidth.toDouble(), measuredHeight.toDouble())
            .toInt()

        mPadding = if (paddingLeft == 0) 10 else paddingLeft
        mRadius = width - mPadding * 2

        mCenter = width / 2

        setMeasuredDimension(width, width)
    }

    private fun drawImage(canvas: Canvas, tmpAngle: Float, bitmap: Bitmap) {
        val imgWidth = mRadius / mSpinItemList!!.size

        val angle = ((tmpAngle + 360f / mSpinItemList!!.size / 2) * Math.PI / 180).toFloat()

        val x = (mCenter + mRadius / 2 / 2 * cos(angle.toDouble())).toInt()
        val y = (mCenter + mRadius / 2 / 2 * sin(angle.toDouble())).toInt()

        val rect = Rect(
            x - imgWidth / 2, y - imgWidth / 2,
            x + imgWidth / 2, y + imgWidth / 2
        )

        canvas.drawBitmap(bitmap, null, rect, null)
    }

    private fun drawCenterImage(canvas: Canvas, drawable: Drawable?) {
        var bitmap = drawableToBitmap(drawable!!)
        bitmap = Bitmap.createScaledBitmap(
            bitmap,
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            false
        )
        canvas.drawBitmap(
            bitmap,
            (measuredWidth / 2 - bitmap.width / 2).toFloat(),
            (measuredHeight / 2 - bitmap.height / 2).toFloat(),
            null
        )
    }

    private fun isColorDark(color: Int): Boolean {
        val colorValue = ColorUtils.calculateLuminance(color)
        val compareValue = 0.30
        return colorValue <= compareValue
    }

    private fun drawTopText(
        canvas: Canvas,
        tmpAngle: Float,
        sweepAngle: Float,
        mStr: String?,
        backgroundColor: Int
    ) {
        val path = Path()
        path.addArc(mRange, tmpAngle, sweepAngle)

        if (textColor == 0) mTextPaint!!.color =
            if (isColorDark(backgroundColor)) -0x1 else -0x1000000

        val typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        mTextPaint!!.setTypeface(typeface)
        mTextPaint!!.textAlign = Paint.Align.LEFT
        mTextPaint!!.textSize = mTopTextSize.toFloat()
        val textWidth = mTextPaint!!.measureText(mStr)
        val hOffset = (mRadius * Math.PI / mSpinItemList!!.size / 2 - textWidth / 2).toInt()

        val vOffset = mTopTextPadding

        canvas.drawTextOnPath(mStr!!, path, hOffset.toFloat(), vOffset.toFloat(), mTextPaint!!)
    }

    private fun drawSecondaryText(
        canvas: Canvas,
        tmpAngle: Float,
        mStr: String?,
        backgroundColor: Int
    ) {
        canvas.save()
        val arraySize = mSpinItemList!!.size

        if (textColor == 0) mTextPaint!!.color =
            if (isColorDark(backgroundColor)) -0x1 else -0x1000000

        val typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        mTextPaint!!.setTypeface(typeface)
        mTextPaint!!.textSize = mSecondaryTextSize.toFloat()
        mTextPaint!!.textAlign = Paint.Align.LEFT

        val textWidth = mTextPaint!!.measureText(mStr)

        val initFloat = (tmpAngle + 360f / arraySize / 2)
        val angle = (initFloat * Math.PI / 180).toFloat()

        val x = (mCenter + mRadius / 2 / 2 * cos(angle.toDouble())).toInt()
        val y = (mCenter + mRadius / 2 / 2 * sin(angle.toDouble())).toInt()

        val rect = RectF(
            x + textWidth, y.toFloat(),
            x - textWidth, y.toFloat()
        )

        val path = Path()
        path.addRect(rect, Path.Direction.CW)
        path.close()
        canvas.rotate(initFloat + (arraySize / 18f), x.toFloat(), y.toFloat())
        canvas.drawTextOnPath(
            mStr!!,
            path,
            mTopTextPadding / 7f,
            mTextPaint!!.textSize / 2.75f,
            mTextPaint!!
        )
        canvas.restore()
    }

    private fun getAngleOfIndexTarget(index: Int): Float {
        return (360f / mSpinItemList!!.size) * index
    }

    fun setRound(numberOfRound: Int) {
        mRoundOfNumber = numberOfRound
    }


    fun setPredeterminedNumber(predeterminedNumber: Int) {
        this.predeterminedNumber = predeterminedNumber
    }

    fun rotateTo(index: Int) {
        val rand = Random()
        rotateTo(index, (rand.nextInt() * 3) % 2, true)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun rotateTo(index: Int, @SpinRotation rotation: Int, startSlow: Boolean) {
        if (isRunning) {
            return
        }

        val rotationAssess = if (rotation <= 0) 1 else -1

        //If the staring position is already off 0 degrees, make an illusion that the rotation has smoothly been triggered.
        // But this inital animation will just reset the position of the circle to 0 degreees.
        if (getRotation() != 0.0f) {
            setRotation(getRotation() % 360f)
            val animationStart: TimeInterpolator =
                if (startSlow) AccelerateInterpolator() else LinearInterpolator()
            //The multiplier is to do a big rotation again if the position is already near 360.
            val multiplier = (if (getRotation() > 200f) 2 else 1).toFloat()
            animate()
                .setInterpolator(animationStart)
                .setDuration(500L)
                .setListener(object : AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        isRunning = true
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        isRunning = false
                        setRotation(0f)
                        rotateTo(index, rotation, false)
                    }

                    override fun onAnimationCancel(animation: Animator) {
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }
                })
                .rotation(360f * multiplier * rotationAssess)
                .start()
            return
        }

        // This addition of another round count for counterclockwise is to simulate the perception of the same number of spin
        // if you still need to reach the same outcome of a positive degrees rotation with the number of rounds reversed.
        if (rotationAssess < 0) mRoundOfNumber++

        val targetAngle =
            ((360f * mRoundOfNumber * rotationAssess) + 270f - getAngleOfIndexTarget(index) - ((360f / mSpinItemList!!.size) / 2))
        animate()
            .setInterpolator(DecelerateInterpolator())
            .setDuration(mRoundOfNumber * 1000 + 900L)
            .setListener(object : AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    isRunning = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    isRunning = false
                    setRotation(getRotation() % 360f)
                    if (mPieRotateListener != null) {
                        mPieRotateListener!!.rotateDone(index)
                    }
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
            .rotation(targetAngle)
            .start()
    }

    var isTouchEnabled: Boolean = true

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isRunning || !isTouchEnabled) {
            return false
        }

        val x = event.x
        val y = event.y

        val xc = width / 2.0f
        val yc = height / 2.0f

        val newFingerRotation: Double


        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                viewRotation = (rotation + 360f) % 360f
                fingerRotation = Math.toDegrees(atan2((x - xc).toDouble(), (yc - y).toDouble()))
                downPressTime = event.eventTime
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                newFingerRotation = Math.toDegrees(atan2((x - xc).toDouble(), (yc - y).toDouble()))

                if (isRotationConsistent(newFingerRotation)) {
                    rotation = newRotationValue(viewRotation, fingerRotation, newFingerRotation)
                }
                return true
            }

            MotionEvent.ACTION_UP -> {
                newFingerRotation = Math.toDegrees(atan2((x - xc).toDouble(), (yc - y).toDouble()))
                var computedRotation =
                    newRotationValue(viewRotation, fingerRotation, newFingerRotation)

                fingerRotation = newFingerRotation

                // This computes if you're holding the tap for too long
                upPressTime = event.eventTime
                if (upPressTime - downPressTime > 700L) {
                    // Disregarding the touch since the tap is too slow
                    return true
                }

                // These operators are added so that fling difference can be evaluated
                // with usually numbers that are only around more or less 100 / -100.
                if (computedRotation <= -250f) {
                    computedRotation += 360f
                } else if (computedRotation >= 250f) {
                    computedRotation -= 360f
                }

                var flingDiff = (computedRotation - viewRotation).toDouble()
                if (flingDiff >= 200 || flingDiff <= -200) {
                    if (viewRotation <= -50f) {
                        viewRotation += 360f
                    } else if (viewRotation >= 50f) {
                        viewRotation -= 360f
                    }
                }

                flingDiff = (computedRotation - viewRotation).toDouble()

                if (flingDiff <= -60 ||  //If you have a very fast flick / swipe, you an disregard the touch difference
                    (flingDiff < 0 && flingDiff >= -59 && upPressTime - downPressTime <= 200L)
                ) {
                    if (predeterminedNumber > -1) {
                        rotateTo(predeterminedNumber, SpinRotation.COUNTERCLOCKWISE, false)
                    } else {
                        rotateTo(fallBackRandomIndex, SpinRotation.COUNTERCLOCKWISE, false)
                    }
                }

                if (flingDiff >= 60 ||  //If you have a very fast flick / swipe, you an disregard the touch difference
                    (flingDiff > 0 && flingDiff <= 59 && upPressTime - downPressTime <= 200L)
                ) {
                    if (predeterminedNumber > -1) {
                        rotateTo(predeterminedNumber, SpinRotation.CLOCKWISE, false)
                    } else {
                        rotateTo(fallBackRandomIndex, SpinRotation.CLOCKWISE, false)
                    }
                }

                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun newRotationValue(
        originalWheenRotation: Float,
        originalFingerRotation: Double,
        newFingerRotation: Double
    ): Float {
        val computationalRotation = newFingerRotation - originalFingerRotation
        return (originalWheenRotation + computationalRotation.toFloat() + 360f) % 360f
    }

    private val fallBackRandomIndex: Int
        get() {
            val rand = Random()
            return rand.nextInt(mSpinItemList!!.size - 1) + 0
        }

    /**
     * This detects if your finger movement is a result of an actual raw touch event of if it's from a view jitter.
     * This uses 3 events of rotation temporary storage so that differentiation between swapping touch events can be determined.
     *
     */
    private fun isRotationConsistent(newRotValue: Double): Boolean {
        val evalValue = newRotValue

        if (java.lang.Double.compare(newRotationStore[2], newRotationStore[1]) != 0) {
            newRotationStore[2] = newRotationStore[1]
        }
        if (java.lang.Double.compare(newRotationStore[1], newRotationStore[0]) != 0) {
            newRotationStore[1] = newRotationStore[0]
        }

        newRotationStore[0] = evalValue

        if (//Is the middle event the odd one out
            java.lang.Double.compare(
                newRotationStore[2],
                newRotationStore[0]
            ) == 0 || java.lang.Double.compare(
                newRotationStore[1],
                newRotationStore[0]
            ) == 0 || java.lang.Double.compare(
                newRotationStore[2],
                newRotationStore[1]
            ) == 0 || (newRotationStore[0] > newRotationStore[1] && newRotationStore[1] < newRotationStore[2])
            || (newRotationStore[0] < newRotationStore[1] && newRotationStore[1] > newRotationStore[2])
        ) {
            return false
        }
        return true
    }


    @IntDef(
        SpinRotation.CLOCKWISE,
        SpinRotation.COUNTERCLOCKWISE
    )
    internal annotation class SpinRotation {
        companion object {
            const val CLOCKWISE: Int = 0
            const val COUNTERCLOCKWISE: Int = 1
        }
    }
}
