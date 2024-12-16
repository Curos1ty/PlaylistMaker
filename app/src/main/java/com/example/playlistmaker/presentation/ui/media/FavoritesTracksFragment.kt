package com.example.playlistmaker.presentation.ui.media

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.data.TrackCreator
import com.example.playlistmaker.databinding.FragmentFavoritesTracksBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import com.example.playlistmaker.presentation.ui.AudioPlayer
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesTracksFragment : Fragment() {
    private val favoritesTracksViewModel: FavoritesTracksViewModel by viewModel()
    private var _binding: FragmentFavoritesTracksBinding? = null
    private lateinit var trackAdapter: TrackAdapter
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            handleTrackClick(track)
        }
        binding.favoritesRecyclerView.adapter = trackAdapter
        favoritesTracksViewModel.favoriteTrackLiveData.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNotEmpty()) {
                trackAdapter.updateData(tracks)
                binding.favotiresLayout.visibility = View.VISIBLE
                binding.noResultsPlaceholder.visibility = View.GONE
            } else {
                binding.favotiresLayout.visibility = View.GONE
                binding.noResultsPlaceholder.visibility = View.VISIBLE
                trackAdapter.updateData(tracks)
            }
        }
    }

    private fun handleTrackClick(track: Track) {
        val trackDto = TrackCreator.map(track)
        val intent = Intent(requireContext(), AudioPlayer::class.java).apply {
            putExtra(AudioPlayer.DATA_TRACK, trackDto)
        }
        startActivity(intent)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoritesTracksFragment()
    }
}