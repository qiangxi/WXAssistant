package com.qiangxi.wxassistant.entity

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.graphics.drawable.Drawable
import com.qiangxi.wxassistant.util.AppUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by qiangxi(Ray) on 2018/5/30.
 *
 */
data class WXDir(var fileName: String,
                 @get:Bindable var time: Long,
                 var path: String,
                 @get:Bindable var size: Long,
                 var file: File,
                 var appName: String?,
                 var appIcon: Drawable?) : BaseObservable() {

    @Bindable("time")
    fun getFormatTime(): String = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA).format(time)

    @Bindable("size")
    fun getFormatSize() = "${AppUtils.formatWithCommaAndHoldDecimal((size / 1024 / 1024F).toDouble())}MB"
}