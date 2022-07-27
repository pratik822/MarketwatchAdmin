package com.pratik.marketwatchadmin.ui


import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.pratik.marketwatchadmin.GlobalUpdate
import com.pratik.marketwatchadmin.data.AddPostData
import com.pratik.marketwatchadmin.data.Req
import com.pratik.marketwatchadmin.data.ResponseDataItem
import com.pratik.marketwatchadmin.databinding.ActivityMainBinding
import com.pratik.marketwatchadmin.network.RetrofitClient
import com.pratik.marketwatchadmin.ui.adapter.PostAdapter
import kotlinx.coroutines.*
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

        registerForContextMenu(binding.rvList);


// See documentation on defining a message payload.






        binding.fabAdd.setOnClickListener { view ->
            startActivity(Intent(this, AddPost::class.java))

        }
        binding.fabNoti.setOnClickListener { view ->
            startActivity(Intent(this, GlobalUpdate::class.java))

        }
    }

    suspend fun apiCall(){
        delay(3000L)
        println("activitys"+"Image Downloads  "+Thread.currentThread().name)

    }



    public fun onClickEventEdit(postion: Int) {
        Toast.makeText(this, "hi" + postion, Toast.LENGTH_LONG).show()
        try {
            val responseDataItem = AddPostData(
                responselist?.get(postion)?.id.toString(),
                responselist?.get(postion)?.buyprice.toString(),
                responselist?.get(postion)?.stoploss.toString(),
                responselist?.get(postion)?.storename.toString(),
                responselist?.get(postion)?.target1.toString(),
                responselist?.get(postion)?.target2.toString(),
                responselist?.get(postion)?.target3.toString(),
                responselist?.get(postion)?.type.toString(),
                responselist?.get(postion)?.duration.toString()
            );

            Intent(this@MainActivity, AddPost::class.java).let {
                it.putExtra("editData", Gson().toJson(responseDataItem))
                startActivity(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public fun onClickEventRemove(postion: Int) {
        Toast.makeText(this, "hi1" + postion, Toast.LENGTH_LONG).show()
        try {
            RetrofitClient.getRetrofitInstance()
                .deleteCreatePost(responselist?.get(postion)?.id.toString())
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
                        responselist?.removeAt(postion)
                        adapter.notifyDataSetChanged();
                    }

                    override fun onFailure(call: Call<Req?>, t: Throwable) {

                    }

                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadpost() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                responselist = RetrofitClient.getRetrofitInstance().getPost().body()
                if(responselist!=null){
                    adapter = PostAdapter(this@MainActivity, responselist!!);
                    binding.rvList.adapter = adapter;
                }
            }catch (e:Exception){
                e.printStackTrace()
            }


        }
    }

    override fun onResume() {
        super.onResume()
         loadpost()
    }


}

