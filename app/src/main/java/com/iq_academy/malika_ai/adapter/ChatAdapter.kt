package com.iq_academy.malika_ai.adapter

import android.annotation.SuppressLint
import android.content.ClipData.Item
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iq_academy.malika_ai.databinding.ItemChatViewBinding
import com.iq_academy.malika_ai.model.room.Chat

/**
 * Rustamov Odilbek, Android developer
 * 28/03/2023  +998-91-775-17-79
 */


class ChatAdapter : ListAdapter<Chat, ChatAdapter.ItemViewHolder>(ITEM_DIF) {
    companion object {
        val ITEM_DIF = object : DiffUtil.ItemCallback<Chat>() {
            override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem.chatId == newItem.chatId
            }

            override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem.chatId == newItem.chatId
            }

        }
    }

    inner class ItemViewHolder(val bn: ItemChatViewBinding) : RecyclerView.ViewHolder(bn.root) {
        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val item = getItem(itemCount - position - 1 )
            with(bn) {
                messageTextUser.text = item.userQuestion
                messageTimeUser.text = item.userTime
                messageTextAI.text = item.aiAnswer
                messageTimeAI.text = item.aiTime

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemChatViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(position)
    }
}