package com.example.coolerthanyou.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.coolerthanyou.BaseFragment
import com.example.coolerthanyou.R
import com.example.coolerthanyou.model.Alert
import com.example.coolerthanyou.model.Freezer
import com.example.coolerthanyou.model.FreezerRecord
import com.example.coolerthanyou.ui.ComponentFreezerOverviewListAdapter
import com.example.coolerthanyou.ui.LinePagerIndicatorDecoration

/**
 * Fragment that acts as a "Home" for navigational purposes.
 * Displays an overview of all relevant data.
 */
class HomeFragment : BaseFragment() {

    private val logTag: String = "HomeFragment"
    private val _homeViewModel: HomeViewModel by viewModels { viewModelFactory }

    private lateinit var urgentButton: Button
    private lateinit var warningButton: Button
    private lateinit var favoritesList: RecyclerView
    private lateinit var freezerList: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)

        application.appComponent.mainComponent().create().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        logger.d(logTag, "onCreateView started")
        root = inflater.inflate(R.layout.fragment_home, container, false)

        // init buttons
        urgentButton = root.findViewById(R.id.fragment_home_urgent_button)
        warningButton = root.findViewById(R.id.fragment_home_warning_button)

        // init recyclers
        favoritesList = root.findViewById(R.id.fragment_home_favorites_list)
        freezerList = root.findViewById(R.id.fragment_home_freezers_list)

        // add paging behavior to favorites
        PagerSnapHelper().attachToRecyclerView(favoritesList)
        favoritesList.addItemDecoration(LinePagerIndicatorDecoration())

        logger.d(logTag, "onCreateView completed")
        return root
    }

    override fun onStart() {
        super.onStart()
        logger.d(logTag, "onStart")

        //initialize recycler adapters
        val favoriteListAdapter = ComponentFreezerOverviewListAdapter(requireContext(), logTag)
        favoritesList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = favoriteListAdapter
        }
        val freezerListAdapter = HomeFreezerListAdapter(requireContext(), ::freezerListClickCallback)
        freezerList.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = freezerListAdapter
        }

        // add observers to each live data
        _homeViewModel.getFreezers().observe(this, Observer { freezers: MutableList<Freezer> ->
            //update favoriteList
            favoriteListAdapter.updateFreezers(freezers.filter { it.is_favorite }.toMutableList())
            favoriteListAdapter.notifyDataSetChanged()

            //Update freezerList
            freezerListAdapter.updateFreezers(freezers)
            freezerListAdapter.notifyDataSetChanged()
        })
        _homeViewModel.getUniqueRecords().observe(this, Observer { records: MutableList<FreezerRecord> ->
            //update favoriteList
            favoriteListAdapter.updateRecords(records)
            favoriteListAdapter.notifyDataSetChanged()
        })
        _homeViewModel.getUrgents().observe(this, Observer { urgents: MutableSet<Alert> ->
            // Enable/disable urgent button
            if (urgents.isEmpty()) {
                urgentButton.visibility = View.GONE
            } else {
                urgentButton.visibility = View.VISIBLE
                urgentButton.text = getString(R.string.fragment_home_urgent_label, urgents.size)
            }

            //update favoriteList
            favoriteListAdapter.updateUrgents(urgents.toMutableSet()) //clone list
            favoriteListAdapter.notifyDataSetChanged()

            //update freezerList
            freezerListAdapter.updateUrgents(urgents)
            freezerListAdapter.notifyDataSetChanged()
        })
        _homeViewModel.getWarnings().observe(this, Observer { warnings: MutableSet<Alert> ->
            // Enable/disable warning button
            if (warnings.isEmpty()) {
                warningButton.visibility = View.GONE
            } else {
                warningButton.visibility = View.VISIBLE
                warningButton.text = getString(R.string.fragment_home_warning_label, warnings.size)
            }

            //update favoriteList
            favoriteListAdapter.updateWarnings(warnings.toMutableSet()) //clone list
            favoriteListAdapter.notifyDataSetChanged()

            //update freezerList
            freezerListAdapter.updateWarnings(warnings)
            freezerListAdapter.notifyDataSetChanged()
        })
    }

    private fun freezerListClickCallback(freezer: Freezer) {
        //TODO: start detail activity
    }
}
