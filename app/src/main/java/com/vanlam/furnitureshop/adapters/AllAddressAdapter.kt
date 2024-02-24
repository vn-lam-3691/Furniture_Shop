package com.vanlam.furnitureshop.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vanlam.furnitureshop.R
import com.vanlam.furnitureshop.data.Address
import com.vanlam.furnitureshop.databinding.Address2RvItemBinding
import com.vanlam.furnitureshop.databinding.AddressRvItemBinding

class AllAddressAdapter: RecyclerView.Adapter<AllAddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(val binding: Address2RvItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindData(address: Address) {
            binding.apply {
                tvAddressTitle.text = address.addressTitle
                tvPhone.text = address.phone
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressTitle == newItem.addressTitle
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            Address2RvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val addressItem = differ.currentList[position]
        holder.bindData(addressItem)
        holder.itemView.setOnClickListener {
            onClick?.invoke(addressItem)
        }
    }

    var onClick: ((Address) -> Unit)? = null
}