package com.c1c

import android.view.View

interface ItemClickInterface<T> {
    fun onItemClick(v: View?, item: T, position: Int)
}