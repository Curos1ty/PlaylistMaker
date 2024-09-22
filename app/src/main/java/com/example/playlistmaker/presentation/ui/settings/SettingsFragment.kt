package com.example.playlistmaker.presentation.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.presentation.ui.settings.view_model.SettingsViewModel
import com.example.playlistmaker.util.App
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment: Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val themeSwitcher = binding.switchSetting
        themeSwitcher.isChecked = settingsViewModel.isDarkTheme.value ?: false
        settingsViewModel.isDarkTheme.observe(viewLifecycleOwner) { isDarkTheme ->
            themeSwitcher.isChecked = isDarkTheme
        }

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setDarkThemeEnabled(isChecked)
            (requireActivity().application as App).switchTheme(isChecked)
        }

        val shareButton = binding.shareButton
        shareButton.setOnClickListener {
            settingsViewModel.shareApp()
        }

        val supportButton = binding.writeToTechSupportButton
        supportButton.setOnClickListener {
            settingsViewModel.openSupport()
        }

        val userAgreementButton = binding.userAgreementButton
        userAgreementButton.setOnClickListener {
            settingsViewModel.openTerms()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}