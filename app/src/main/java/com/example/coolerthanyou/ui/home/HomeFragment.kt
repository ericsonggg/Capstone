package com.example.coolerthanyou.ui.home

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
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
 * Displays an overview of favorite freezers
 * Lists all known freezers
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
        logger.d(logTag, "onCreateView")
        root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.d(logTag, "onViewCreated")

        // init buttons
        urgentButton = root.findViewById(R.id.fragment_home_urgent_button)
        warningButton = root.findViewById(R.id.fragment_home_warning_button)

        // init recyclers
        favoritesList = root.findViewById(R.id.fragment_home_favorites_list)
        freezerList = root.findViewById(R.id.fragment_home_freezers_list)

        // add paging behavior to favorites
        PagerSnapHelper().attachToRecyclerView(favoritesList)
        favoritesList.addItemDecoration(LinePagerIndicatorDecoration())
    }

    override fun onStart() {
        super.onStart()
        logger.d(logTag, "onStart")
        _homeViewModel.updateData()

        //add click listeners to buttons
        urgentButton.setOnClickListener {
            showUrgentDialog()
        }
        warningButton.setOnClickListener {
            showWarningDialog()
        }

        //initialize recycler adapters
        val favoriteListAdapter = ComponentFreezerOverviewListAdapter(::freezerListClickCallback)
        favoritesList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = favoriteListAdapter
        }
        val freezerListAdapter = HomeFreezerListAdapter(::freezerListClickCallback)
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

    /**
     * OnClick listener for the freezer list.
     * Starts the details fragment for the chosen freezer
     *
     * @param boxId The boxId of the freezer that was clicked
     */
    private fun freezerListClickCallback(boxId: Long) {
        navigateToDetails(boxId)
    }

    /**
     * Create and show a dialog that lists all freezers with urgents in descending order
     * Does nothing if the view model is missing data
     */
    private fun showUrgentDialog() {
        val clickList: MutableList<Long> = mutableListOf()

        val dataAdapter = ArrayAdapter<String>(requireContext(), R.layout.component_simple_dialog_item).apply {
            // map freezers into [boxId, name]
            val freezers = _homeViewModel.getFreezers().value?.groupBy({ it.boxId }, { it.name }) ?: return

            // map urgents into [boxId, count(Urgents)], in descending order, and add each urgent to the array
            val urgents = _homeViewModel.getUrgents().value ?: return
            urgents.groupingBy { it.boxId }.eachCount().toList().sortedByDescending { it.second }.forEach {
                add(getString(R.string.fragment_home_urgent_dialog_list_item, freezers[it.first], it.second))
                clickList.add(it.first) //add to list so we know which one was clicked later
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.fragment_home_urgent_dialog_title)
            .setAdapter(dataAdapter) { dialogInterface, i ->
                navigateToDetails(clickList[i])
                dialogInterface.dismiss()
            }
            .setCancelable(true)
            .create()
            .show()
    }

    /**
     * Create and show a dialog that lists all freezers with warnings in descending order
     * Does nothing if the view model is missing data
     */
    private fun showWarningDialog() {
        val clickList: MutableList<Long> = mutableListOf()

        val dataAdapter = ArrayAdapter<String>(requireContext(), R.layout.component_simple_dialog_item).apply {
            // map freezers into [boxId, name]
            val freezers = _homeViewModel.getFreezers().value?.groupBy({ it.boxId }, { it.name }) ?: return

            // map urgents into [boxId, count(Urgents)], in descending order, and add each urgent to the array
            val warnings = _homeViewModel.getWarnings().value ?: return
            warnings.groupingBy { it.boxId }.eachCount().toList().sortedByDescending { it.second }.forEach {
                add(getString(R.string.fragment_home_warning_dialog_list_item, freezers[it.first], it.second))
                clickList.add(it.first) //add to list so we know which one was clicked later
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.fragment_home_warning_dialog_title)
            .setAdapter(dataAdapter) { dialogInterface, i ->
                navigateToDetails(clickList[i])
                dialogInterface.dismiss()
            }
            .setCancelable(true)
            .create()
            .show()
    }

    /**
     * Helper function to navigate to the Details fragment
     *
     * @param boxId The boxId of the freezer that was clicked
     */
    private fun navigateToDetails(boxId: Long) {
        HomeFragmentDirections.actionNavHomeToNavDetails(boxId).apply {
            findNavController().navigate(this)
        }
    }
}
