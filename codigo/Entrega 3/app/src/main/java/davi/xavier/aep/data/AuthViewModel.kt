package davi.xavier.aep.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
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

class AuthViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = Firebase.auth
    private val databaseReference: DatabaseReference = Firebase.database.reference
    private val userInfoData: FirebaseLiveData<UserInfo> = FirebaseLiveData(null, UserInfoBuilder())
    
    fun isUserLogged(): Boolean {
        return firebaseAuth.currentUser != null
    }
    
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
    
    fun getCurrentUserInfo(): LiveData<UserInfo> {
        return userInfoData
    }
    
    fun login(login: String, password: String): Task<AuthResult> {
        val task = firebaseAuth.signInWithEmailAndPassword(login, password)
        task.addOnCompleteListener { result ->
            if (result.isSuccessful) {
                updateInfoQuery()
            }
        }
        return task
    }
    
    private fun updateInfoQuery() {
        firebaseAuth.currentUser?.let { 
            userInfoData.updateQuery(databaseReference.child(Constants.USER_INFO_PATH).child(it.uid))
        }
    }
    
    fun logoff() {
        firebaseAuth.signOut()
    }
    
    fun signUp(email: String, password: String, height: Int, weight: Double, sex: Sex): Task<Void> {
        val task = firebaseAuth.createUserWithEmailAndPassword(email, password).continueWithTask {
            val user = firebaseAuth.currentUser!!
            val info = UserInfo(
                userUid = user.uid,
                height, weight, sex
            )

            return@continueWithTask databaseReference.child(Constants.USER_INFO_PATH).child(user.uid).setValue(info)
        }
        task.addOnCompleteListener { 
            if (it.isSuccessful) {
                updateInfoQuery()
            } else {
                firebaseAuth.currentUser?.delete()
            }
        }
        
        return task
    }
    
    private class UserInfoBuilder : FirebaseLiveData.DataBuilder<UserInfo> {
        override fun buildData(dataSnapshot: DataSnapshot): UserInfo {
            return dataSnapshot.getValue(UserInfo::class.java) ?: UserInfo()
        }
    }
}
