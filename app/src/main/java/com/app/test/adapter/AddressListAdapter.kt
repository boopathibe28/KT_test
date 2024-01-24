package com.app.test.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.test.R
import com.app.test.adapter.AddressListAdapter.MyViewHolder
import com.app.test.app_interfaces.ListOnclick
import com.app.test.utils.CartRealmModel
import io.realm.RealmResults

class AddressListAdapter(
    private val context: Context,
    private val cartList: RealmResults<CartRealmModel>,
    private val onclick: ListOnclick
) : RecyclerView.Adapter<MyViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): MyViewHolder {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = layoutInflater.inflate(R.layout.adapter_add_list, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(myViewHolder: MyViewHolder, position: Int) {
        val values = cartList[position]
        myViewHolder.txtName.text = values!!.emp_name
        myViewHolder.txtLatLong.text = """${values.emp_roll} / ${values.emp_details}
${values.item_id}"""
        myViewHolder.lyoutParent.setOnClickListener {
            onclick.onClick(
                values.emp_roll,
                values.emp_details,
                values.emp_name,
                values.item_id
            )
        }
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtName: TextView
        var txtLatLong: TextView
        var lyoutParent: LinearLayout

        init {
            lyoutParent = itemView.findViewById(R.id.lyoutParent)
            txtName = itemView.findViewById(R.id.txtName)
            txtLatLong = itemView.findViewById(R.id.txtLatLong)
        }
    }
}