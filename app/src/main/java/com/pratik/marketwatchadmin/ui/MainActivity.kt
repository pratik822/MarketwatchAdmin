package com.pratik.marketwatchadmin.ui

import android.R.attr
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.pratik.marketwatchadmin.data.ResponseDataItem
import com.pratik.marketwatchadmin.ui.adapter.PostAdapter
import com.pratik.marketwatchadmin.databinding.ActivityMainBinding
import com.pratik.marketwatchadmin.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.pratik.marketwatchadmin.R

import android.graphics.Color
import android.util.Log

import android.view.View

import android.R.attr.direction
import android.content.Intent
import android.os.Parcelable
import android.text.Html
import android.widget.Toast

import com.tutorialsbuzz.halfswipe.SwipeHelper.UnderlayButtonClickListener


import androidx.appcompat.content.res.AppCompatResources
import com.google.firebase.messaging.RemoteMessage
import com.pratik.marketwatchadmin.data.AddPostData
import com.pratik.marketwatchadmin.data.Req

import com.tutorialsbuzz.halfswipe.SwipeHelper.UnderlayButton

import com.tutorialsbuzz.halfswipe.SwipeHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var linearLayoutManager: LinearLayoutManager
    var responselist: ArrayList<ResponseDataItem>? = null
    lateinit var adapter: PostAdapter;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        linearLayoutManager = LinearLayoutManager(this);


        binding.swipeRefresh.setOnRefreshListener {

            // See documentation on defining a message payload.
            // See documentation on defining a message payload.
// See documentation on defining a message payload.
            // See documentation on defining a message payload.
//            val messages = Message.builder()
//                .putData("score", "850")
//                .putData("time", "2:45")
//                .setTopic("market")
//                .build()
//            val response = FirebaseMessaging.getInstance().send(messages)
// Response is a message ID string.
// Response is a message ID string.
            //  println("Successfully sent message: $response")

            loadpost()
            binding.swipeRefresh.isRefreshing = false
        }
        binding.rvList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.rvList.layoutManager = linearLayoutManager
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = Html.fromHtml("<font color='#ffffff'>Market Watch</font>")


        val topic = "highScores"

// See documentation on defining a message payload.



        object : SwipeHelper(this, binding.rvList, true) {
            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder,
                underlayButtons: MutableList<UnderlayButton>
            ) {

                underlayButtons.add(UnderlayButton(
                    "Remove",
                    AppCompatResources.getDrawable(
                        applicationContext,
                        R.drawable.icons_remove
                    ),
                    Color.parseColor("#00FF00"), Color.parseColor("#ffffff")
                ) {
                    RetrofitClient.getRetrofitInstance()
                        .deleteCreatePost(responselist?.get(viewHolder.adapterPosition)?.id.toString())
                        .enqueue(object : Callback<Req?> {
                            override fun onResponse(
                                call: Call<Req?>,
                                response: Response<Req?>
                            ) {
                                Toast.makeText(
                                    this@MainActivity,
                                    response.body()?.message,
                                    Toast.LENGTH_LONG
                                ).show()
                                responselist?.removeAt(viewHolder.adapterPosition)
                                adapter.notifyDataSetChanged();
                            }

                            override fun onFailure(call: Call<Req?>, t: Throwable) {

                            }

                        })
                })
                underlayButtons.add(UnderlayButton(
                    "Edit",
                    AppCompatResources.getDrawable(
                        applicationContext,
                        R.drawable.icons_edit1
                    ),
                    Color.parseColor("#FF0000"), Color.parseColor("#ffffff")
                ) {
                    val responseDataItem = AddPostData(
                        responselist?.get(viewHolder.adapterPosition)?.id.toString(),
                        responselist?.get(viewHolder.adapterPosition)?.buyprice.toString(),
                        responselist?.get(viewHolder.adapterPosition)?.stoploss.toString(),
                        responselist?.get(viewHolder.adapterPosition)?.storename.toString(),
                        responselist?.get(viewHolder.adapterPosition)?.target1.toString(),
                        responselist?.get(viewHolder.adapterPosition)?.target2.toString(),
                        responselist?.get(viewHolder.adapterPosition)?.target3.toString(),
                        responselist?.get(viewHolder.adapterPosition)?.type.toString(),
                        responselist?.get(viewHolder.adapterPosition)?.duration.toString()
                    );

                    Intent(this@MainActivity, AddPost::class.java).let {
                        it.putExtra("editData", Gson().toJson(responseDataItem))
                        startActivity(it)
                    }


                })
            }
        }


        GlobalScope.launch(Dispatchers.Main) {
            responselist = RetrofitClient.getRetrofitInstance().getPost().body()
            adapter = PostAdapter(this@MainActivity, responselist!!);
            binding.rvList.adapter = adapter;


        }



        binding.fabAdd.setOnClickListener { view ->
            startActivity(Intent(this, AddPost::class.java))

        }
    }

    private fun loadpost() {
        GlobalScope.launch(Dispatchers.Main) {
            responselist = RetrofitClient.getRetrofitInstance().getPost().body()
            adapter = PostAdapter(this@MainActivity, responselist!!);
            binding.rvList.adapter = adapter;
        }
    }

    override fun onResume() {
        super.onResume()
        loadpost()
    }


}

