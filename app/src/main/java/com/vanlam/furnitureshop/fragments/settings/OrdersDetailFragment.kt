package com.vanlam.furnitureshop.fragments.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanlam.furnitureshop.R
import com.vanlam.furnitureshop.adapters.BillingProductAdapter
import com.vanlam.furnitureshop.data.OrderStatus
import com.vanlam.furnitureshop.data.getOrderStatus
import com.vanlam.furnitureshop.databinding.FragmentOrdersDetailBinding
import com.vanlam.furnitureshop.utils.VerticalItemDecoration

class OrdersDetailFragment : Fragment() {
    private lateinit var binding: FragmentOrdersDetailBinding
    private val billingProductAdapter by lazy { BillingProductAdapter() }
    private val agrs by navArgs<OrdersDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = agrs.order

        setupRcvBillingProducts()

        binding.apply {
            tvOrderId.text = "Order #${order.orderId}"

            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status,
                )
            )

            val currentStep = when (getOrderStatus(order.orderStatus)) {
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }

            stepView.go(currentStep, false)
            if (currentStep == 3) {
                stepView.done(true)
            }

            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.street}, ${order.address.state}, ${order.address.city}"
            tvPhoneNumber.text = order.address.phone

            tvTotalPrice.text = "$ ${order.totalPrice.toString()}"

            imageCloseOrder.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        billingProductAdapter.differ.submitList(order.products)
    }

    private fun setupRcvBillingProducts() {
        binding.rvProducts.apply {
            adapter = billingProductAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
}
