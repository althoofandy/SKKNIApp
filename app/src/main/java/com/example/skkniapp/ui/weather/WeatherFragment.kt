package com.example.skkniapp.ui.weather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ScrollView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.skkniapp.R
import com.example.skkniapp.core.AppConstants
import com.example.skkniapp.core.Resource
import com.example.skkniapp.data.sensor.ShakeDetector
import com.example.skkniapp.databinding.FragmentWeatherBinding
import com.example.skkniapp.databinding.ItemDailyForecastBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WeatherViewModel by viewModel()

    private var selectedCityLatLng: Pair<Double, Double>? = null
    private var currentWeatherUiModel: WeatherUiModel? = null
    private var isForecastExpanded = false

    private var shakeDetector: ShakeDetector? = null

    private val cityWeatherAdapter = CityWeatherAdapter(
        onClick = { city ->
            selectedCityLatLng = city.latitude to city.longitude
            viewModel.selectCity(city.cityName, city.latitude, city.longitude)
        }
    )

    private val citySearchAdapter = CitySearchAdapter(
        onResultClick = { result ->
            selectedCityLatLng = result.latitude to result.longitude
            viewModel.selectCity(result.name, result.latitude, result.longitude)
            binding.etSearchCity.setText("")
            binding.rvSearchResults.visibility = View.GONE
        }
    )

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.loadWeatherForCurrentLocation()
        } else {
            Snackbar.make(binding.root, getString(R.string.permission_required), Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shakeDetector = ShakeDetector(requireContext()) {
            retryWeather()
            Snackbar.make(binding.root, getString(R.string.shake_refresh_triggered), Snackbar.LENGTH_SHORT).show()
        }

        binding.rvOtherCities.adapter = cityWeatherAdapter
        binding.rvSearchResults.adapter = citySearchAdapter

        binding.btnGetWeather.setOnClickListener {
            requestLocationAndLoadWeather()
        }

        binding.btnFavorite.setOnClickListener {
            onFavoriteStarClicked()
        }

        binding.tvForecastToggle.setOnClickListener {
            isForecastExpanded = !isForecastExpanded
            currentWeatherUiModel?.let { uiModel ->
                bindDailyForecast(uiModel.dailyForecast, uiModel.hourlyForecast)
            }
        }

        binding.btnRetryWeather.setOnClickListener {
            retryWeather()
        }

        binding.statWind.setOnClickListener {
            openWindCompass()
        }

        binding.btnRetryFavorites.setOnClickListener {
            viewModel.loadOtherCitiesWeather()
        }

        binding.etSearchCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchCity(s?.toString().orEmpty())
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.weatherState.collect { state -> renderWeather(state) } }
                launch { viewModel.otherCitiesState.collect { state -> renderOtherCities(state) } }
                launch { viewModel.searchResultsState.collect { state -> renderSearchResults(state) } }
                launch { viewModel.selectedCityName.collect { cityName -> renderSelectedCity(cityName) } }
            }
        }

        requestLocationAndLoadWeather()
    }

    private fun openWindCompass() {
        val uiModel = currentWeatherUiModel ?: return
        val cityName = viewModel.selectedCityName.value ?: getString(R.string.weather_title)
        val args = com.example.skkniapp.ui.compass.CompassFragment.newArgs(
            windDirectionDegrees = uiModel.windDirectionDegrees,
            windDirectionLabel = uiModel.windDirectionLabel,
            windSpeedLabel = uiModel.windSpeedLabel,
            cityName = cityName
        )
        findNavController().navigate(R.id.compassFragment, args)
    }

    private fun retryWeather() {
        val cityName = viewModel.selectedCityName.value
        val latLng = selectedCityLatLng
        if (cityName != null && latLng != null) {
            viewModel.selectCity(cityName, latLng.first, latLng.second)
        } else {
            requestLocationAndLoadWeather()
        }
    }

    private fun requestLocationAndLoadWeather() {
        val hasPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.loadWeatherForCurrentLocation()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun preserveScrollPosition(block: () -> Unit) {
        val scrollView = binding.root as? ScrollView
        val scrollY = scrollView?.scrollY ?: 0
        block()
        scrollView?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                scrollView.scrollTo(0, scrollY)
                scrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun renderWeather(state: Resource<WeatherUiModel>) = preserveScrollPosition {
        val shimmer = binding.shimmerWeatherInclude.root
        val forecastShimmer = binding.shimmerForecastInclude.root
        when (state) {
            is Resource.Loading -> {
                binding.groupWeatherContent.visibility = View.GONE
                binding.layoutWeatherError.visibility = View.GONE
                binding.tvLocationDetail.visibility = View.GONE
                binding.btnFavorite.visibility = View.GONE
                shimmer.visibility = View.VISIBLE
                shimmer.startShimmer()

                binding.groupForecastContent.visibility = View.GONE
                binding.tvForecastError.visibility = View.GONE
                forecastShimmer.visibility = View.VISIBLE
                forecastShimmer.startShimmer()
            }
            is Resource.Success -> {
                shimmer.stopShimmer()
                shimmer.visibility = View.GONE
                binding.layoutWeatherError.visibility = View.GONE
                binding.groupWeatherContent.visibility = View.VISIBLE

                forecastShimmer.stopShimmer()
                forecastShimmer.visibility = View.GONE
                binding.tvForecastError.visibility = View.GONE
                binding.groupForecastContent.visibility = View.VISIBLE

                bindWeather(state.data)
                updateFavoriteStarIcon()
            }
            is Resource.Error -> {
                shimmer.stopShimmer()
                shimmer.visibility = View.GONE
                binding.groupWeatherContent.visibility = View.GONE
                binding.tvLocationDetail.visibility = View.GONE
                binding.layoutWeatherError.visibility = View.VISIBLE
                binding.tvWeatherErrorMessage.text = state.message.ifBlank {
                    getString(R.string.weather_error_default)
                }

                forecastShimmer.stopShimmer()
                forecastShimmer.visibility = View.GONE
                binding.groupForecastContent.visibility = View.GONE
                binding.tvForecastError.visibility = View.VISIBLE

                binding.btnFavorite.visibility = View.GONE
            }
        }
    }

    private fun renderOtherCities(state: Resource<List<CityWeatherUiModel>>) = preserveScrollPosition {
        val shimmer = binding.shimmerFavoritesInclude.root
        when (state) {
            is Resource.Loading -> {
                binding.rvOtherCities.visibility = View.GONE
                binding.tvFavoritesEmpty.visibility = View.GONE
                binding.tvOtherCitiesHint.visibility = View.GONE
                binding.layoutFavoritesError.visibility = View.GONE
                shimmer.visibility = View.VISIBLE
                shimmer.startShimmer()
            }
            is Resource.Success -> {
                shimmer.stopShimmer()
                shimmer.visibility = View.GONE
                binding.layoutFavoritesError.visibility = View.GONE

                if (state.data.isEmpty()) {
                    binding.rvOtherCities.visibility = View.GONE
                    binding.tvFavoritesEmpty.visibility = View.VISIBLE
                    binding.tvOtherCitiesHint.visibility = View.GONE
                } else {
                    binding.tvFavoritesEmpty.visibility = View.GONE
                    binding.rvOtherCities.visibility = View.VISIBLE
                    binding.tvOtherCitiesHint.visibility = View.VISIBLE
                    cityWeatherAdapter.submitList(state.data)
                }
                updateFavoriteStarIcon()
            }
            is Resource.Error -> {
                shimmer.stopShimmer()
                shimmer.visibility = View.GONE
                binding.rvOtherCities.visibility = View.GONE
                binding.tvFavoritesEmpty.visibility = View.GONE
                binding.tvOtherCitiesHint.visibility = View.GONE
                binding.layoutFavoritesError.visibility = View.VISIBLE
            }
        }
    }

    private fun renderSearchResults(state: Resource<List<CitySearchResultUiModel>>) {
        when (state) {
            is Resource.Loading -> Unit
            is Resource.Success -> {
                citySearchAdapter.submitList(state.data)
                binding.rvSearchResults.visibility = if (state.data.isEmpty()) View.GONE else View.VISIBLE
            }
            is Resource.Error -> {
                binding.rvSearchResults.visibility = View.GONE
                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun renderSelectedCity(cityName: String?) {
        cityWeatherAdapter.setSelectedCity(cityName)
        if (cityName == null) {
            binding.tvScreenTitle.text = getString(R.string.weather_title)
            binding.btnGetWeather.text = getString(R.string.action_refresh)
        } else {
            binding.tvScreenTitle.text = getString(R.string.weather_title_city, cityName)
            binding.btnGetWeather.text = getString(R.string.action_back_to_my_location)
        }
        updateFavoriteStarIcon()
    }

    private fun updateFavoriteStarIcon() {
        val cityName = viewModel.selectedCityName.value
        if (cityName == null) {
            binding.btnFavorite.visibility = View.GONE
            return
        }
        binding.btnFavorite.visibility = View.VISIBLE
        val isFavorited = viewModel.isCityFavorited(cityName)
        binding.btnFavorite.setImageResource(
            if (isFavorited) R.drawable.ic_star_filled else R.drawable.ic_star_outline
        )
    }

    private fun onFavoriteStarClicked() {
        val cityName = viewModel.selectedCityName.value ?: return
        val latLng = selectedCityLatLng ?: return
        if (viewModel.isCityFavorited(cityName)) {
            viewModel.removeCityFromFavorites(cityName)
            Snackbar.make(binding.root, getString(R.string.city_removed), Snackbar.LENGTH_SHORT).show()
        } else {
            val weather = currentWeatherUiModel
            viewModel.addCityToFavorites(
                cityName,
                latLng.first,
                latLng.second,
                weather?.temperatureLabel.orEmpty(),
                weather?.emoji.orEmpty()
            )
            Snackbar.make(binding.root, getString(R.string.city_added), Snackbar.LENGTH_SHORT).show()
        }
        updateFavoriteStarIcon()
    }

    private fun bindWeather(uiModel: WeatherUiModel) {
        currentWeatherUiModel = uiModel
        binding.tvCardCityName.text = uiModel.cityName
        if (uiModel.locationDetailLabel.isNullOrBlank()) {
            binding.tvLocationDetail.visibility = View.GONE
        } else {
            binding.tvLocationDetail.visibility = View.VISIBLE
            binding.tvLocationDetail.text = uiModel.locationDetailLabel
        }
        binding.tvWeatherEmoji.text = uiModel.emoji
        binding.tvTemperature.text = uiModel.temperatureLabel
        binding.tvDescription.text = uiModel.description
        binding.tvFeelsLike.text = uiModel.feelsLikeLabel
        binding.tvHumidity.text = uiModel.humidityLabel
        binding.tvWindSpeed.text = uiModel.windSpeedLabel
        bindDailyForecast(uiModel.dailyForecast, uiModel.hourlyForecast)
    }

    private fun bindDailyForecast(items: List<DailyForecastUiModel>, hourlyForecast: List<HourlyForecastUiModel>) {
        binding.tvForecastToggle.animate()
            .rotation(if (isForecastExpanded) AppConstants.CHEVRON_ROTATED_DEGREES else AppConstants.CHEVRON_DEFAULT_DEGREES)
            .setDuration(AppConstants.FORECAST_TOGGLE_ANIMATION_MS)
            .start()
        binding.tvForecastToggle.contentDescription = if (isForecastExpanded) {
            getString(R.string.forecast_show_less)
        } else {
            getString(R.string.forecast_show_all)
        }

        val visibleItems = if (isForecastExpanded) items else items.take(AppConstants.COLLAPSED_FORECAST_DAYS)

        binding.llDailyForecast.removeAllViews()
        visibleItems.forEach { item ->
            val itemBinding = ItemDailyForecastBinding.inflate(
                layoutInflater, binding.llDailyForecast, false
            )
            itemBinding.tvDayLabel.text = item.dayLabel
            itemBinding.tvDayEmoji.text = item.emoji
            itemBinding.tvDayMaxTemp.text = item.maxTemperatureLabel
            itemBinding.tvDayMinTemp.text = item.minTemperatureLabel
            itemBinding.tvDayPrecipitation.text = item.precipitationLabel

            val backgroundRes = if (item.isToday) R.drawable.bg_stat_pill_selected else R.drawable.bg_stat_pill
            itemBinding.root.setBackgroundResource(backgroundRes)
            val textColor = if (item.isToday) {
                ContextCompat.getColor(requireContext(), R.color.white)
            } else {
                ContextCompat.getColor(requireContext(), R.color.text_primary)
            }
            val secondaryColor = if (item.isToday) {
                ContextCompat.getColor(requireContext(), R.color.white)
            } else {
                ContextCompat.getColor(requireContext(), R.color.text_secondary)
            }
            itemBinding.tvDayLabel.setTextColor(secondaryColor)
            itemBinding.tvDayMaxTemp.setTextColor(textColor)
            itemBinding.tvDayMinTemp.setTextColor(secondaryColor)
            itemBinding.tvDayPrecipitation.setTextColor(secondaryColor)

            itemBinding.root.setOnClickListener {
                val hourlyForDay = hourlyForecast.filter { hour -> hour.date == item.date }
                HourlyForecastBottomSheet.newInstance(item.dayLabel, hourlyForDay)
                    .show(childFragmentManager, "hourly_forecast")
            }

            binding.llDailyForecast.addView(itemBinding.root)
        }
    }

    override fun onResume() {
        super.onResume()
        shakeDetector?.start()
    }

    override fun onPause() {
        super.onPause()
        shakeDetector?.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        shakeDetector = null
        _binding = null
    }
}
