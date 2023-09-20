package com.jqwong.music.adapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.dragswipe.listener.DragAndSwipeDataCallback
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputLayout
import com.jqwong.music.R
import com.jqwong.music.model.ChangePlatformItem
import com.jqwong.music.model.ChangePlatformMode

class PlatformPriorityAdapter: BaseQuickAdapter<ChangePlatformItem, QuickViewHolder>(),
    DragAndSwipeDataCallback {

    override fun onBindViewHolder(
        holder: QuickViewHolder,
        position: Int,
        item: ChangePlatformItem?
    ) {
        val tv = holder.getView<TextView>(R.id.tv_name)
        val switch = holder.getView<SwitchMaterial>(R.id.sm_enable)
        val menu = holder.getView<TextInputLayout>(R.id.menu_mode)
        tv.text = item?.platform?.toString()
        switch.isChecked = item!!.enable
        val list = mutableListOf<String>()
        ChangePlatformMode.values().forEach {
            list.add(it.toString())
        }
        (menu.editText as? AutoCompleteTextView)?.let{
            it.setDropDownBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this.context,R.color.dropdown_menu_background)))
            it.setAdapter(ArrayAdapter(this.context,R.layout.item_drop_down_text, list))
            it.hint = item.mode.toString()
        }
        menu.editText?.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var mode:ChangePlatformMode? = null
                ChangePlatformMode.values().forEach {
                    if(it.toString() == p0.toString()){
                        mode = it
                        return@forEach
                    }
                }
                if(mode != null){
                    item.mode = mode!!
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        switch.setOnCheckedChangeListener { compoundButton, b ->
            item.enable = b
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_platform_priority,parent)
    }

    override fun dataRemoveAt(position: Int) {
    }

    override fun dataSwap(fromPosition: Int, toPosition: Int) {
        swap(fromPosition, toPosition)
    }
}