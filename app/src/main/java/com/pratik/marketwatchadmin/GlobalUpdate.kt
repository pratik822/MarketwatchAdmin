package com.pratik.marketwatchadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.pratik.marketwatchadmin.data.*
import com.pratik.marketwatchadmin.databinding.ActivityGlobalUpdateBinding
import com.pratik.marketwatchadmin.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class GlobalUpdate : AppCompatActivity() {
    lateinit var binding: ActivityGlobalUpdateBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityGlobalUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.t1.setOnClickListener {
            binding.edtNotification.setText("");
            binding.edtNotification.append(binding.t1.text.toString())
        }
        binding.t2.setOnClickListener {
            binding.edtNotification.setText("");
            binding.edtNotification.append(binding.t2.text.toString())
        }
        binding.t3.setOnClickListener {
            binding.edtNotification.setText("");
            binding.edtNotification.append(binding.t3.text.toString())
        }
        binding.t4.setOnClickListener {
            binding.edtNotification.setText("");
            binding.edtNotification.append(binding.t4.text.toString())
        }

        binding.t5.setOnClickListener {
            binding.edtNotification.setText("");
            binding.edtNotification.append(binding.t5.text.toString())
        }
        binding.t6.setOnClickListener {
            binding.edtNotification.setText("");
            binding.edtNotification.append(binding.t6.text.toString())
        }

        binding.t7.setOnClickListener {
            binding.edtNotification.setText("");
            binding.edtNotification.append(binding.t7.text.toString())
        }

        binding.btnNotify.setOnClickListener {
            if (!binding.edtNotification.text.toString().isEmpty()) {
                RetrofitClient.getRetrofitInstance().createPostNotification(AddPostNotification(binding.edtNotification.text.toString())).enqueue(object:
                    Callback<Req>{
                    override fun onResponse(call: Call<Req>, response: Response<Req>) {
                        Toast.makeText(
                            this@GlobalUpdate,
                            response.body()?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onFailure(call: Call<Req>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                })

                lifecycleScope.launch(Dispatchers.IO) {
                   notification(binding.edtNotification.text.toString())
                }


            } else {
                Toast.makeText(this, "Textfield cannot be blank", Toast.LENGTH_LONG).show();
            }
        }

        binding.imageView.setOnClickListener {

            share(binding.edtNotification.text.toString())
        }

    }
    fun share(txt: String?) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND

        sendIntent.putExtra(Intent.EXTRA_TEXT, txt+" for get updated in stock market download our app https://play.google.com/store/apps/details?id=marketwatch.com.app.marketwatch")
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }
    suspend fun notification(msg: String) {
        val url = URL("https://fcm.googleapis.com/fcm/send")
        val http = url.openConnection() as HttpURLConnection
        http.requestMethod = "POST"
        http.doOutput = true
        http.setRequestProperty("Content-type", "application/json")
        http.setRequestProperty(
            "Authorization",
            "key=AAAAOarZyBE:APA91bErpioh26b9S7lQ3zOLgKn5ErfNyq90kX4BMw7ZFN8TcDdSgZ_zppwynLY39cRMQ3HAspRcO2jkkmxBJZk-CkDHKJ974OXnwxmGq9-1hfaBX1MhMMj2wk2eqXz0ylUWklDHoWHi"
        )

            val data = Data(msg, "");
            val dataReq = dataReq("/topics/market", data);
            val out = Gson().toJson(dataReq).toString().toByteArray(StandardCharsets.UTF_8)
            val stream = http.outputStream
            stream.write(out)
           println("bbbb--"+Gson().toJson(dataReq))

        println("aaaaa" + http.responseCode.toString() + " " + http.responseMessage)
        http.disconnect()
        if(http.responseMessage=="OK"){
            notificationNew(msg)
        }

    }
    suspend fun notificationNew(msg: String) {
        val url = URL("https://fcm.googleapis.com/fcm/send")
        val http = url.openConnection() as HttpURLConnection
        http.requestMethod = "POST"
        http.doOutput = true
        http.setRequestProperty("Content-type", "application/json")
        http.setRequestProperty(
            "Authorization",
            "key=AAAAOarZyBE:APA91bErpioh26b9S7lQ3zOLgKn5ErfNyq90kX4BMw7ZFN8TcDdSgZ_zppwynLY39cRMQ3HAspRcO2jkkmxBJZk-CkDHKJ974OXnwxmGq9-1hfaBX1MhMMj2wk2eqXz0ylUWklDHoWHi"
        )

        val data = Data(msg, "");
        val dataReq = dataReqnew("/topics/market-test", data);
        val out = Gson().toJson(dataReq).toString().toByteArray(StandardCharsets.UTF_8)
        val stream = http.outputStream
        stream.write(out)
        println("bbbb"+Gson().toJson(dataReq))

//
        println("aaaaa" + http.responseCode.toString() + " " + http.responseMessage)
        http.disconnect()
        if(http.responseMessage=="OK"){
            withContext(Dispatchers.Main){
                Toast.makeText(this@GlobalUpdate,"Done!",Toast.LENGTH_LONG).show()
            }

        }
//
    }
}