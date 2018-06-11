package com.qiangxi.wxassistant.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * Created by niko on 15/9/2017.
 */
object AppUtils {

    /**
     * 安装apk
     */
    fun installApk(context: Context, file: File?) {
        if (file == null) {
            Toast.makeText(context, "未找到apk文件", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = Intent.ACTION_VIEW
        val uri = Uri.fromFile(file)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }


    /**
     * 使用逗号格式化数字，保留小数位后的0
     */
    fun formatWithCommaAndHoldDecimal(number: Double): String {
        val decimalFormat = DecimalFormat("###,##0.00")
        decimalFormat.roundingMode = RoundingMode.DOWN
        return decimalFormat.format(number)
    }

}
