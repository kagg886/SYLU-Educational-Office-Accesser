package com.kagg886.sylu_eoa.ui.model.impl

import com.kagg886.sylu_eoa.api.v2.network.NetWorkClient
import com.kagg886.sylu_eoa.api.v2.network.asJSONBean
import com.kagg886.sylu_eoa.getApp
import com.kagg886.sylu_eoa.ui.model.BaseViewModel
import com.kagg886.sylu_eoa.util.Announcement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable

private val app by lazy {
    getApp()
}

class AppOnlineConfigViewModel: BaseViewModel<UpdateInfo>() {
    private val _broadcast = MutableStateFlow("")

    val broadcast = _broadcast.asStateFlow()

    private val client = NetWorkClient("https://gitee.com")

    override suspend fun onDataFetch(): UpdateInfo {
        val info = client.execute("/api/v5/repos/kagg886/sylu-educational-office-accesser/releases/latest").asJSONBean<UpdateInfo>()

        val oldAnn = app.getConfig(Announcement).first()

        val newAnn = client.execute("/kagg886/sylu-educational-office-accesser/raw/master-3.0/runtime/broadcast.txt").body?.string()
        if (oldAnn == newAnn) {
            _broadcast.value = ""
        } else {
            _broadcast.value = newAnn!!
            app.updateConfig(Announcement,newAnn)
        }
        return info
    }
}
@Serializable
data class UpdateInfo(
    val id: Int,
    val tag_name: String,
    val target_commitish: String,
    val prerelease: Boolean,
    val name: String,
    val body: String,
    val author: Author,
    val created_at: String,
    val assets: List<Asset>
)
@Serializable
data class Author(
    val id: Int,
    val login: String,
    val name: String,
    val avatar_url: String,
    val url: String,
    val html_url: String,
    val remark: String,
    val followers_url: String,
    val following_url: String,
    val gists_url: String,
    val starred_url: String,
    val subscriptions_url: String,
    val organizations_url: String,
    val repos_url: String,
    val events_url: String,
    val received_events_url: String,
    val type: String
)
@Serializable
data class Asset(
    val browser_download_url: String,
    val name: String
)