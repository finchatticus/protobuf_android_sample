package com.mohsenoid.protobuftest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPOutputStream

class MainActivity : AppCompatActivity() {

    private companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val jsonString = resources.getString(R.string.data_set)
        val ecgJson = JSONObject(jsonString)
        val dateStart = ecgJson.getInt("dateStart")
        val dateEnd = ecgJson.getInt("dateEnd")
        val frequency = ecgJson.getInt("frequency")
        val dataSet = ecgJson.getJSONArray("dataSet")
        val dataSetList = mutableListOf<Int>()
        for (i in 0 until dataSet.length()) {
            dataSetList.add(dataSet.getInt(i))
        }

        val ecg = EcgProtos.Ecg.newBuilder()
                .setDateStart(dateStart)
                .setDateEnd(dateEnd)
                .setFrequency(frequency)
                .addAllDataSet(dataSetList)
                .build()

        // Serialize Ecg to ByteArray
        val bytes = ecg.toByteArray()

        // Compress JSON by Gzip
        val compressed = compress(jsonString)

        val jsonSize = jsonString.toByteArray().size
        val compressedSize = compressed.size
        val protoBufSize = bytes.size
        Log.d(TAG, "JSON      size: $jsonSize byte, ${jsonSize.toKb()} kB")
        Log.d(TAG, "Gzip JSON size: $compressedSize byte, ${compressedSize.toKb()} kB")
        Log.d(TAG, "Protobuf  size: $protoBufSize byte, ${protoBufSize.toKb()} kB")
    }

    private fun Int.toKb() = this / 1024

    @Throws(IOException::class)
    fun compress(string: String): ByteArray {
        val os = ByteArrayOutputStream(string.length)
        val gos = GZIPOutputStream(os)
        gos.write(string.toByteArray())
        gos.close()
        val compressed = os.toByteArray()
        os.close()
        return compressed
    }

}
