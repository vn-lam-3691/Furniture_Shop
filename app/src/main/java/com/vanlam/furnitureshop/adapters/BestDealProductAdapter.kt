package com.vanlam.furnitureshop.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vanlam.furnitureshop.data.Product
import com.vanlam.furnitureshop.databinding.BestDealsRvItemBinding

class BestDealProductAdapter: RecyclerView.Adapter<BestDealProductAdapter.BestDealProductViewHolder>() {

    inner class BestDealProductViewHolder(private val binding: BestDealsRvItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindData(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgBestDeal)
                tvDealProductName.text = product.name
                product.offerPercentage?.let {
                    val remainingPricePercentage = 1f - product.offerPercentage
                    val priceAfterOffer = remainingPricePercentage * product.price
                    tvNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                    tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
                tvOldPrice.text = "$ ${product.price.toString()}"
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealProductViewHolder {
        return BestDealProductViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestDealProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bindData(product)
    }
}