package com.qiangxi.wxassistant.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.qiangxi.wxassistant.R
import com.qiangxi.wxassistant.adapter.ApkAdapter
import com.qiangxi.wxassistant.databinding.ActivityMainBinding
import com.qiangxi.wxassistant.databinding.DialogApkDetailBinding
import com.qiangxi.wxassistant.entity.WXDir
import com.qiangxi.wxassistant.model.ApkModel
import com.qiangxi.wxassistant.util.AppUtils
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private val mAdapter = ApkAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //config RV  adapter and listener
        val decoration = DividerItemDecoration(mBinding.apkRV.context, DividerItemDecoration.VERTICAL)
        decoration.setDrawable(resources.getDrawable(R.drawable.shape_item_decoration))
        mBinding.apkRV.addItemDecoration(decoration)
        mBinding.adapter = mAdapter

        mAdapter.setOnItemClickListener { _, _, position ->

            showDialog(mAdapter.getItem(position))
        }
        mAdapter.installListener = { file -> AppUtils.installApk(this, file) }

        //obtain apk data
        launch(UI) {
            //just for rename and flush file info, that can show correct list when first open app.
            async(CommonPool) { ApkModel.obtainAndParseApk(applicationContext) }.await()
            mAdapter.setNewData(async(context = CommonPool) { ApkModel.obtainAndParseApk(applicationContext) }.await())//set real data
        }
        mBinding.refresh.setOnRefreshListener {
            launch(UI) {
                mAdapter.setNewData(async(context = CommonPool) { ApkModel.obtainAndParseApk(applicationContext) }.await())
                mBinding.refresh.isRefreshing = false
                showToast("刷新完成")
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    private fun showDialog(wxDir: WXDir?) {
        val binding = DataBindingUtil.inflate<DialogApkDetailBinding>(LayoutInflater.from(this),
                R.layout.dialog_apk_detail, null, false)
        binding.data = wxDir

        val dialog = AlertDialog.Builder(this)
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("安装") { dialog, _ ->
                    dialog.dismiss()
                    AppUtils.installApk(this, wxDir?.file)
                }
                .setView(binding.root)
                .create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
        resizeWidthAndHeight(dialog)
    }

    /**
     * 在dialog.show()之后调用
     */
    private fun resizeWidthAndHeight(dialog: AlertDialog) {
        val window = dialog.window
        val lp = window.attributes
        lp.gravity = Gravity.CENTER
        lp.width = dp2px(280f)
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }

    private fun dp2px(dp: Float): Int {
        val displayMetrics = resources.displayMetrics
        return (dp * displayMetrics.density + 0.5).toInt()
    }

}
