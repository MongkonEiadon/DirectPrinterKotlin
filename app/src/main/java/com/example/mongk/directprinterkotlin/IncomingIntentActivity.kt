package com.example.mongk.directprinterkotlin

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.print.PrintHelper
import android.support.v7.app.AppCompatActivity
import android.util.Log
import mu.KotlinLogging
import org.slf4j.LoggerFactory

class IncomingIntentActivity : AppCompatActivity() {

    val logger = KotlinLogging.logger{}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent.action

        if(Intent.ACTION_SEND == action){
            logger.info{intent};

            doPrint()
        }


        setContentView(R.layout.activity_main)
}

    fun handleSendImage(intent: Intent){
        val text = intent.getStringExtra(Intent.EXTRA_TEXT)
        if(text !=null){
            logger.info{text}
        }
    }

    private fun doPrint(){
        val photoPrinter = PrintHelper(this)
        photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT

        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

        photoPrinter.printBitmap("test_print", bitmap)
    }
}
