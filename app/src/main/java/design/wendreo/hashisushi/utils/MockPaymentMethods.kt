package design.wendreo.hashisushi.utils

import java.util.*

object MockPaymentMethods {

    val paymentMethods: List<String>
        get() {
            val list = ArrayList<String>()

            list.add("Ethereum Wallet")
            list.add("Bitcoin Wallet")
            list.add("Visa")
            list.add("Master Card")
            return list
        }
}