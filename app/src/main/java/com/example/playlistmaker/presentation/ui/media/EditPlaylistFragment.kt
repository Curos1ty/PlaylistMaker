package com.example.playlistmaker.presentation.ui.media

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.util.toPx
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : CreatePlaylistFragment() {
    private val viewModel: EditPlaylistViewModel by viewModel()

    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onAttach(context: Context) {
        super.onAttach(context)

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Glide.with(this)
                    .load(uri)
                    .transform(
                        com.bumptech.glide.load.resource.bitmap.CenterCrop(),
                        RoundedCorners(requireContext().toPx(RADIUS).toInt())
                    )
                    .into(binding.playlistCoverImage)
                viewModel.setImagePath(uri)
            }
        }
    }

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

        val playlistId = arguments?.getLong(PLAYLIST_ID_KEY, 0) ?: 0L
        if (playlistId != 0L) {
            viewModel.setPlaylistId(playlistId)
        }

        viewModel.playlistId.observe(viewLifecycleOwner) { playlistId ->
            viewModel.loadPlaylistInfo(playlistId)
        }

        binding.playlistCreateToolbar.title = getString(R.string.playlist_update_text)
        binding.createPlaylistButton.text = getString(R.string.save_text)

        viewModel.currentPlaylistName.observe(viewLifecycleOwner) { name ->
            if (binding.editTitlePlaylist.text.toString() != name) {
                binding.editTitlePlaylist.setText(name)
            }
        }

        viewModel.currentPlaylistDescription.observe(viewLifecycleOwner) { description ->
            if (binding.editDescriptionPlaylist.text.toString() != description) {
                binding.editDescriptionPlaylist.setText(description)
            }
        }

        viewModel.currentPlaylistImagePath.observe(viewLifecycleOwner) { coverImageUri ->
            if (coverImageUri != null) {
                Glide.with(this)
                    .load(coverImageUri)
                    .transform(
                        com.bumptech.glide.load.resource.bitmap.CenterCrop(),
                        RoundedCorners(requireContext().toPx(RADIUS).toInt())
                    )
                    .into(binding.playlistCoverImage)
            }
        }


        binding.imagePlayList.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createPlaylistButton.setOnClickListener {
            val newName = binding.editTitlePlaylist.text.toString()
            val newDescription = binding.editDescriptionPlaylist.text.toString()

            viewModel.setName(newName)
            viewModel.setDescription(newDescription)

            viewModel.savePlaylist()
            findNavController().popBackStack()
        }

        binding.playlistCreateToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    companion object {
        const val PLAYLIST_ID_KEY = "playlistId"
        const val RADIUS = 8

        fun newInstance(playlistId: Long): EditPlaylistFragment {
            val fragment = EditPlaylistFragment()
            val args = Bundle()
            args.putLong(PLAYLIST_ID_KEY, playlistId)
            fragment.arguments = args
            return fragment
        }
    }
}