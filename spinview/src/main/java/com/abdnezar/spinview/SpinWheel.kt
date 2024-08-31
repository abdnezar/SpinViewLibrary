package com.abdnezar.spinview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.abdnezar.spinview.model.SpinItem
import com.abdnezar.spinview.utils.Utils.convertDpToPixel
import com.abdnezar.spinview.views.Spin
import com.abdnezar.spinview.views.Spin.PieRotateListener
import java.util.Random

class SpinWheel : RelativeLayout, PieRotateListener {
    private var mBackgroundColor = 0
    private var mTextColor = 0
    private var mTopTextSize = 0
    private var mSecondaryTextSize = 0
    private var mBorderColor = 0
    private var mTopTextPadding = 0
    private var mEdgeWidth = 0
    private var mCenterImage: Drawable? = null
    private var mCursorImage: Drawable? = null

    private lateinit var spin: Spin
    private var ivCursorView: ImageView? = null

    private lateinit var spinWheelRoundItemSelectedListener: ((Int) -> Unit)

    override fun rotateDone(index: Int) {
        spinWheelRoundItemSelectedListener(index)
    }

    fun setSpinWheelRoundItemSelectedListener(listener: (Int) -> Unit) {
        this.spinWheelRoundItemSelectedListener = listener
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(ctx: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.SpinWheelView)
            mBackgroundColor =
                typedArray.getColor(R.styleable.SpinWheelView_spnwBackgroundColor, -0x340000)
            mTopTextSize = typedArray.getDimensionPixelSize(
                R.styleable.SpinWheelView_spnwTopTextSize,
                convertDpToPixel(15f, context).toInt()
            )
            mSecondaryTextSize = typedArray.getDimensionPixelSize(
                R.styleable.SpinWheelView_spnwSecondaryTextSize,
                convertDpToPixel(20f, context).toInt()
            )
            mTextColor = typedArray.getColor(R.styleable.SpinWheelView_spnwTopTextColor, 0)
            mTopTextPadding = typedArray.getDimensionPixelSize(
                R.styleable.SpinWheelView_spnwTopTextPadding,
                convertDpToPixel(25f, context).toInt()
            ) + convertDpToPixel(10f, context).toInt()
            mCursorImage = typedArray.getDrawable(R.styleable.SpinWheelView_spnwCursor)
            mCenterImage = typedArray.getDrawable(R.styleable.SpinWheelView_spnwCenterImage)
            mEdgeWidth = typedArray.getInt(R.styleable.SpinWheelView_spnwEdgeWidth, 10)
            mBorderColor = typedArray.getColor(R.styleable.SpinWheelView_spnwEdgeColor, 0)
            typedArray.recycle()
        }

        val inflater = LayoutInflater.from(context)
        val frameLayout = inflater.inflate(R.layout.layout_spinwheel, this, false) as FrameLayout

        spin = frameLayout.findViewById(R.id.spinView)
        ivCursorView = frameLayout.findViewById(R.id.cursorView)

        spin.setPieRotateListener(this)
        spin.setPieBackgroundColor(mBackgroundColor)
        spin.setTopTextPadding(mTopTextPadding)
        spin.setTopTextSize(mTopTextSize)
        spin.setSecondaryTextSizeSize(mSecondaryTextSize)
        spin.setPieCenterImage(mCenterImage)
        spin.setBorderColor(mBorderColor)
        spin.setBorderWidth(mEdgeWidth)


        if (mTextColor != 0) spin.setPieTextColor(mTextColor)

        ivCursorView?.setImageDrawable(mCursorImage)

        addView(frameLayout)
    }


    var isTouchEnabled: Boolean
        get() = spin.isTouchEnabled
        set(touchEnabled) {
            spin.isTouchEnabled = touchEnabled
        }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        //This is to control that the touch events triggered are only going to the PieView
        for (i in 0 until childCount) {
            if (isPielView(getChildAt(i))) {
                return super.dispatchTouchEvent(ev)
            }
        }
        return false
    }

    private fun isPielView(view: View): Boolean {
        if (view is ViewGroup) {
            for (i in 0 until childCount) {
                if (isPielView(view.getChildAt(i))) {
                    return true
                }
            }
        }
        return view is Spin
    }

    fun setSpinWheelBackgrouldColor(color: Int) {
        spin.setPieBackgroundColor(color)
    }

    fun setSpinWheelCursorImage(drawable: Int) {
        ivCursorView!!.setBackgroundResource(drawable)
    }

    fun setSpinWheelCenterImage(drawable: Drawable?) {
        spin.setPieCenterImage(drawable)
    }

    fun setBorderColor(color: Int) {
        spin.setBorderColor(color)
    }

    fun setSpinWheelTextColor(color: Int) {
        spin.setPieTextColor(color)
    }

    fun setData(data: List<SpinItem>?) {
        spin.setData(data)
    }

    fun setRound(numberOfRound: Int) {
        spin.setRound(numberOfRound)
    }

    fun setPredeterminedNumber(fixedNumber: Int) {
        spin.setPredeterminedNumber(fixedNumber)
    }

    fun startSpinWheelWithTargetIndex(index: Int) {
        spin.rotateTo(index)
    }

    fun startSpinWheelWithRandomTarget() {
        val r = Random()
        spin.rotateTo(r.nextInt(spin.spinItemListSize - 1))
    }
}
