package com.example.weather.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.repository.WeatherRepository
import com.example.weather.viewmodel.MainViewModel
import com.example.weather.viewmodel.MainViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_top_app_bar.*
import java.util.*

class TopAppBar : Fragment() {

    private var searchView: SearchView? = null
    private var queryTextListener: SearchView.OnQueryTextListener? = null
    private lateinit var  viewModel: MainViewModel
    private var place: String = ""

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val repository = WeatherRepository()
        val viewModelFactory = MainViewModelFactory(repository)
        // setting view model
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        return inflater.inflate(R.layout.fragment_top_app_bar, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuIconsColorControl()
        place = viewModel.place
        loadFragment(WeatherInfoView.newInstance(viewModel.isElder, place))
        topAppBar.title = place

        val searchItem = topAppBar.menu.findItem(R.id.action_change_location)
        searchView = searchItem.actionView as SearchView
        // Changing location using searchView with onQueryTextSubmit function
        queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean { return false }
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.place = capitalizeEach(query) // Capitalizing each word of location name
                place = viewModel.place
                loadFragment(WeatherInfoView.newInstance(viewModel.isElder, place))
                topAppBar.title = place
                menuIconsColorControl()
                searchItem.collapseActionView()
                return true
            }
        }
        searchView!!.setOnQueryTextListener(queryTextListener)

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                // Dark mode menu option button
                R.id.action_dark_mode -> {
                    if (isDarkTheme(requireActivity())) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // dark mode disabled
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // dark mode enabled
                    }
                    menuItem.isChecked = !menuItem.isChecked // switching menu item check - if there is no room on the toolbar, actions will be moved to dropdown menu, with checkboxes indicating button state
                    true
                }
                // Elder mode menu option button
                R.id.action_elderly_mode -> {

                    viewModel.isElder = !viewModel.isElder // switching elder mode indicator in view model
                    menuIconsColorControl()                // switching menu button on/off
                    loadFragment(WeatherInfoView.newInstance(viewModel.isElder, place)) // loading fragment with switched elder mode
                    menuItem.isChecked = !menuItem.isChecked // switching menu item check - if there is no room on the toolbar, actions will be moved to dropdown menu, with checkboxes indicating button state
                    true
                }
                else -> false
            }
        }
    }

    // Function that return true/false due to dark mode being enabled/disabled
    private fun isDarkTheme(activity: Activity): Boolean {
        return activity.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    // Function that loads selected fragment in the place of another
    private fun loadFragment(fragment: Fragment) {

        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.view_container, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    // Function that capitalizes each word in a phrase.
    private fun capitalizeEach(string: String) : String{
        val chunks = string.split(" ")
        var capitalized = ""
        for (chunk in chunks) {
            capitalized += chunk.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT) + " "
        }
        return capitalized.dropLast(1)
    }

    // Function that controls menu icons transparency, that indicates if button is enabled or disabled
    private fun menuIconsColorControl() {

        if (isDarkTheme(requireActivity())) topAppBar.menu.getItem(2).icon.alpha = 255
        else                                topAppBar.menu.getItem(2).icon.alpha = 127

        if (viewModel.isElder)              topAppBar.menu.getItem(1).icon.alpha = 255
        else                                topAppBar.menu.getItem(1).icon.alpha = 127
    }
}