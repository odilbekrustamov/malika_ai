package com.iq_academy.malika_ai.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iq_academy.malika_ai.databinding.ItemChatViewBinding
import com.iq_academy.malika_ai.model.room.Chat

class ChatAdapter : ListAdapter<Chat, ChatAdapter.ItemViewHolder>(ITEM_DIF) {
    companion object {
        val ITEM_DIF = object : DiffUtil.ItemCallback<Chat>() {
            override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class ItemViewHolder(val bn: ItemChatViewBinding) : RecyclerView.ViewHolder(bn.root) {
        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val item = getItem(itemCount - position - 1)
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