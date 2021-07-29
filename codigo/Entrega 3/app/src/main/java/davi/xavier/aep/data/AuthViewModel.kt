package davi.xavier.aep.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import davi.xavier.aep.data.entities.Sex
import davi.xavier.aep.data.entities.UserInfo
import davi.xavier.aep.util.Constants
import davi.xavier.aep.util.FirebaseLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = Firebase.auth
    private val databaseReference: DatabaseReference = Firebase.database.reference
    private val userInfoData: FirebaseLiveData<UserInfo> by lazy {
        val data = FirebaseLiveData(null, UserInfoBuilder())
        firebaseAuth.addAuthStateListener {
            updateInfoQuery()
        }
        
        return@lazy data
    }
    
    fun isUserLogged(): Boolean {
        return firebaseAuth.currentUser != null
    }
    
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
    
    fun getCurrentUserInfo(): LiveData<UserInfo> {
        return userInfoData
    }

    suspend fun login(login: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            firebaseAuth.signInWithEmailAndPassword(login, password).await()
        }
    }
    
    private fun updateInfoQuery() {
        firebaseAuth.currentUser?.let { 
            userInfoData.updateQuery(databaseReference
                .child(Constants.USER_INFO_PATH)
                .child(it.uid))
        }
    }
    
    fun logoff() {
        firebaseAuth.signOut()
    }
    
    suspend fun signUp(email: String, password: String, height: Int, weight: Double, sex: Sex) {
        withContext(Dispatchers.IO) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            
            val user = firebaseAuth.currentUser!!
            val info = UserInfo(
                userUid = user.uid,
                height, weight, sex
            )

            databaseReference.child(Constants.USER_INFO_PATH).child(user.uid).setValue(info).await()
        }
    }
    
    private class UserInfoBuilder : FirebaseLiveData.DataBuilder<UserInfo> {
        override fun buildData(dataSnapshot: DataSnapshot): UserInfo {
            return dataSnapshot.getValue(UserInfo::class.java) ?: UserInfo()
        }
    }
}
