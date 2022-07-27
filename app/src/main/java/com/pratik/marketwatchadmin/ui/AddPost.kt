package com.pratik.marketwatchadmin.ui


import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.pratik.marketwatchadmin.R
import com.pratik.marketwatchadmin.data.*
import com.pratik.marketwatchadmin.databinding.ActivityAddPostBinding
import com.pratik.marketwatchadmin.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import com.pratik.marketwatchadmin.databinding.ActivityGlobalUpdateBinding


class AddPost : AppCompatActivity() {
    lateinit var binding: ActivityAddPostBinding;
    var durationList: List<String> = listOf("INTRA DAY", "1 TO 2 DAYS", "2 TO 4 DAYS", "6 TO 8 DAYS")
    lateinit var adapter: ArrayAdapter<String>;
    var type: String = "buy";
    var id: String = "0";


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, durationList);
        binding.spinnerType.adapter = adapter

        binding.edtstockname.setFilters(arrayOf<InputFilter>(AllCaps()))



        if (intent.extras != null) {
            var editData: String? = intent.getStringExtra("editData");
            var jsonObject = JSONObject(editData);
            binding.edttarget1.setText(jsonObject.getString("target1"));
            binding.edttarget2.setText(jsonObject.getString("target2"));
            binding.edttarget3.setText(jsonObject.getString("target3"));
            binding.edtstockname.setText(jsonObject.getString("storename"));
            binding.edtbuyprice.setText(jsonObject.getString("buyprice"));
            binding.edtstoploss.setText(jsonObject.getString("stoploss"));
            id = jsonObject.getString("id")

            if (jsonObject.getString("type") == "buy") {
                binding.radioBuy.isChecked = true;
            } else if (jsonObject.getString("type") == "sell") {
                binding.radioSell.isChecked = true;
            }
            val position = durationList.indexOf(jsonObject.getString("duration"))
            binding.spinnerType.setSelection(position);

        }

        if (binding.radioBuy.isChecked) {
            type = "buy"
        } else if (binding.radioSell.isChecked) {
            type = "sell"
        }

        binding.buttonPost.setOnClickListener {

            if (intent.extras != null) {

                if (binding.radioBuy.isChecked) {
                    type = "buy"
                }else{
                    type = "sell"
                }

                println("mystatus"+type)
                val responseDataItem = AddPostData(
                    id,
                    binding.edtBuyprice.editText?.text.toString(),
                    binding.edtStoploss.editText?.text.toString(),
                    binding.edtStockname.editText?.text.toString(),
                    binding.edtTarget1.editText?.text.toString(),
                    binding.edtTarget2.editText?.text.toString(),
                    binding.edtTarget3.editText?.text.toString(),
                    type,
                    binding.spinnerType.selectedItem.toString()
                );
                RetrofitClient.getRetrofitInstance().updateCreatePost(responseDataItem)
                    ?.enqueue(object : Callback<Req?> {
                        override fun onResponse(
                            call: Call<Req?>,
                            response: Response<Req?>
                        ) {
                            Toast.makeText(
                                this@AddPost,
                                response.body()?.message,
                                Toast.LENGTH_LONG
                            ).show()
                            finish();


                        }

                        override fun onFailure(call: Call<Req?>, t: Throwable) {


                        }

                    })
            } else {
                val responseDataItem = AddPostData(
                    "",
                    binding.edtBuyprice.editText?.text.toString(),
                    binding.edtStoploss.editText?.text.toString(),
                    binding.edtStockname.editText?.text.toString(),
                    binding.edtTarget1.editText?.text.toString(),
                    binding.edtTarget2.editText?.text.toString(),
                    binding.edtTarget3.editText?.text.toString(),
                    type,
                    binding.spinnerType.selectedItem.toString()
                );

                RetrofitClient.getRetrofitInstance().createPost(responseDataItem)
                    ?.enqueue(object : Callback<Req?> {
                        override fun onResponse(
                            call: Call<Req?>,
                            response: Response<Req?>
                        ) {
                            GlobalScope.launch(Dispatchers.IO) {
                                notification("");
                                notificationNew("")
                            }
                            Toast.makeText(
                                this@AddPost,
                                response.body()?.message,
                                Toast.LENGTH_LONG
                            ).show()

                        }

                        override fun onFailure(call: Call<Req?>, t: Throwable) {

                            println("myrespo" + t.printStackTrace())
                        }

                    })
            }


        }

    }

    private fun notification(msg: String) {
        val url = URL("https://fcm.googleapis.com/fcm/send")
        val http = url.openConnection() as HttpURLConnection
        http.requestMethod = "POST"
        http.doOutput = true
        http.setRequestProperty("Content-type", "application/json")
        http.setRequestProperty(
            "Authorization",
            "key=AAAAOarZyBE:APA91bErpioh26b9S7lQ3zOLgKn5ErfNyq90kX4BMw7ZFN8TcDdSgZ_zppwynLY39cRMQ3HAspRcO2jkkmxBJZk-CkDHKJ974OXnwxmGq9-1hfaBX1MhMMj2wk2eqXz0ylUWklDHoWHi"
        )

        if (msg.isEmpty()) {
            val data = Data("New tip added!", "");
            val dataReq = dataReq("/topics/market", data);
            val out = Gson().toJson(dataReq).toString().toByteArray(StandardCharsets.UTF_8)
            val stream = http.outputStream
            stream.write(out)
        }

        println("aaaaa" + http.responseCode.toString() + " " + http.responseMessage)

         http.disconnect()


    }
    private fun notificationNew(msg: String) {
        val url = URL("https://fcm.googleapis.com/fcm/send")
        val http = url.openConnection() as HttpURLConnection
        http.requestMethod = "POST"
        http.doOutput = true
        http.setRequestProperty("Content-type", "application/json")
        http.setRequestProperty(
            "Authorization",
            "key=AAAAOarZyBE:APA91bErpioh26b9S7lQ3zOLgKn5ErfNyq90kX4BMw7ZFN8TcDdSgZ_zppwynLY39cRMQ3HAspRcO2jkkmxBJZk-CkDHKJ974OXnwxmGq9-1hfaBX1MhMMj2wk2eqXz0ylUWklDHoWHi"
        )

        if (msg.isEmpty()) {
            val data = Data("New tip added!", "");
            val dataReq = dataReq("/topics/market-test", data);
            val out = Gson().toJson(dataReq).toString().toByteArray(StandardCharsets.UTF_8)
            val stream = http.outputStream
            stream.write(out)
        }
//
//        val data = Data(msg, "");
//        val dataReq = dataReqnew("/topics/market-test", data);
//        val out = Gson().toJson(dataReq).toString().toByteArray(StandardCharsets.UTF_8)
//        val stream = http.outputStream
//        stream.write(out)

//
        println("aaaaa" + http.responseCode.toString() + " " + http.responseMessage)
        http.disconnect()
        if(http.responseMessage=="OK"){
            finish();
        }

    }

}