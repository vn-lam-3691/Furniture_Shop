package com.vanlam.furnitureshop.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.vanlam.furnitureshop.R
import com.vanlam.furnitureshop.utils.Constants.INTRODUCTION_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
): ViewModel() {

    private val _navigate = MutableStateFlow(0)
    val navigate: StateFlow<Int> = _navigate

    companion object {
        const val SHOPPING_ACTIVITY = 2
        var ACCOUNT_OPTION = R.id.action_introductionFragment_to_accountOptionsFragment
    }

    init {
        val isStartButtonClick = sharedPreferences.getBoolean(INTRODUCTION_KEY, false)
        val user = firebaseAuth.currentUser

        if (user != null) {
            // Trường hợp user đã login vào ứng dụng thành công từ trước đó -> Navigate sang Shopping activity
            viewModelScope.launch {
                _navigate.emit(SHOPPING_ACTIVITY)
            }
        }
        else if (isStartButtonClick) {
            // Trường hợp user đã click vào nút Start trong lần đầu cài app -> Navigate sang OptionAccount trong tất cả các lần dùng tiếp theo
            viewModelScope.launch {
                _navigate.emit(ACCOUNT_OPTION)
            }
        }
        else {
            Unit
        }
    }

    fun clickStartButton() {
        sharedPreferences.edit()
            .putBoolean(INTRODUCTION_KEY, true)
            .apply()
    }
}