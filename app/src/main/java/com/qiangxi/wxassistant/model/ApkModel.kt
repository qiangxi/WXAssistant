package com.qiangxi.wxassistant.model

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import com.qiangxi.wxassistant.entity.WXDir
import java.io.File

/**
 * Created by qiangxi(Ray) on 2018/5/30.
 *
 */
object ApkModel {

    private const val suffix1 = ".apk.1"
    private const val suffix2 = ".apk"

    private fun findWXPath(): String? {
        val file = File(Environment.getExternalStorageDirectory().absolutePath)
        val dirs = file.listFiles { _, name -> name.toLowerCase() == "tencent" }
        return if (dirs != null && dirs.isNotEmpty()) {
            dirs[0].absolutePath + "/MicroMsg/Download/"
        } else {
            null
        }
    }

    fun obtainAndParseApk(context: Context): List<WXDir> {
        val wxPath = findWXPath()
        if (TextUtils.isEmpty(wxPath)) {
            return emptyList()
        }
        val file = File(wxPath)
        val tempList = getApk(file)

        return resolveSuffixAndInfo(context, tempList)
    }

    private fun resolveSuffixAndInfo(context: Context, list: MutableList<File>): List<WXDir> {
        val results = mutableListOf<WXDir>()
        if (list.isNotEmpty()) {
            list.forEach {
                if (it.name.endsWith(suffix1)) {
                    val temp = File(it.parent + File.separator + it.nameWithoutExtension)
                    it.renameTo(temp)
                }
                val wxTemp = parseAppIconAndName(context, it.absolutePath)
                results.add(WXDir(it.name, it.lastModified(), it.absolutePath, it.length(), it, wxTemp?.appName, wxTemp?.appIcon))
            }
        }
        return results
    }

    private fun parseAppIconAndName(context: Context, absolutePath: String?): WXDir? {
        if (absolutePath == null || absolutePath.endsWith(suffix1)) {
            return null
        }
        val packageManager = context.packageManager
        val packageArchiveInfo = packageManager.getPackageArchiveInfo(absolutePath, 0)
        val applicationInfo = packageArchiveInfo.applicationInfo
        val appName = packageManager.getApplicationLabel(applicationInfo).toString()
        val appIcon = packageManager.getApplicationIcon(applicationInfo)
        return WXDir("", 0, "", 0, File(""), appName, appIcon)//just for appName and appIcon,other params are placeholder
    }

    private fun getApk(file: File): MutableList<File> {
        val apkFile = arrayListOf<File>()
        if (file.isDirectory) {
            val listFiles = file.listFiles()
            if (listFiles != null && listFiles.isNotEmpty()) {
                listFiles.forEach {
                    apkFile.addAll(getApk(it))
                }
            }
        } else {
            if (file.name.endsWith(suffix1) || file.name.endsWith(suffix2)) {
                apkFile.add(file)
            }
        }
        return apkFile
    }
}