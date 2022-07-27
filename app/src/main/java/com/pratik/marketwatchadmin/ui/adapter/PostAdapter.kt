package com.pratik.marketwatchadmin.ui.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pratik.marketwatchadmin.R
import com.pratik.marketwatchadmin.data.ResponseDataItem
import com.pratik.marketwatchadmin.databinding.ItemListsBinding
import com.pratik.marketwatchadmin.ui.MainActivity


class PostAdapter(var context: Context, var list: ArrayList<ResponseDataItem>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    class ViewHolder(var binding: ItemListsBinding) : RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = ItemListsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvStockname.setText(list[position].storename)
        holder.binding.tvBuyprice.setText("BUY @" + list.get(position).buyprice)
        if (list.get(position).stoploss.isEmpty()) {
            holder.binding.tvStoploss.setText("STOPLOSS " + "-")
        } else {
            holder.binding.tvStoploss.setText("STOPLOSS " + list.get(position).stoploss)
        }

        if (list.get(position).target1.isEmpty()) {
            holder.binding.tvTargetOne.setText("-")
        } else {
            holder.binding.tvTargetOne.setText("@" + list.get(position).target1)
        }

        if (list.get(position).target2.isEmpty()) {
            holder.binding.tvTargetTwo.setText("-")
        } else {
            holder.binding.tvTargetTwo.setText("@" + list.get(position).target2)
        }

        if (list.get(position).target3.isEmpty()) {
            holder.binding.tvTargetThree.setText("-")
        } else {
            holder.binding.tvTargetThree.setText("@" + list.get(position).target3)
        }


        if (list.get(position).type == "buy") {
            holder.binding.tvCall.setText("BUY CALL")
        } else {
            holder.binding.tvCall.setText("SELL CALL")
        }

        holder.binding.tvPosteddate.setText(list.get(position).create_date)
        holder.binding.tvStatus.setText(list[position].duration)
        if (list.get(position).duration.contains("Hit")) {
            holder.binding.tvStatus.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    android.R.color.holo_red_dark
                )
            )
        } else if (list.get(position).duration.contains("days")) {
            holder.binding.tvStatus.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.light_gray
                )
            )
        } else {
            holder.binding.tvStatus.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    android.R.color.holo_green_dark
                )
            )
        }

        holder.binding.btnEdit.setOnClickListener {
            (context as MainActivity).onClickEventEdit(position)
        }
        holder.binding.btnRemove.setOnClickListener {
            (context as MainActivity).onClickEventRemove(position)
        }
        holder.binding.ivShare.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                var msg: String = list.get(position).storename
                if (!list.get(position).buyprice.isEmpty()) {
                    msg += " Buy@" + list.get(position).buyprice.toString() + ""
                }
                if (!list.get(position).stoploss.isEmpty() || list.get(
                        position
                    ).stoploss.isEmpty()
                ) {
                    msg += " stoploss@" + list.get(position).stoploss
                        .toString() + " "
                }
                if (!list.get(position).target1.isEmpty() || !list.get(
                        position
                    ).target1.contains("-")
                ) {
                    msg += " target1@" + list.get(position).target1
                        .toString() + " "
                }
                if (!list.get(position).target2.isEmpty() || !list.get(
                        position
                    ).target2.contains("-")
                ) {
                    msg += " target2@" + list.get(position).target2
                        .toString() + " "
                }
                if (!list.get(position).target3.isEmpty() || !list.get(
                        position
                    ).target3.contains("-")
                ) {
                    msg += " target3@" + list.get(position).target3
                        .toString() + " "
                }
                msg += " for get updated in stock market download our app https://play.google.com/store/apps/details?id=marketwatch.com.app.marketwatch"
                Log.d("msg", msg!!)
                val bundle = Bundle()
                bundle.putString("share", list.get(position).storename)

                getBitmapFromView(msg)
            }
        })


    }

    fun getBitmapFromView(txt: String?) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, txt)
        sendIntent.type = "text/plain"
        context.startActivity(sendIntent)
    }

    override fun getItemCount(): Int {
        return list.size;
    }
}