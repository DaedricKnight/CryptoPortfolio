package com.research.whitedzenin.cryptoportfolio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.research.whitedzenin.cryptoportfolio.Network.NetworkClient
import kotlinx.coroutines.experimental.launch
import java.io.BufferedInputStream
import kotlinx.coroutines.experimental.delay
import kotterknife.bindView

class MainActivity : AppCompatActivity() {

    private val PERMISSION_STORAGE: Int = 676
    val checkButton: Button by bindView(R.id.buttonCheck)
    val editValue: EditText by bindView(R.id.editValue)
    val textAmount: TextView by bindView(R.id.textViewAmount)
    val spinner_item: Spinner by bindView(R.id.spinner_item)
    val historical = mutableListOf("")
    val networkClient = NetworkClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadHistorical(networkClient)
        checkButton.setOnClickListener {
//============permission to write data only for research============================================
//            if(askPermissions(applicationContext))
                launch {
                    checkPortfolio(networkClient,spinner_item.selectedItem.toString(),10)
//==================================only for research===============================================
//                    var save = SaveData()
//                    for (i in 8..10) {
//                        historical.forEach {
//                        save.saveData(it, checkPortfolio(it,i))
////                            checkPortfolio(it,2)
//                            delay(1000)
//                        }
//                        println(2)
//                        saveData(i.toString(),"coins")
//                    }
//                    runOnUiThread { textAmount.setText("EXPERIMENT FINISHED!") }
//==================================only for research===============================================
                }

        }
    }

    fun loadHistorical(networkClient: NetworkClient)= launch {
            val str = networkClient.readStream(BufferedInputStream(
                    networkClient.get("https://coinmarketcap.com/historical/")))
            str.split("href=\"/historical/20").let { it.forEach { historical.add(it.split("\">")[0])} }
            historical.removeAt(0)
            historical.removeAt(0)
            val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, historical)
            runOnUiThread{spinner_item.adapter = adapter} }

    suspend fun checkPortfolio(networkClient: NetworkClient,spinnerValue:String, limit:Int):String{
            val names = mutableListOf("")
            val persantage = mutableListOf(0.0)
            val values = mutableListOf(0.0)
            val amount = mutableListOf("")
            val str = networkClient.readStream(BufferedInputStream(
                    networkClient.get("https://coinmarketcap.com/historical/20"+spinnerValue+"/")))
            val val_str = mutableListOf("")
            str.split("no-wrap currency-name\" data-sort=\"", limit = limit)
                    .let { it.forEach { names.add(it.split("\">")[0])} }
            str.split("no-wrap text-right\" data-sort=\"", limit = limit)
                    .let { it.forEach { val_str.add(it.split("\">")[0])} }
            str.split("no-wrap market-cap text-right\" data-usd=\"", limit = limit)
                    .let { it.forEach { try{ persantage.add(it.split("\"")[0].toDouble()) } catch (e:Exception){}} }
            str.split("data-supply=\"", limit = limit)
                .let { it.forEach { try{ amount.add(it.split("\"")[0]) } catch (e:Exception){}} }
            names.removeAt(0)
            names.removeAt(0)
            Log.d("names",names.toString())
            val_str.removeAt(0)
            val_str.removeAt(0)
            persantage.removeAt(0)
            amount.removeAt(0)
            amount.removeAt(0)
            persantage.forEach{
                try {
                    values.add(editValue.text.toString().toDouble()*(it/persantage.sum()))
                }catch (e:Exception){}
            }
            values.removeAt(0)
            val coins = mutableListOf(0.0)
            values.forEach{
                try {
                    coins.add(it/val_str[values.indexOf(it)].toDouble())
                }catch (e:Exception){}
            }
            coins.removeAt(0)

            delay(500)
            val names2 = mutableListOf("")
            val val_str2 = mutableListOf("")
            val portfolio = mutableListOf(0.0)
            val str2 = networkClient.readStream(BufferedInputStream(
                    networkClient.get("https://coinmarketcap.com/")))
            str2.split("no-wrap currency-name\" data-sort=\"").let { it.forEach { names2.add(it.split("\">")[0])} }
            str2.split("class=\"price\" data-usd=\"").let { it.forEach { val_str2.add(it.split("\"")[0])} }
            names2.removeAt(0)
            val_str2.removeAt(0)
            for(i in 0..names.size-1){
                try {
                    if(names2.contains(names[i])) {
                        portfolio.add(val_str2[names2.indexOf(names[i])].toDouble() * coins[i])
                    }
                }catch (e:Exception){}
            }
            runOnUiThread { textAmount.setText("$".plus(portfolio.sum().toString())) }
        Log.i("values",spinnerValue.plus("   ").plus(portfolio.sum().toString()))
        return portfolio.sum().toString()
    }

    fun askPermissions(context: Context):Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSION_STORAGE)
                return false
            } else return true
        } else return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_STORAGE -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(applicationContext,"Storage permission disabled, please enable it.",Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}


