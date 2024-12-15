package com.example.playlistmaker.presentation.ui.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.presentation.ui.AudioPlayer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistFragment : Fragment() {
    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreatePlaylistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupListeners()
        setupImageSelection()



        viewModel.imageScaleType.observe(viewLifecycleOwner) { scaleType ->
            binding.playlistCoverImage.scaleType = scaleType
        }



        if (viewModel.currentPlaylistImagePath != null) {
            binding.playlistCoverImage.scaleType = ImageView.ScaleType.CENTER_CROP
            binding.playlistCoverImage.setImageURI(Uri.parse(viewModel.currentPlaylistImagePath))
        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (viewModel.currentPlaylistName.isNotBlank() ||
                viewModel.currentPlaylistDescription.isNotBlank() ||
                viewModel.currentPlaylistImagePath != null
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

        viewModel.isTitleFilled.observe(viewLifecycleOwner) { isFilled ->
            binding.editTitlePlaylistBackground.isActivated = isFilled
        }
        viewModel.isDescriptionFilled.observe(viewLifecycleOwner) { isFilled ->
            binding.editDescriptionPlaylistBackground.isActivated = isFilled
        }


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
            viewModel.savePlaylist(
                onSuccess = {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Плейлист \"${viewModel.currentPlaylistName}\" создан")
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            dialog.dismiss()
                            if (hasNavController()) {
                                findNavController().navigateUp()
                            } else {
                                parentFragmentManager.popBackStack()
                            }
                        }
                        .show()
                },
                onError = { errorMessage ->
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(errorMessage)
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            )
        }
    }

    private fun showExitConfirmationDialog() {
        if (viewModel.currentPlaylistName.isNotBlank() ||
            viewModel.currentPlaylistDescription.isNotBlank() ||
            viewModel.currentPlaylistImagePath != null
        ) {
            MaterialAlertDialogBuilder(requireContext())
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
        binding.createPlaylistButton.backgroundTintList = ContextCompat.getColorStateList(
            requireContext(),
            if (isTitleFilled) R.color.blue_background else R.color.gray
        )
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
                    viewModel.setImagePath(saveImageToPrivateStorage(uri))
                }
            }

        binding.imagePlayList.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        if (viewModel.currentPlaylistImagePath != null) {
            binding.playlistCoverImage.setImageURI(Uri.parse(viewModel.currentPlaylistImagePath))
        } else {
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri): String? {
        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PLAYLIST_COVER_ALBUM
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val fileName = "${System.currentTimeMillis()}.jpg"
        val file = File(filePath, fileName)
        val inputStream = context?.contentResolver?.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory.decodeStream(inputStream)
            ?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        inputStream?.close()
        return file.absolutePath
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

        const val PLAYLIST_COVER_ALBUM = "playlist_cover_album"
        const val IS_FROM_ACTIVITY = "isFromActivity"
    }
}