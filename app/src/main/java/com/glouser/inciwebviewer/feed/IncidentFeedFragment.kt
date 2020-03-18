package com.glouser.inciwebviewer.feed

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.glouser.inciwebviewer.R
import com.glouser.inciwebviewer.database.IncidentDatabase
import com.glouser.inciwebviewer.databinding.FragmentIncidentFeedBinding

/**
 * An Activity that shows the items from the incident RSS feed in a scrolling list.
 */
class IncidentFeedFragment : Fragment() {

    private val viewModel: IncidentFeedViewModel by lazy {
        val activity = requireNotNull(this.activity)
        val database = IncidentDatabase.getInstance(activity.application).incidentDatabaseDao
        val sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)
        val viewModelFactory = IncidentFeedViewModelFactory(database, sharedPreferences)
        ViewModelProviders.of(this, viewModelFactory).get(IncidentFeedViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentIncidentFeedBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Set up incident list in recycler view.
        val adapter = IncidentAdapter(FeedItemListener { incidentId ->
            viewModel.onIncidentClicked(incidentId)
        })
        binding.incidentList.adapter = adapter

        viewModel.incidents.observe(this, Observer {
            it?.let { adapter.data = it }
        })

        viewModel.navToIncidentDetails.observe(this, Observer { incidentId ->
            incidentId?.let {
                // Launch IncidentDetailsFragment for this incident.
                this.findNavController().navigate(IncidentFeedFragmentDirections.actionFeedFragmentToDetailsFragment(it))
                viewModel.doneNavToIncidentDetails()
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    /**
     * Handle the onResume lifecycle event.
     */
    override fun onResume() {
        super.onResume()
        viewModel.checkIncidentFeed()
    }

    /**
     * Handle the action bar menu creation event.
     *
     * @param menu The action bar menu.
     * @param menuInflater
     * @return true if the menu was created
     */
    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.menu_main, menu)
    }

    /**
     * Handle a menu item selection.
     *
     * @param item The menu item that was selected.
     * @return true if the event was handled
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_update) {
            Log.d(TAG, "onOptionsItemSelected() -- Update now")
            viewModel.downloadIncidentFeed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        /**
         * Tag for logging.
         */
        private val TAG = IncidentFeedFragment::class.java.simpleName
    }
}
