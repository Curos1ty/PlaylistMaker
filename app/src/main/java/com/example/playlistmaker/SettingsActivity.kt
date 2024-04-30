package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(saveInstanceState: Bundle?) {
        super.onCreate(saveInstanceState)
        setContentView(R.layout.activity_settings)
        val toolbar = findViewById<Toolbar>(R.id.settings_toolbar)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val shareButton = findViewById<MaterialTextView>(R.id.share_button)
        val supportButton = findViewById<MaterialTextView>(R.id.write_to_tech_support_button)
        val userAgreementButton = findViewById<MaterialTextView>(R.id.user_agreement_button)

        shareButton.setOnClickListener {
            shareApp()
        }

        supportButton.setOnClickListener {
            writeToTechSupport()
        }

        userAgreementButton.setOnClickListener {
            userAgreement()
        }
    }

    private fun shareApp() {
        val appLink = getString(R.string.link_text_share_app)
        val message = getString(R.string.text_share_app) + ": $appLink"

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)

        try {
            startActivity(Intent.createChooser(shareIntent, "Поделиться приложением"))
        } catch (e: Exception) {
            Toast.makeText(this, "Не удалось открыть окно чтобы поделиться приложением", Toast.LENGTH_SHORT).show()
        }
    }

    private fun writeToTechSupport() {
        val message = getString(R.string.message_support)
        val mailSupport = getString(R.string.email_text_support)
        val mailTitleSupport = getString(R.string.title_text_support)
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailSupport))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, mailTitleSupport)
        supportIntent.putExtra(Intent.EXTRA_TEXT, message)

        try {
            startActivity(Intent.createChooser(supportIntent, "Написать в поддержку"))
        } catch (e:Exception) {
            Toast.makeText(this, "Не удалось открыть окно для отправки сообщения тех. поддержке", Toast.LENGTH_SHORT).show()
        }
    }

    private fun userAgreement() {
        val linkUserAgreement = getString(R.string.link_user_agreement)
        val linkUserAgreementIntent = Intent(Intent.ACTION_VIEW)
        linkUserAgreementIntent.data = Uri.parse(linkUserAgreement)

        try {
            startActivity(Intent.createChooser(linkUserAgreementIntent, "Пользовательское соглашение"))
        } catch (e:Exception) {
            Toast.makeText(this, "Не удалось открыть браузер", Toast.LENGTH_SHORT).show()
        }

    }


}