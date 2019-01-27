package com.naman14.timberx.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

import com.naman14.timberx.R
import com.naman14.timberx.databinding.FragmentSearchBinding
import com.naman14.timberx.ui.adapters.AlbumAdapter
import com.naman14.timberx.ui.widgets.RecyclerItemClickListener
import com.naman14.timberx.util.*
import com.naman14.timberx.ui.adapters.ArtistAdapter
import com.naman14.timberx.ui.adapters.SongsAdapter
import com.naman14.timberx.ui.viewmodels.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : NowPlayingFragment() {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var songAdapter: SongsAdapter
    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var artistAdapter: ArtistAdapter

    var binding by AutoClearedValue<FragmentSearchBinding>(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_search, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        searchViewModel = ViewModelProviders
                .of(activity!!, InjectorUtils.provideSearchViewModel(activity!!))
                .get(SearchViewModel::class.java)

        songAdapter = SongsAdapter()
        rvSongs.layoutManager = LinearLayoutManager(activity)
        rvSongs.adapter = songAdapter

        albumAdapter = AlbumAdapter()
        rvAlbums.layoutManager = GridLayoutManager(activity, 3)
        rvAlbums.adapter = albumAdapter

        artistAdapter = ArtistAdapter()
        rvArtist.layoutManager = GridLayoutManager(activity, 3)
        rvArtist.adapter = artistAdapter

        rvSongs.addOnItemClick(object : RecyclerItemClickListener.OnClickListener {
            override fun onItemClick(position: Int, view: View) {
                songAdapter.getSongForPosition(position)?.let { song ->
                    mainViewModel.mediaItemClicked(song,
                            getExtraBundle(songAdapter.songs!!.toSongIDs(), "All songs"))
                }
            }
        })

        rvAlbums.addOnItemClick(object : RecyclerItemClickListener.OnClickListener {
            override fun onItemClick(position: Int, view: View) {
                mainViewModel.mediaItemClicked(albumAdapter.albums!![position], null)
            }
        })

        rvArtist.addOnItemClick(object : RecyclerItemClickListener.OnClickListener {
            override fun onItemClick(position: Int, view: View) {
                mainViewModel.mediaItemClicked(artistAdapter.artists!![position], null)
            }
        })

        etSearch.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchViewModel.search(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                songAdapter.updateData(arrayListOf())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
        })

        searchViewModel.searchLiveData.observe(this, Observer {
            songAdapter.updateData(it.songs)
            albumAdapter.updateData(it.albums)
            artistAdapter.updateData(it.artists)
        })

        binding.let {
            it.viewModel = searchViewModel
            it.setLifecycleOwner(this)
        }

    }
}
