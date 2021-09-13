package com.example.alan.sdkdemo.util

import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.widget.*
import com.example.alan.sdkdemo.R
import com.example.alan.sdkdemo.ui.ZJConferenceActivity
import com.vcrtc.VCRTC
import com.vcrtc.widget.WhiteBoardView

/**
 * Created by ricardo
 * 9/13/21.
 */
class WhiteBoardUtil(val vcrtc: VCRTC, val context: ZJConferenceActivity) {
    private var isJoin = false
    private var isMark = true
    private var isExpand = false
    private var whiteBoardView: WhiteBoardView? = null
    private var isSelect = false
    private var currentMarkBitmap: Bitmap? = null

    // tools
    private lateinit var ivPen: ImageView
    private lateinit var ivEarse: ImageView
    private lateinit var ivClear: ImageView
    private lateinit var ivDown: ImageView

    // status
    // color
    private lateinit var ivBlack: ImageView
    private lateinit var ivGreen: ImageView
    private lateinit var ivYellow: ImageView
    private lateinit var ivOrange: ImageView
    private lateinit var ivBlue: ImageView

    // width
    private lateinit var ivThin: ImageView
    private lateinit var ivRegular: ImageView
    private lateinit var ivMedium: ImageView
    private lateinit var ivBold: ImageView

    private lateinit var ivMarkFloat: ImageView
    private lateinit var ivMarkBackground: ImageView

    private lateinit var llTools: LinearLayout
    private lateinit var llBaseTools: LinearLayout
    private lateinit var llBaseToolsBackground: LinearLayout
    private lateinit var floatParent: LinearLayout
    private lateinit var llClearTools: LinearLayout

    private lateinit var tvClearAll: TextView
    private lateinit var tvClearMine: TextView
    private lateinit var tvClearOther: TextView

    var frameBackground: FrameLayout? = null

    private var rlWhiteParent: RelativeLayout? = null
    private var currentColor = Color.BLACK
    private var currentWidth = 10
    private val COLOR = 0
    private val WIDTH = 1
    private val TOOLS = 2

    public fun initWhiteBoardView(rootView: View) {
        rootView.apply {
            rlWhiteParent = findViewById(R.id.fl_white_content)
            llTools = findViewById(R.id.ll_tools)
            ivPen = findViewById(R.id.iv_pen)
            ivEarse = findViewById(R.id.iv_earser)
            ivClear = findViewById(R.id.iv_clear)
            ivDown = findViewById(R.id.iv_down)
            ivBlack = findViewById(R.id.iv_black)
            ivYellow = findViewById(R.id.iv_yellow)
            ivOrange = findViewById(R.id.iv_orange)
            ivGreen = findViewById(R.id.iv_green)
            ivBlue = findViewById(R.id.iv_blue)
            ivThin = findViewById(R.id.iv_thin)
            ivRegular = findViewById(R.id.iv_regular)
            ivMedium = findViewById(R.id.iv_medium_bold)
            ivBold = findViewById(R.id.iv_bold)
            ivMarkFloat = findViewById(R.id.iv_mark_float)
            llBaseTools = findViewById(R.id.ll_base_tools)
            llBaseToolsBackground = findViewById(R.id.ll_base_tools_background)
            floatParent = findViewById(R.id.float_parent)
            ivMarkBackground = findViewById(R.id.iv_white_background)
            llClearTools = findViewById(R.id.ll_clear_tools)
            tvClearAll = findViewById(R.id.tv_white_clear_all)
            tvClearMine = findViewById(R.id.tv_white_clear_mine)
            tvClearOther = findViewById(R.id.tv_white_clear_other)
            frameBackground = rootView.findViewById(R.id.frame_background)
            ivPen.setOnClickListener(whiteToolsListener)
            ivEarse.setOnClickListener(whiteToolsListener)
            ivClear.setOnClickListener(whiteToolsListener)
            ivDown.setOnClickListener(whiteToolsListener)
            ivBlack.setOnClickListener(whiteToolsListener)
            ivYellow.setOnClickListener(whiteToolsListener)
            ivGreen.setOnClickListener(whiteToolsListener)
            ivBlue.setOnClickListener(whiteToolsListener)
            ivOrange.setOnClickListener(whiteToolsListener)
            ivThin.setOnClickListener(whiteToolsListener)
            ivRegular.setOnClickListener(whiteToolsListener)
            ivMedium.setOnClickListener(whiteToolsListener)
            ivBold.setOnClickListener(whiteToolsListener)
            floatParent.setOnClickListener(whiteToolsListener)
            tvClearOther.setOnClickListener(whiteToolsListener)
            tvClearMine.setOnClickListener(whiteToolsListener)
            tvClearAll.setOnClickListener(whiteToolsListener)
            ivPen.isSelected = true
            ivRegular.isSelected = true
            ivBlack.isSelected = true
        }


    }

