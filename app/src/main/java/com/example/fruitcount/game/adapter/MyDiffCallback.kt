package com.example.fruitcount.game.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.fruitcount.game.data.Item

object MyDiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(
        oldItem: Item,
        newItem: Item
    ): Boolean {
        return oldItem.imageList == newItem.imageList
    }

    override fun areContentsTheSame(
        oldItem: Item,
        newItem: Item
    ): Boolean {
        return oldItem == newItem
    }

}