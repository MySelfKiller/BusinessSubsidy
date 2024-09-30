package com.hoom.library.base.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.v3.MessageDialog

object CallPhoneUtil {

    /**
     * 拨打电话（直接拨打电话）
     *
     * @param phoneNum 电话号码
     */
    fun callPhone(context: Activity, phoneNum: String) {
        MessageDialog.show((context as AppCompatActivity), "拨打电话", phoneNum, "呼叫", "取消")
            .setOkButton(
                OnDialogButtonClickListener { baseDialog, v ->
                    val intent = Intent(Intent.ACTION_CALL)
                    val data = Uri.parse("tel:$phoneNum")
                    intent.data = data
                    context.startActivity(intent)
                    false
                })
    }
}