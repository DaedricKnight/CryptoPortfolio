package com.research.whitedzenin.cryptoportfolio.Data

import android.os.Environment
import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class SaveData {
    fun saveData(data: String,value: String){
        var fileWriter: FileWriter? = null
        var csvWriter: CSVWriter? = null
        try {
            val file = File(Environment.getExternalStorageDirectory().toString() + "/crypto.csv")
            fileWriter = FileWriter(Environment.getExternalStorageDirectory().toString() + "/crypto.csv",true)
            // write String Array
            csvWriter = CSVWriter(fileWriter,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END)

            if(!file.exists()) csvWriter.writeNext(arrayOf("Data", "Value"))

            val data = arrayOf(
                    data,
                    value)

            csvWriter.writeNext(data)

            println("Write CSV using CSVWriter successfully!")

        } catch (e: Exception) {
            println("Writing CSV error!")
            e.printStackTrace()
        } finally {
            try {
                fileWriter!!.flush()
                fileWriter.close()
                csvWriter!!.close()
            } catch (e: IOException) {
                println("Flushing/closing error!")
                e.printStackTrace()
            }
        }
    }
}