package com.vanlam.furnitureshop.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vanlam.furnitureshop.data.Product
import com.vanlam.furnitureshop.databinding.ProductRvItemBinding

class BestProductAdapter: RecyclerView.Adapter<BestProductAdapter.BestProductViewHolder>() {
    var onClick: ((Product) -> Unit)? = null

    inner class BestProductViewHolder(private val binding: ProductRvItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindData(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgProduct)
                tvName.text = product.name
                tvPrice.text = "$ ${product.price.toString()}"
                product.offerPercentage?.let {
                    val remainingPricePercentage = 1f - product.offerPercentage
                    val priceAfterOffer = remainingPricePercentage * product.price
                    tvNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                    tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }

                if (product.offerPercentage == null) {
                    tvNewPrice.visibility = View.GONE
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductViewHolder {
        return BestProductViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BestProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bindData(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}