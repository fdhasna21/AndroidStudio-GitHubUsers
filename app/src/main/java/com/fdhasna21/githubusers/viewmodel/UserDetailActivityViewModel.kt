package com.fdhasna21.githubusers.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fdhasna21.githubusers.model.entity.HistoryDb
import com.fdhasna21.githubusers.model.response.RepoResponse
import com.fdhasna21.githubusers.model.response.UserResponse
import com.fdhasna21.githubusers.repository.HistoryRepositoryImp
import com.fdhasna21.githubusers.repository.UserRepositoryImp
import kotlinx.coroutines.launch

/**
 * Updated by Fernanda Hasna on 27/09/2024.
 */

class UserDetailActivityViewModel(
    private val userRepository: UserRepositoryImp,
    private val historyRepository: HistoryRepositoryImp
) : BaseViewModel() {

    private var _userDetail : MutableLiveData<UserResponse> =
        MutableLiveData<UserResponse>()
    val userDetail : LiveData<UserResponse> get() = _userDetail

    private var _allFollowings : MutableLiveData<ArrayList<UserResponse>> =
        MutableLiveData<ArrayList<UserResponse>>(arrayListOf())
    val allFollowings : LiveData<ArrayList<UserResponse>> get() = _allFollowings
    
    private var _allFollowers : MutableLiveData<ArrayList<UserResponse>> =
        MutableLiveData<ArrayList<UserResponse>>(arrayListOf())
    val allFollowers : LiveData<ArrayList<UserResponse>> get() = _allFollowers

    private var _allRepos : MutableLiveData<ArrayList<RepoResponse>> =
        MutableLiveData<ArrayList<RepoResponse>>(arrayListOf())
    val allRepos : LiveData<ArrayList<RepoResponse>> get() = _allRepos

    private var _allStars : MutableLiveData<ArrayList<RepoResponse>> =
        MutableLiveData<ArrayList<RepoResponse>>(arrayListOf())
    val allStars : LiveData<ArrayList<RepoResponse>> get() = _allStars

    private var _username : MutableLiveData<String> = MutableLiveData("")
    val username : LiveData<String> get() = _username

    fun setUsername(username: String){
        _username.value = username
        getUserDetail()
    }

    private fun getUserDetail(){
        _username.value?.let { username ->
            startLoading()
            userRepository.getUserDetail(
                username = username,
                onSuccess = { result ->
                    result.data.let {
                        _userDetail.value = it
                    }
                    endLoading()
                },
                onFailed = {
                    endLoading()
                }
            )
        }
    }

    fun getFollowingsFromRepository(){
        _username.value?.let { username ->
            startLoading()
            userRepository.getUserFollowings(
                username = username,
                onSuccess = { result ->
                    result.data.let {
                        _allFollowings.value = it
                    }
                    endLoading()
                },
                onFailed = {
                    endLoading()
                }
            )
        }
    }

    fun getFollowersFromRepository(){
        _username.value?.let { username ->
            startLoading()
            userRepository.getUserFollowers(
                username = username,
                onSuccess = { result ->
                    result.data.let {
                        _allFollowers.value = it
                    }
                    endLoading()
                },
                onFailed = {
                    endLoading()
                }
            )
        }
    }

    fun getReposFromRepository(){
        _username.value?.let { username ->
            startLoading()
            userRepository.getUserRepos(
                username = username,
                onSuccess = { result ->
                    result.data.let {
                        _allRepos.value = it
                    }
                    endLoading()
                },
                onFailed = {
                    endLoading()
                }
            )
        }
    }

    fun getStarsFromRepository(){
        _username.value?.let { username ->
            startLoading()
            userRepository.getUserStars(
                username = username,
                onSuccess = { result ->
                    result.data.let {
                        _allStars.value = it
                    }
                    endLoading()
                },
                onFailed = {
                    endLoading()
                }
            )
        }
    }

    fun updateHistoryFromRepository(userPictCachePath: String){
        startLoading()
        userDetail.value?.let { user ->
            user.id?.let{
                viewModelScope.launch {
                    val username = user.username ?: ""
                    val newTimestamp = System.currentTimeMillis()
                    val updated = HistoryDb(username = username, userId = it, photoProfile = userPictCachePath, timestamp = newTimestamp)
                    historyRepository.updateHistory(
                        history = updated,
                        onSuccess = {
                            endLoading()
                        },
                        onFailed = {
                            endLoading()
                        }
                    )
                }
            }
        }
    }
}