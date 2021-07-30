package davi.xavier.aep.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import davi.xavier.aep.data.entities.Sex
import davi.xavier.aep.data.entities.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val userInfoData: LiveData<UserInfo> by lazy { 
        repository.getCurrentUserInfo()
    }
    
    fun isLogged(): Boolean = repository.isUserLogged()
    
    fun getUserInfo(): LiveData<UserInfo> = userInfoData
    
    suspend fun login(email: String, password: String) {
        withContext(Dispatchers.IO) { repository.login(email, password) }
    }
    
    suspend fun signUp(email: String, password: String, height: Int, weight: Double, sex: Sex) {
        withContext(Dispatchers.IO) {
            repository.signUp(UserInfo(height = height, weight = weight, sex = sex), email, password)
        }
    }
    
    suspend fun logoff() {
        withContext(Dispatchers.IO) { repository.logoff() }
    }

    class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown viewmodel")
        }
    }
}
