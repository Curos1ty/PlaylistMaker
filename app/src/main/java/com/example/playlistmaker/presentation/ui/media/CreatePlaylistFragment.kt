package com.example.playlistmaker.presentation.ui.media

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.presentation.ui.AudioPlayer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

open class CreatePlaylistFragment : Fragment() {
    protected var _binding: FragmentCreatePlaylistBinding? = null
    protected val binding get() = _binding!!
    private val viewModel: CreatePlaylistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupListeners()
        setupImageSelection()

        if (viewModel.currentPlaylistImagePath.value != null) {
            binding.playlistCoverImage.scaleType = ImageView.ScaleType.CENTER_CROP
            binding.playlistCoverImage.setImageURI(Uri.parse(viewModel.currentPlaylistImagePath.value))
        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (viewModel.currentPlaylistName.value?.isNotBlank() == true ||
                viewModel.currentPlaylistDescription.value?.isNotBlank() == true ||
                viewModel.currentPlaylistImagePath.value != null
            ) {
                showExitConfirmationDialog()
            } else {
                if (hasNavController()) {
                    findNavController().navigateUp()
                } else {
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as? AudioPlayer)?.setActivityVisibility(false)
    }

    override fun onDetach() {
        super.onDetach()
        (activity as? AudioPlayer)?.setActivityVisibility(true)
    }

    private fun setupToolbar() {
        binding.playlistCreateToolbar.setNavigationOnClickListener {
            showExitConfirmationDialog()
        }
    }

    private fun setupListeners() {
        binding.editTitlePlaylist.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    viewModel.setName(s.toString())
                }

                override fun afterTextChanged(p0: Editable?) {
                    updateCreateButtonState()
                }
            }
        )

        binding.editDescriptionPlaylist.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    viewModel.setDescription(s.toString())
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            }
        )

        binding.editTitlePlaylist.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.editDescriptionPlaylist.requestFocus()
                true
            } else {
                false
            }
        }

        binding.editDescriptionPlaylist.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                binding.editDescriptionPlaylist.clearFocus()
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.editDescriptionPlaylist.windowToken, 0)

                true
            } else {
                false
            }
        }

        binding.createPlaylistButton.setOnClickListener {
            val message = getString(R.string.playlist_created, viewModel.currentPlaylistName.value)
            viewModel.savePlaylist(
                onSuccess = {
                    val dialog =
                        MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialogTheme)
                            .setMessage(message)
                            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                                dialog.dismiss()
                                navigateUpOrPopBackStack()
                            }.create()
                    dialog.setOnDismissListener{
                        navigateUpOrPopBackStack()
                    }
                    dialog.show()
                },
                onError = { errorMessage ->
                    MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialogTheme)
                        .setMessage(getString(errorMessage.messageResId))
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            )
        }
    }

    private fun navigateUpOrPopBackStack() {
        if (hasNavController()) {
            findNavController().navigateUp()
        } else {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showExitConfirmationDialog() {
        if (viewModel.currentPlaylistName.value?.isNotBlank() == true ||
            viewModel.currentPlaylistDescription.value?.isNotBlank() == true ||
            viewModel.currentPlaylistImagePath.value != null
        ) {
            MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialogTheme)
                .setTitle(getString(R.string.dialog_exit_creation_title))
                .setMessage(getString(R.string.dialog_exit_creation_message))
                .setPositiveButton(getString(R.string.dialog_exit_positive)) { _, _ ->
                    if (hasNavController()) {
                        findNavController().navigateUp()
                    } else {
                        parentFragmentManager.popBackStack()
                    }

                }
                .setNegativeButton(getString(R.string.dialog_exit_negative)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        } else {
            if (hasNavController()) {
                findNavController().navigateUp()
            } else {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun updateCreateButtonState() {
        val isTitleFilled = binding.editTitlePlaylist.text?.isNotBlank() == true
        binding.createPlaylistButton.isEnabled = isTitleFilled
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupImageSelection() {
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.playlistCoverImage.setImageURI(uri)
                    binding.playlistCoverImage.scaleType = ImageView.ScaleType.CENTER_CROP
                    viewModel.setImagePath(uri)
                }
            }

        binding.imagePlayList.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        viewModel.currentPlaylistImagePath.observe(viewLifecycleOwner) { coverImageUri ->
            if (coverImageUri != null) {
                binding.playlistCoverImage.setImageURI(Uri.parse(coverImageUri))
                binding.playlistCoverImage.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }
    }

    private fun hasNavController(): Boolean {
        return try {
            findNavController()
            true
        } catch (e: IllegalStateException) {
            false
        }
    }

    companion object {
        fun newInstance(isFromActivity: Boolean): CreatePlaylistFragment {
            val fragment = CreatePlaylistFragment()
            fragment.arguments = Bundle().apply {
                putBoolean(IS_FROM_ACTIVITY, isFromActivity)
            }
            return fragment
        }

        const val IS_FROM_ACTIVITY = "isFromActivity"
    }
}