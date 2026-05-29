package com.ticket12306.android.ui.station

import androidx.lifecycle.viewModelScope
import com.ticket12306.android.data.model.Station
import com.ticket12306.android.data.repository.StationRepository
import com.ticket12306.android.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class StationViewModel @Inject constructor(
    private val stationRepository: StationRepository
) : BaseViewModel() {

    companion object {
        /** 搜索防抖延迟（毫秒） */
        private const val SEARCH_DEBOUNCE_MS = 300L
    }

    /** 搜索结果车站列表 */
    private val _stations = MutableStateFlow<List<Station>>(emptyList())
    val stations: StateFlow<List<Station>> = _stations.asStateFlow()

    /** 热门车站列表 */
    private val _hotStations = MutableStateFlow<List<Station>>(emptyList())
    val hotStations: StateFlow<List<Station>> = _hotStations.asStateFlow()

    /** 当前搜索关键字（用于高亮匹配） */
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /** 是否正在加载车站数据 */
    private val _isLoadingStations = MutableStateFlow(false)
    val isLoadingStations: StateFlow<Boolean> = _isLoadingStations.asStateFlow()

    /** 本地是否有车站缓存数据 */
    private val _hasStations = MutableStateFlow(false)
    val hasStations: StateFlow<Boolean> = _hasStations.asStateFlow()

    /** 防抖搜索关键字流 */
    private val _searchQueryDebounced = MutableStateFlow("")

    init {
        setupSearchDebounce()
        checkAndLoadStations()
        loadHotStations()
    }

    /**
     * 设置搜索防抖：用户输入后延迟300ms才执行搜索
     * 避免频繁查询数据库，提升用户体验
     */
    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQueryDebounced
                .debounce(SEARCH_DEBOUNCE_MS)
                .collect { query ->
                    performSearch(query)
                }
        }
    }

    /**
     * 检查本地缓存状态并加载车站数据
     * 逻辑步骤：
     * 1. 检查本地是否有缓存数据
     * 2. 有缓存：检查是否过期，过期则后台更新
     * 3. 无缓存：从网络获取
     */
    private fun checkAndLoadStations() {
        viewModelScope.launch {
            val hasLocalData = stationRepository.hasStations()
            _hasStations.value = hasLocalData

            if (hasLocalData) {
                loadLocalStations()
                if (stationRepository.isCacheExpired()) {
                    fetchStationsSilently()
                }
            } else {
                fetchStations()
            }
        }
    }

    /**
     * 加载本地缓存的全部车站数据
     */
    private fun loadLocalStations() {
        viewModelScope.launch {
            stationRepository.allStations.collect { stationList ->
                _stations.value = stationList
            }
        }
    }

    /**
     * 加载热门车站列表
     * 从本地数据库按预定义的热门车站代码批量查询
     */
    private fun loadHotStations() {
        viewModelScope.launch {
            stationRepository.getHotStations().collect { hotList ->
                _hotStations.value = hotList
            }
        }
    }

    /**
     * 搜索车站（带防抖）
     * 用户输入关键字后，通过防抖流延迟执行实际搜索
     * 支持按城市名、车站名、拼音搜索
     */
    fun searchStations(query: String) {
        _searchQuery.value = query
        _searchQueryDebounced.value = query
    }

    /**
     * 执行实际搜索操作
     * 步骤：
     * 1. 空关键字 -> 显示全部车站
     * 2. 非空关键字 -> 综合搜索（名称、拼音、城市）
     */
    private fun performSearch(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                stationRepository.allStations.collect { stationList ->
                    _stations.value = stationList
                }
            } else {
                stationRepository.searchStations(query).collect { stationList ->
                    _stations.value = stationList
                }
            }
        }
    }

    /**
     * 从网络获取车站数据（显示加载状态）
     * 用于首次加载或手动刷新场景
     */
    fun fetchStations() {
        viewModelScope.launch(exceptionHandler) {
            _isLoadingStations.value = true

            val result = stationRepository.fetchStations()

            _isLoadingStations.value = false

            result.fold(
                onSuccess = { stationList ->
                    _hasStations.value = true
                    _stations.value = stationList
                    showSuccess("车站数据更新成功")
                },
                onFailure = { error ->
                    showError(error.message ?: "获取车站数据失败")
                }
            )
        }
    }

    /**
     * 静默从网络获取车站数据（不显示加载状态）
     * 用于缓存过期时后台更新，不打断用户操作
     */
    private fun fetchStationsSilently() {
        viewModelScope.launch {
            showSuccess("车站数据已过期，正在后台更新")
            val result = stationRepository.fetchStations()
            result.fold(
                onSuccess = {
                    _hasStations.value = true
                },
                onFailure = { }
            )
        }
    }

    /**
     * 根据车站代码获取车站信息
     */
    fun getStationByCode(code: String): Station? {
        return _stations.value.find { it.code == code }
    }

    /**
     * 根据车站名称获取车站信息
     */
    fun getStationByName(name: String): Station? {
        return _stations.value.find { it.name == name }
    }
}
