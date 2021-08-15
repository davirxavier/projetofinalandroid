package davi.xavier.aep.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import davi.xavier.aep.data.entities.Sex
import davi.xavier.aep.data.entities.User
import davi.xavier.aep.data.entities.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val userInfoData: LiveData<User?> by lazy { 
        repository.getCurrentUserInfo()
    }
    
    fun isLogged(): Boolean = repository.isUserLogged()
    
    fun getUserInfo(): LiveData<User?> = userInfoData
    
    suspend fun setCurrentStatUid(uid: String?) {
        withContext(Dispatchers.IO) {
            repository.setCurrentStat(uid)
        }
    }
    
    suspend fun login(email: String, password: String) {
        withContext(Dispatchers.IO) { repository.login(email, password) }
    }
    
    suspend fun signUp(email: String, password: String, height: Int, weight: Double, sex: Sex) {
        withContext(Dispatchers.IO) {
            repository.signUp(UserInfo(height = height, weight = weight, sex = sex), email, password)
        }
    }
    
    suspend fun logoff() {
        withContext(Dispatchers.IO) { 
            repository.setCurrentStat(null)
            repository.logoff() 
        }
    }
    
    suspend fun forgotPassword(email: String) {
        withContext(Dispatchers.IO) { repository.sendResetEmail(email) }
    }
    
    class AuthViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown viewmodel")
        }
    }
}
