package com.pratik.marketwatchadmin.data

data class AddPostData(
                         var id:String,
                         val buyprice: String,
                         val stoploss: String,
                         val storename: String,
                         val target1: String,
                         val target2: String,
                         val target3: String,
                         val type: String,
                         var duration:String)