    private val whiteToolsListener = View.OnClickListener {
        when(it.id){
            R.id.iv_pen -> {
                if (ivPen.isSelected && llTools.visibility == View.GONE) {
                    if (llClearTools.visibility == View.VISIBLE) {
                        llClearTools.visibility = View.GONE
                    } else {
                        val lp = floatParent.layoutParams as RelativeLayout.LayoutParams
                        lp.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                        lp.setMargins(floatParent.left, floatParent.top - DensityUtil.dip2px(context, 86f), 0, 0)
                        floatParent.layoutParams = lp
                    }
                    llTools.visibility = View.VISIBLE
                } else if (ivPen.isSelected && llTools.visibility == View.VISIBLE) {
                    updatePosition()
                    llTools.visibility = View.GONE
                } else {
                    refreshWhiteTools(TOOLS)
                    ivPen.isSelected = true
                    whiteBoardView?.setPenColor(currentColor)
                    whiteBoardView?.setPenWidth(currentWidth.toFloat())
                    if (llClearTools.visibility == View.VISIBLE) {
                        updatePosition()
                        llClearTools.visibility = View.GONE
                    }
                }
            }
            R.id.iv_earser -> {
                resetToolsPosition()
                refreshWhiteTools(TOOLS)
                ivEarse.isSelected = true
                whiteBoardView?.setPathEraser()
            }
            R.id.iv_clear -> {
                if (llClearTools.visibility == View.GONE) {
                    if (llTools.visibility == View.VISIBLE) {
                        llTools.visibility = View.GONE
                    } else {
                        val lp = floatParent.layoutParams as RelativeLayout.LayoutParams
                        lp.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                        lp.setMargins(floatParent.left, floatParent.top - DensityUtil.dip2px(context, 86f), 0, 0)
                        floatParent.layoutParams = lp
                    }
                    if (isJoin) {
                        tvClearOther.visibility = View.GONE
                        tvClearAll.visibility = View.GONE
                    } else {
                        tvClearOther.visibility = View.VISIBLE
                        tvClearAll.visibility = View.VISIBLE
                    }
                    llClearTools.visibility = View.VISIBLE
                } else {
                    updatePosition()
                    llClearTools.visibility = View.GONE
                }
            }
            R.id.iv_down -> {
                resetToolsPosition()
                if (whiteBoardView != null) {
                    val bitmap = com.vcrtc.utils.BitmapUtil.createBitmapFromView(frameBackground)
                    BitmapUtil.saveBitmapInDCIM(context, bitmap, System.currentTimeMillis().toString() + "" + ".jpg")
                }
            }
            R.id.iv_black -> setColor(ivBlack, "#000000")
            R.id.iv_yellow -> setColor(ivYellow, "#FFD562")
            R.id.iv_orange -> setColor(ivOrange, "#FF605C")
            R.id.iv_green -> setColor(ivGreen, "#40E3AB")
            R.id.iv_blue -> setColor(ivBlue, "#408CFF")
            R.id.iv_thin -> setWidth(ivThin, 5)
            R.id.iv_regular -> setWidth(ivRegular, 10)
            R.id.iv_medium_bold -> setWidth(ivMedium, 15)
            R.id.iv_bold -> setWidth(ivBold, 20)
            R.id.tv_white_clear_all -> {
                resetToolsPosition()
                whiteBoardView?.clear()
                vcrtc.clearWhiteboardPayload()
//                sendWhiteBoardBitmap(vcrtc)
            }
            R.id.tv_white_clear_mine -> {
                resetToolsPosition()
                whiteBoardView?.clearLocal()
            }
            R.id.tv_white_clear_other -> {
                resetToolsPosition()
                whiteBoardView?.clearPayload()
            }
        }
    }


    private fun updatePosition() {
        val lp = floatParent.layoutParams as RelativeLayout.LayoutParams
        lp.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        lp.setMargins(floatParent.left, floatParent.top + DensityUtil.dip2px(context, 86f), 0, 0)
        floatParent.layoutParams = lp
    }

    private fun resetToolsPosition() {
        if (llTools.visibility == View.VISIBLE) {
            updatePosition()
            llTools.visibility = View.GONE
        }
        if (llClearTools.visibility == View.VISIBLE) {
            updatePosition()
            llClearTools.visibility = View.GONE
        }
    }

    private fun setColor(iv: ImageView, color: String) {
        refreshWhiteTools(COLOR)
        iv.isSelected = true
        currentColor = Color.parseColor(color)
        whiteBoardView?.setPenColor(currentColor)
    }

    private fun setWidth(iv: ImageView, width: Int) {
        refreshWhiteTools(WIDTH)
        iv.isSelected = true
        currentWidth = width
        whiteBoardView?.setPenWidth(width.toFloat())
    }

    private fun refreshWhiteTools(type: Int) {
        when(type){
            COLOR -> {
                ivBlack.isSelected = false
                ivYellow.isSelected = false
                ivGreen.isSelected = false
                ivBlue.isSelected = false
                ivOrange.isSelected = false
            }
            WIDTH -> {
                ivThin.isSelected = false
                ivRegular.isSelected = false
                ivMedium.isSelected = false
                ivBold.isSelected = false
            }
            TOOLS -> {
                ivPen.isSelected = false
                ivEarse.isSelected = false
                ivClear.isSelected = false
            }
        }
    }


}