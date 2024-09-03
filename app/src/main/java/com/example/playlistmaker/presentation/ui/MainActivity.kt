package com.example.playlistmaker.presentation.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.settings.activity.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.search)
        val buttonMediaLibrary = findViewById<Button>(R.id.media_library)
        val buttonSettings = findViewById<Button>(R.id.settings)

        buttonMediaLibrary.setOnClickListener {
            val displayIntentMediaLibrary = Intent(this, MediaLibraryActivity::class.java)
            startActivity(displayIntentMediaLibrary)
        }

        buttonSearch.setOnClickListener {
            val displayIntentSearch = Intent(this, SearchActivity::class.java)
            startActivity(displayIntentSearch)
        }
        buttonSettings.setOnClickListener {
            val displayIntentSettings = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntentSettings)
        }
    }
}
