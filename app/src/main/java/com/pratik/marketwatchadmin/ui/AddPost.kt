package com.pratik.marketwatchadmin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.api.AnnotationsProto.http


import com.google.gson.Gson
import com.google.gson.JsonObject
import com.pratik.marketwatchadmin.R
import com.pratik.marketwatchadmin.data.*
import com.pratik.marketwatchadmin.databinding.ActivityAddPostBinding
import com.pratik.marketwatchadmin.network.RetrofitClient
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.OutputStream

import java.nio.charset.StandardCharsets

import java.net.HttpURLConnection

import java.net.URL
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AddPost : AppCompatActivity() {
    lateinit var binding:ActivityAddPostBinding;
     var durationList:List<String> = listOf("IntraDay","1 to 2 days","2 to 4 days","6 to 8 days")
     lateinit var adapter:ArrayAdapter<String>;
     var type:String="buy";
      var id:String="0";



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        binding= ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,durationList);
        binding.spinnerType.adapter=adapter

        binding.btnNotify.setOnClickListener {
            if(!binding.edtNotification.text.toString().isEmpty()){
                notification(binding.edtNotification.text.toString())
            }else{
                Toast.makeText(this,"Textfield cannot be blank",Toast.LENGTH_LONG).show();
            }
        }



        if(intent.extras!=null){
            var editData: String? =intent.getStringExtra("editData");
            var jsonObject= JSONObject(editData);
            binding.edtTarget1.setText(jsonObject.getString("target1"));
            binding.edtTarget2.setText(jsonObject.getString("target2"));
            binding.edtTarget3.setText(jsonObject.getString("target3"));
            binding.edtStockname.setText(jsonObject.getString("storename"));
            binding.edtBuyprice.setText(jsonObject.getString("buyprice"));
            binding.edtStoploss.setText(jsonObject.getString("stoploss"));
            id=jsonObject.getString("id")

            if(jsonObject.getString("type")=="buy"){
                binding.radioBuy.isChecked=true;
            }else if(jsonObject.getString("type")=="sell"){
                binding.radioSell.isChecked=true;
            }
            val position=durationList.indexOf(jsonObject.getString("duration"))
            binding.spinnerType.setSelection(position);

        }

        if(binding.radioBuy.isSelected){
            type="buy"
        }else  if(binding.radioSell.isSelected){
            type="sell"
        }



binding.buttonPost.setOnClickListener {

    if(intent.extras!=null) {
        val responseDataItem=AddPostData(id,binding.edtBuyprice.text.toString(),binding.edtStoploss.text.toString(),binding.edtStockname.text.toString(),binding.edtTarget1.text.toString(),binding.edtTarget2.text.toString(),binding.edtTarget3.text.toString(),type,binding.spinnerType.selectedItem.toString());
        RetrofitClient.getRetrofitInstance().updateCreatePost(responseDataItem)
            ?.enqueue(object : Callback<Req?> {
                override fun onResponse(
                    call: Call<Req?>,
                    response: Response<Req?>
                ) {
                    Toast.makeText(this@AddPost,response.body()?.message,Toast.LENGTH_LONG).show()


                }

                override fun onFailure(call: Call<Req?>, t: Throwable) {


                }

            })
    }else{
        val responseDataItem=AddPostData("",binding.edtBuyprice.text.toString(),binding.edtStoploss.text.toString(),binding.edtStockname.text.toString(),binding.edtTarget1.text.toString(),binding.edtTarget2.text.toString(),binding.edtTarget3.text.toString(),type,binding.spinnerType.selectedItem.toString());

        RetrofitClient.getRetrofitInstance().createPost(responseDataItem)
            ?.enqueue(object : Callback<Req?> {
                override fun onResponse(
                    call: Call<Req?>,
                    response: Response<Req?>
                ) {
                    GlobalScope.launch(Dispatchers.IO) {
                        notification("");
                    }
                    Toast.makeText(this@AddPost,response.body()?.message,Toast.LENGTH_LONG).show()

                }

                override fun onFailure(call: Call<Req?>, t: Throwable) {

                    println("myrespo" + t.printStackTrace())
                }

            })
    }


}

    }
    private fun notification(msg:String){
        val url = URL("https://fcm.googleapis.com/fcm/send")
        val http = url.openConnection() as HttpURLConnection
        http.requestMethod = "POST"
        http.doOutput = true
        http.setRequestProperty("Content-type", "application/json")
        http.setRequestProperty(
            "Authorization",
            "key=AAAAOarZyBE:APA91bErpioh26b9S7lQ3zOLgKn5ErfNyq90kX4BMw7ZFN8TcDdSgZ_zppwynLY39cRMQ3HAspRcO2jkkmxBJZk-CkDHKJ974OXnwxmGq9-1hfaBX1MhMMj2wk2eqXz0ylUWklDHoWHi"
        )

        if(msg.isEmpty()){
            val  data=Data("","New tip added!");
            val dataReq=dataReq("/topics/market",data);
            val out = Gson().toJson(dataReq).toString().toByteArray(StandardCharsets.UTF_8)
            val stream = http.outputStream
            stream.write(out)
        }else{
            val  data=Data(msg,"");
            val dataReq=dataReq("/topics/market",data);
            val out = Gson().toJson(dataReq).toString().toByteArray(StandardCharsets.UTF_8)

            val stream = http.outputStream
            stream.write(out)
        }


        println("aaaaa"+http.responseCode.toString() + " " + http.responseMessage)
        http.disconnect()
        finish();
    }
}