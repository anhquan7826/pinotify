package com.anhquan.pinotify.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.anhquan.pinotify.R
import com.anhquan.pinotify.databinding.CommonStatusViewBinding

class StatusView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private var binding: CommonStatusViewBinding =
        CommonStatusViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.StatusView)
        val status = attributes.getInt(R.styleable.StatusView_status, 0) == 0
        val text = attributes.getString(R.styleable.StatusView_text)

        binding.commonStatusText.apply {
            this.text = text
            compoundDrawables.forEach {
                it?.setTint(context.getColor(if (status) R.color.passed else R.color.failed))
            }
        }

        attributes.recycle()
    }
}