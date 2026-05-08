package com.example.myexpense

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class EditExpenseActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_expense)


        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_save_changes).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.tv_delete_expense).setOnClickListener {
            finish()
        }

    }
}