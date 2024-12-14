package com.example.playlistmaker.presentation.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.presentation.adapter.PlaylistAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private val playlistsViewModel: PlaylistsViewModel by viewModel()
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var playlistAdapter: PlaylistAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playlistRecyclerView.layoutManager = GridLayoutManager(context, 2)

        playlistAdapter = PlaylistAdapter(onPlaylistClick = null, isInBottomSheet = false)
        binding.playlistRecyclerView.adapter = playlistAdapter

        playlistsViewModel.playlistsLiveData.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.setPlaylists(playlists)
            if (playlists.isEmpty()) {
                binding.emptyText.visibility = View.VISIBLE
                binding.emptyPlaylistPlaceholder.visibility = View.VISIBLE
            } else {
                binding.emptyText.visibility = View.GONE
                binding.emptyPlaylistPlaceholder.visibility = View.GONE
            }
        }

        binding.createPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediaLibraryFragment2_to_createPlaylistFragment2,
                Bundle().apply {
                    putBoolean("isFromActivity", false)
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        playlistsViewModel.loadPlaylists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}