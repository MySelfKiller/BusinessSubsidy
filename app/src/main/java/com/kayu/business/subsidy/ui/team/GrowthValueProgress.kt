package com.kayu.business.subsidy.ui.team

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class GrowthValueProgress : View {
    private var callback: MyCallback? = null
    private var context: Context
    var currentValues = 1100 //当前成长值
    private val v0Values = 0 //v0会员成长值
    private val v1Values = 100 //v1会员成长值
    private val v2Values = 10000 //v2会员成长值
    private val v3Values = 40000 //v3会员成长值
    private val v4Values = 80000 //v4会员成长值
    private var paint: Paint? = null //会员画笔
    private var grayPaint: Paint? = null
    private var pointPaint1: Paint? = null
    private var pointPaint2: Paint? = null
    private var pointPaint3: Paint? = null
    private var pointPaint4: Paint? = null
    private var lineHeight = 8 //线的高度
    private var pointSize = 8 //圆心的半径
    private var offsetX = 0 //x的坐标;
    private var width = 0
    private var hight = 0
    private var paintList: MutableList<Paint>? = null

    constructor(context: Context) : super(context) {
        this.context = context
        initPaint()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.context = context
        initPaint()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.context = context
        initPaint()
    }

    private fun initPaint() {
        lineHeight = hight / 2 //线的高度设置为布局的一半高度
        pointSize = hight / 2 //圆点的半径设置为布局的一半高度
        grayPaint = Paint()
        grayPaint!!.color = Color.GRAY
        grayPaint!!.strokeWidth = lineHeight.toFloat()
        grayPaint!!.isAntiAlias = true
        grayPaint!!.textAlign = Paint.Align.CENTER
        grayPaint!!.style = Paint.Style.STROKE
        paint = Paint()
        paint!!.color = Color.RED
        paint!!.strokeWidth = lineHeight.toFloat()
        paint!!.isAntiAlias = true
        paint!!.textAlign = Paint.Align.CENTER
        paint!!.style = Paint.Style.STROKE
        pointPaint1 = Paint()
        pointPaint2 = Paint()
        pointPaint3 = Paint()
        pointPaint4 = Paint()
        paintList = mutableListOf()
        paintList!!.add(pointPaint1!!)
        paintList!!.add(pointPaint2!!)
        paintList!!.add(pointPaint3!!)
        paintList!!.add(pointPaint4!!)
        for (i in paintList!!.indices) {
            val mPaint = paintList!![i]
            mPaint.strokeWidth = 10f
            mPaint.isAntiAlias = true
            mPaint.style = Paint.Style.FILL
            mPaint.textAlign = Paint.Align.CENTER
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val lineLength = width / 5

        //绘制底部长灰线
        canvas.drawLine(
            0f,
            lineHeight.toFloat(),
            width.toFloat(),
            lineHeight.toFloat(),
            grayPaint!!
        )
        drawProgress(canvas, lineLength)
    }

    /**
     * 画进度
     *
     * @param canvas
     * @param lineLength 每个区间的平均长度
     */
    private fun drawProgress(canvas: Canvas, lineLength: Int) {

        //在V0~V1区间内
        if (currentValues >= v0Values && currentValues < v1Values) {
            val stopX = currentValues * lineLength / (v1Values - v0Values)
            //x起始位置，y起始位置，x停止位置，y停止位置
            offsetX = stopX
            pointPaint1!!.color = Color.RED
            pointPaint2!!.color = Color.GRAY
            pointPaint3!!.color = Color.GRAY
            pointPaint4!!.color = Color.GRAY
        } else if (currentValues >= v1Values && currentValues < v2Values) {
            //在V1~V2区间内
            val stopX = (currentValues - v1Values) * lineLength / (v2Values - v1Values)
            offsetX = lineLength * 1 + stopX
            pointPaint1!!.color = Color.RED
            pointPaint2!!.color = Color.GRAY
            pointPaint3!!.color = Color.GRAY
            pointPaint4!!.color = Color.GRAY
        } else if (currentValues >= v2Values && currentValues < v3Values) {
            //在V2~V3区间内
            val stopX = (currentValues - v2Values) * lineLength / (v3Values - v2Values)
            offsetX = lineLength * 2 + stopX
            pointPaint1!!.color = Color.RED
            pointPaint2!!.color = Color.RED
            pointPaint3!!.color = Color.GRAY
            pointPaint4!!.color = Color.GRAY
        } else if (currentValues >= 0 && currentValues <= v4Values) {
            //在V3~V4区间内
            val stopX = (currentValues - v3Values) * lineLength / (v4Values - v3Values)
            offsetX = lineLength * 3 + stopX
            pointPaint1!!.color = Color.RED
            pointPaint2!!.color = Color.RED
            pointPaint3!!.color = Color.RED
            pointPaint4!!.color = Color.GRAY
        } else if (currentValues > v4Values) {
            val stopX = 10 //超过8万使用固定值
            offsetX = lineLength * 4 + stopX
            pointPaint1!!.color = Color.RED
            pointPaint2!!.color = Color.RED
            pointPaint3!!.color = Color.RED
            pointPaint4!!.color = Color.RED
        }
        canvas.drawLine(0f, lineHeight.toFloat(), offsetX.toFloat(), lineHeight.toFloat(), paint!!)


        //圆心的XY坐标，圆心半径
        canvas.drawCircle(
            (1 * lineLength - pointSize).toFloat(),
            pointSize.toFloat(),
            pointSize.toFloat(),
            pointPaint1!!
        )
        canvas.drawCircle(
            (2 * lineLength - pointSize).toFloat(),
            pointSize.toFloat(),
            pointSize.toFloat(),
            pointPaint2!!
        )
        canvas.drawCircle(
            (3 * lineLength - pointSize).toFloat(),
            pointSize.toFloat(),
            pointSize.toFloat(),
            pointPaint3!!
        )
        canvas.drawCircle(
            (4 * lineLength - pointSize).toFloat(),
            pointSize.toFloat(),
            pointSize.toFloat(),
            pointPaint4!!
        )
        if (callback != null) {
            callback!!.callBack(offsetX, currentValues)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        width = this.measuredWidth
        hight = this.measuredHeight
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    fun getOffsetX(callback: MyCallback?) {
        this.callback = callback
    }

    interface MyCallback {
        fun callBack(offsetX: Int, currentValues: Int)
    }
}