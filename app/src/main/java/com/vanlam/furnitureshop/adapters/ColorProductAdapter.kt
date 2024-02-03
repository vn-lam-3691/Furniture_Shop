package com.vanlam.furnitureshop.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.vanlam.furnitureshop.databinding.ColorRvItemBinding

class ColorProductAdapter: RecyclerView.Adapter<ColorProductAdapter.ColorProductViewHolder>() {
    private var selectedPosition = -1

    inner class ColorProductViewHolder(val binding: ColorRvItemBinding): ViewHolder(binding.root) {
        fun bind(colorCode: Int, position: Int) {
            val imageDrawable = ColorDrawable(colorCode)
            binding.imageColor.setImageDrawable(imageDrawable)

            if (selectedPosition == position) {  // Color is selected
                binding.apply {
                    imageShadow.visibility = View.VISIBLE
                    imagePicker.visibility = View.VISIBLE
                }
            }
            else {  // Color isn't selected
                binding.apply {
                    imageShadow.visibility = View.INVISIBLE
                    imagePicker.visibility = View.INVISIBLE
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorProductViewHolder {
        return ColorProductViewHolder(
            ColorRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ColorProductViewHolder, position: Int) {
        val colorCode = differ.currentList[position]
        holder.bind(colorCode, position)

        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0) {
                notifyItemChanged(selectedPosition)
            }

            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClick?.invoke(colorCode)
        }
    }

    var onItemClick: ((Int) -> Unit)? = null
}