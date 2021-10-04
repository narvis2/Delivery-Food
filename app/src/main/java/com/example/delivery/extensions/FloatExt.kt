package com.example.delivery.extensions

import android.content.res.Resources

// Float 를 픽셀값으로 변환
fun Float.fromDpToPx() : Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}