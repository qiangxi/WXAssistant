package com.qiangxi.wxassistant.adapter

import android.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qiangxi.wxassistant.R
import com.qiangxi.wxassistant.databinding.ItemWxApkBinding
import com.qiangxi.wxassistant.entity.WXDir
import java.io.File

/**
 * Created by qiangxi(Ray) on 2018/5/30.
 *
 */
class ApkAdapter : BaseQuickAdapter<WXDir, BaseViewHolder>(R.layout.item_wx_apk) {
    var installListener: ((file: File?) -> Unit)? = null

    override fun convert(helper: BaseViewHolder?, item: WXDir?) {
        val binding: ItemWxApkBinding = DataBindingUtil.bind(helper?.itemView)
        binding.wx = item
        binding.executePendingBindings()
        binding.install.setOnClickListener {
            installListener?.invoke(item?.file)
        }
    }
}