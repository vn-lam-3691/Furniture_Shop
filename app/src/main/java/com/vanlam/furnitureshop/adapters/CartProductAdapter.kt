package com.vanlam.furnitureshop.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vanlam.furnitureshop.data.CartProduct
import com.vanlam.furnitureshop.data.Product
import com.vanlam.furnitureshop.databinding.CartProductItemBinding
import com.vanlam.furnitureshop.helper.getProductPrice

class CartProductAdapter: RecyclerView.Adapter<CartProductAdapter.CartProductViewHolder>() {

    inner class CartProductViewHolder(val binding: CartProductItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindData(cartItem: CartProduct) {
            binding.apply {
                Glide.with(itemView).load(cartItem.product.images[0]).into(imageCartProduct)
                tvCartProductName.text = cartItem.product.name
                tvCartProductPrice.text = cartItem.product.price.toString()
                tvQuantity.text = cartItem.quantity.toString()
                tvCartProductSize.text = cartItem.selectedSize

                val priceAfterOffer = cartItem.product.offerPercentage.getProductPrice(cartItem.product.price)
                tvCartProductPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"

                imageCartProductColor.setImageDrawable(ColorDrawable(cartItem.selectedColor ?: Color.TRANSPARENT))
                tvCartProductSize.text = cartItem.selectedSize?: "".also {
                    imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder {
        return CartProductViewHolder(
            CartProductItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
        val cartItem = differ.currentList[position]
        holder.bindData(cartItem)

        holder.itemView.setOnClickListener {
            onClick?.invoke(cartItem)
        }

        holder.binding.imageIncreaseQuantity.setOnClickListener {
            onIncreaseClick?.invoke(cartItem)
        }

        holder.binding.imageDecreaseQuantity.setOnClickListener {
            onDecreaseClick?.invoke(cartItem)
        }
    }

    var onClick: ((CartProduct) -> Unit)? = null
    var onIncreaseClick: ((CartProduct) -> Unit)? = null
    var onDecreaseClick: ((CartProduct) -> Unit)? = null
}