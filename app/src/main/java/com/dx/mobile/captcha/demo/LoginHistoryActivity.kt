package com.dx.mobile.captcha.demo

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dx.mobile.captcha.demo.db.LoginRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LoginHistoryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LoginHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_history)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LoginHistoryAdapter()
        recyclerView.adapter = adapter

        // Load login history
        loadLoginHistory()
    }

    private fun loadLoginHistory() {
        lifecycleScope.launch {
            val loginRecords = withContext(Dispatchers.IO) {
                App.getDatabase().loginDao().getAll()
            }
            adapter.setData(loginRecords)
        }
    }

    inner class LoginHistoryAdapter : RecyclerView.Adapter<LoginHistoryAdapter.ViewHolder>() {
        private var loginRecords: List<LoginRecord> = emptyList()

        fun setData(data: List<LoginRecord>) {
            loginRecords = data
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_login_record, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = loginRecords.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(loginRecords[position])
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val phoneTextView: TextView = itemView.findViewById(R.id.phone_text_view)
            private val timeTextView: TextView = itemView.findViewById(R.id.time_text_view)
            private val smsCodeTextView: TextView = itemView.findViewById(R.id.sms_code_text_view)
            private val tokenTextView: TextView = itemView.findViewById(R.id.token_text_view)
            private val copyButton: Button = itemView.findViewById(R.id.copy_button)
            private val verifyButton: Button = itemView.findViewById(R.id.verify_button)

            fun bind(record: LoginRecord) {
                // Format phone number
                phoneTextView.text = "(${record.countryCode})${record.phoneNumber}"

                // Format login time
                val dateFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault())
                val formattedDate = dateFormat.format(Date(record.loginTime))
                timeTextView.text = "ログイン時刻: $formattedDate"

                smsCodeTextView.text = "SMSコード: ${record.smsCode}"

                // Display token
                tokenTextView.text = "token: ${record.token}"

                // Set up copy button
                copyButton.setOnClickListener {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("token", record.token)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(this@LoginHistoryActivity, "トークンをコピーしました", Toast.LENGTH_SHORT).show()
                }

                // Verify button functionality will be implemented later
                verifyButton.setOnClickListener {
                    // TODO
                }
            }
        }
    }
}