package com.example.playlistmaker.presentation.ui.media

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.data.TrackCreator
import com.example.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import com.example.playlistmaker.presentation.ui.AudioPlayer
import com.example.playlistmaker.util.TimeUtils
import com.example.playlistmaker.util.getCorrectForm
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistInfoFragment : Fragment() {
    private var playlistId: Long? = null
    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!


    private lateinit var trackAdapter: TrackAdapter
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<View>

    private val viewModel: PlaylistInfoViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            playlistId = it.getLong(PLAYLIST_ID_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val playlistId = arguments?.getLong(PLAYLIST_ID_KEY)
        playlistId?.let {
            viewModel.loadPlaylistInfo(it)
        }

        binding.playlistInfoToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.playlistShareButton.setOnClickListener {
            handleShareButtonClick()
        }


        val behavior = BottomSheetBehavior.from(binding.playlistTracksBottomSheet)
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        behavior.isDraggable = true

        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistMenuBottomSheet)
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.playlistMenuButton.setOnClickListener {
            if (menuBottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = requireActivity().display
            display?.getRealMetrics(displayMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = requireActivity().windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(displayMetrics)
        }

        val screenHeight = displayMetrics.heightPixels
        val peekHeightPercentage = when {
            screenHeight >= 2400 -> 0.30
            screenHeight <= 1280 -> 0.1
            else -> 0.2
        }

        behavior.peekHeight = (screenHeight * peekHeightPercentage).toInt()

        trackAdapter = TrackAdapter(
            trackList = mutableListOf(),
            onItemClick = { track ->
                handleTrackClick(track)
            },
            onItemLongClick = { track ->
                MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialogTheme)
                    .setTitle(getString(R.string.dialog_title_delete_track))
                    .setMessage(getString(R.string.dialog_delete_message_track))
                    .setPositiveButton(getString(R.string.dialog_positive_delete_track)) { _, _ ->
                        viewModel.deleteTrackFromPlaylist(track.trackId)
                    }
                    .setNegativeButton(R.string.dialog_exit_negative) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

        )
        binding.playlistRecyclerView.adapter = trackAdapter

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            trackAdapter.updateData(tracks)
            if (tracks.isEmpty()) {
                binding.emptyText.visibility = View.VISIBLE
            } else {
                binding.emptyText.visibility = View.GONE
            }
        }

        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            binding.playlistName.text = playlist?.name
            binding.playlistInfoDescription.text = playlist?.description
            binding.playlistTitle.text = playlist?.name

            Glide.with(binding.albumArt.context)
                .load(playlist?.coverImageUri)
                .placeholder(R.drawable.placeholder_album_player)
                .into(binding.albumArt)

            Glide.with(binding.albumArt.context)
                .load(playlist?.coverImageUri)
                .placeholder(R.drawable.placeholder_album_player)
                .into(binding.playlistImage)
        }


        viewModel.tracksText.observe(viewLifecycleOwner) { tracksText ->
            binding.playlistInfoText.text =
                "${viewModel.totalDuration.value} • $tracksText"

            binding.numberTracksPlaylist.text = tracksText
        }



        binding.shareButton.setOnClickListener {
            handleShareButtonClick()
        }
        binding.editButton.setOnClickListener {
            handleEditButtonClick()
        }

        binding.deleteButton.setOnClickListener {
            handleDeleteButtonClick()
        }

    }

    private fun handleShareButtonClick() {
        val playlist = viewModel.playlist.value
        val tracks = viewModel.tracks.value ?: emptyList()

        if (tracks.isEmpty()) {
            MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialogTheme)
                .setMessage(R.string.dialog_playlist_share_empty)
                .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
                .show()
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            val tracksText = tracks
                .mapIndexed { index, track ->
                    "${index + 1}. ${track.artistName} - ${track.trackName} (${
                        TimeUtils.formatTime(
                            track.trackTimeMillis
                        )
                    })"
                }
                .joinToString("\n")

            val trackCount = tracks.size
            val trackWord = getCorrectForm(trackCount, listOf("трек", "трека", "треков"))

            val message = buildString {
                append("${playlist?.name}\n")
                append("${playlist?.description}\n\n")
                append("$trackCount $trackWord\n")
                append(tracksText)
            }

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    private fun handleTrackClick(track: Track) {
        val trackDto = TrackCreator.map(track)
        val intent = Intent(requireContext(), AudioPlayer::class.java).apply {
            putExtra(AudioPlayer.DATA_TRACK, trackDto)
        }
        startActivity(intent)
    }

    private fun handleEditButtonClick() {
        val playlistId = viewModel.playlist.value?.id
        if (playlistId != null) {
            val bundle = Bundle().apply {
                putLong(EditPlaylistFragment.PLAYLIST_ID_KEY, playlistId)
            }
            findNavController().navigate(R.id.editPlaylistFragment, bundle)
        }
    }

    private fun handleDeleteButtonClick() {

        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialogTheme)
            .setTitle(getString(R.string.playlist_info_delete))
            .setMessage(getString(R.string.playlist_delete_text))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deletePlaylist(playlistId!!)
                findNavController().popBackStack()
            }
            .setNegativeButton(R.string.dialog_exit_negative) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PLAYLIST_ID_KEY = "playlistId"

        fun newInstance() = PlaylistInfoFragment()
    }
}