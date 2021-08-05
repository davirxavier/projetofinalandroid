package davi.xavier.aep.util.builders

import com.google.firebase.database.DataSnapshot
import davi.xavier.aep.data.entities.UserInfo
import davi.xavier.aep.util.FirebaseLiveData

class UserInfoBuilder : FirebaseLiveData.DataBuilder<UserInfo> {
    override fun buildData(dataSnapshot: DataSnapshot): UserInfo {
        return dataSnapshot.getValue(UserInfo::class.java) ?: UserInfo()
    }
}
