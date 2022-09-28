package com.smh_digitalassistant_library.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.smh_digitalassistant_library.R
import com.smh_digitalassistant_library.models.MessageViewModel

class ChatAdapter(private val messageList: ArrayList<MessageViewModel>): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    companion object {
        private const val BOT = 0
        private const val USER = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when(viewType) {
            BOT -> {
                LayoutInflater.from(parent.context).inflate(R.layout.card_view_bot_message, parent, false)
            }
            USER -> {
                LayoutInflater.from(parent.context).inflate(R.layout.card_view_user_message, parent, false)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messageList[position])
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].sender_id == "BOT"){
            BOT
        } else {
            USER
        }
    }

    fun add(message: MessageViewModel) {
        messageList.add(message)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.chat_message)
        private val cardView: CardView = itemView.findViewById(R.id.card_chat_message)

        fun bind(message: MessageViewModel) {
            messageTextView.text = message.message
        }
    }
}