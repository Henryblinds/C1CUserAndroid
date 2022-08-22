package com.c1c

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class GenericAdapter<T>(items: List<T>, layoutId: Int, br: Int, ls: ItemClickInterface<T>) : RecyclerView.Adapter<GenericAdapter<T>.BindingViewHolder>() {

    var layId: Int = layoutId
    var itemList: List<T> = items
    var brId: Int = br
    var listener: ItemClickInterface<T> = ls

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(layId, parent, false)
        return BindingViewHolder(v)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        val item: T = itemList[position]
        holder.getBinding()!!.setVariable(brId, item)
        holder.getBinding()!!.executePendingBindings()
    }

    inner class BindingViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private val binding: ViewDataBinding? = DataBindingUtil.bind(v)

        init {
            v.setOnClickListener(this)
        }

        fun getBinding() : ViewDataBinding? {
            return this.binding
        }

        override fun onClick(p0: View?) {
            val pos = layoutPosition
            val item = itemList[pos]
            listener.onItemClick(p0,item,pos)
        }

    }

    fun notifyDataSetChanged(item: List<T>) {
        this.itemList = item
        notifyDataSetChanged()
    }
}