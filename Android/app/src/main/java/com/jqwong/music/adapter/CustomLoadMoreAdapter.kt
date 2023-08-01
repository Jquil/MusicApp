package com.jqwong.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.jqwong.music.databinding.ComponentAdapterLoadingBinding

class CustomLoadMoreAdapter : TrailingLoadStateAdapter<CustomLoadMoreAdapter.CustomVH>() {
    override fun onBindViewHolder(holder: CustomLoadMoreAdapter.CustomVH, loadState: LoadState) {
        // 根据加载状态，来自定义你的 UI 界面
        when (loadState) {
            is LoadState.NotLoading -> {
                holder.viewBinding.cpiLoading.visibility = View.GONE
            }
            is LoadState.Loading -> {
                holder.viewBinding.cpiLoading.visibility = View.VISIBLE
            }
            else -> {
                holder.viewBinding.cpiLoading.visibility = View.GONE
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): CustomLoadMoreAdapter.CustomVH {
        // 创建你自己的 UI 布局
        val viewBinding = ComponentAdapterLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomVH(viewBinding).apply {

        }
    }

    class CustomVH(val viewBinding: ComponentAdapterLoadingBinding) : RecyclerView.ViewHolder(viewBinding.root)
}