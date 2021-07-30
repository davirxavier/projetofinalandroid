package davi.xavier.aep.data

import androidx.lifecycle.LiveData
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import davi.xavier.aep.data.entities.Sex
import davi.xavier.aep.data.entities.UserInfo
import davi.xavier.aep.util.Constants
import davi.xavier.aep.util.FirebaseLiveData
import davi.xavier.aep.util.builders.UserInfoBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository {
    private val firebaseAuth: FirebaseAuth by lazy { Firebase.auth }
    private val databaseReference: DatabaseReference by lazy { Firebase.database.reference }
    private var currentRef: DatabaseReference? = null
    
    private val userInfoData: FirebaseLiveData<UserInfo> by lazy {
        val data = FirebaseLiveData(null, UserInfoBuilder())

        updateInfoQuery(false)
        firebaseAuth.addAuthStateListener {
            updateInfoQuery()
        }

        return@lazy data
    }

    private fun updateInfoQuery(updateLiveDataQuery: Boolean = true) {
        firebaseAuth.currentUser?.let {
            userInfoData.updateQuery(databaseReference
                .child(Constants.USER_INFO_PATH)
                .child(it.uid))

            val ref = databaseReference
                .child(Constants.USER_INFO_PATH)
                .child(it.uid)

            currentRef = ref
            if (updateLiveDataQuery) userInfoData.updateQuery(ref)
        }
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
        return firebaseAuth.signInWithEmailAndPassword(login, password).await()
    }

    fun logoff() {
        firebaseAuth.signOut()
    }

    suspend fun signUp(userInfo: UserInfo, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()

        val user = firebaseAuth.currentUser!!
        databaseReference.child(Constants.USER_INFO_PATH).child(user.uid).setValue(userInfo).await()
    }

    private fun currentQuery(): DatabaseReference {
        return currentRef ?: throw IllegalStateException("User has not been authenticated or is invalid.")
    }
}
