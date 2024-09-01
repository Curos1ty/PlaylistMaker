package com.example.playlistmaker.domain.sharing.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val context: Context
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.link_text_share_app)
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.link_user_agreement)
    }


    private fun getSupportEmailData(): EmailData {
        return EmailData(
            email = context.getString(R.string.email_text_support),
            subject = context.getString(R.string.title_text_support),
            body = context.getString(R.string.message_support)
        )
    }
}