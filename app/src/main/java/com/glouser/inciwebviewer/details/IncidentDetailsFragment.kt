package com.glouser.inciwebviewer.details

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.glouser.inciwebviewer.database.IncidentDatabase
import com.glouser.inciwebviewer.databinding.FragmentIncidentDetailsBinding

/**
 * An Activity that shows the details for one incident.
 */
class IncidentDetailsFragment : Fragment() {

    private lateinit var binding: FragmentIncidentDetailsBinding
    private lateinit var viewModel: IncidentDetailsViewModel

    /**
     * Set up this fragment.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState instance state (unused)
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        binding = FragmentIncidentDetailsBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = IncidentDatabase.getInstance(application).incidentDatabaseDao

        val navArgs: IncidentDetailsFragmentArgs by navArgs()
        val viewModelFactory = IncidentDetailsViewModelFactory(navArgs.incidentID, dataSource)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(IncidentDetailsViewModel::class.java)

        binding.detailsViewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.viewUriEvent.observe(this, Observer {
            it?.let { uri ->
                Log.d(TAG, "Starting activity to view $uri")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
                viewModel.doneViewUri()
            }
        })

        return binding.root
    }

    companion object {
        /**
         * Tag for logging.
         */
        private val TAG = IncidentDetailsFragment::class.java.simpleName
    }
}
