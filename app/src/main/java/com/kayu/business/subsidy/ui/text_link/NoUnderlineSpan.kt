package com.kayu.business.subsidy.ui.text_link

import android.text.TextPaint
import android.text.style.UnderlineSpan

class NoUnderlineSpan : UnderlineSpan() {
    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = false
    }
}