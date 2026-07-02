package `in`.paperboxd.app.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.R
import `in`.paperboxd.app.data.repository.UserRepository
import `in`.paperboxd.app.domain.model.UserProfile
import `in`.paperboxd.app.ui.components.AvatarImage
import `in`.paperboxd.app.ui.theme.Surface
import `in`.paperboxd.app.ui.theme.TextPrimary
import `in`.paperboxd.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FollowListMode { Followers, Following }

@HiltViewModel
class FollowListViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var users by mutableStateOf<List<UserProfile>>(emptyList())
        private set
    private var page = 1
    private var hasMore = true
    private var loading = false
    private var key: String? = null

    fun load(username: String, mode: FollowListMode) {
        val k = "$username/$mode"
        if (key == k) return
        key = k
        users = emptyList(); page = 1; hasMore = true
        loadMore(username, mode)
    }

    fun loadMore(username: String, mode: FollowListMode) {
        if (!hasMore || loading) return
        loading = true
        viewModelScope.launch {
            val result = when (mode) {
                FollowListMode.Followers -> userRepository.followers(username, page, 30)
                FollowListMode.Following -> userRepository.following(username, page, 30)
            }
            result.onSuccess { resp ->
                users = users + resp.users
                page += 1
                hasMore = resp.users.size == 30
            }
            loading = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowListSheet(
    username: String,
    mode: FollowListMode,
    onOpenProfile: (String) -> Unit,
    onDismiss: () -> Unit,
    viewModel: FollowListViewModel = hiltViewModel()
) {
    LaunchedEffect(username, mode) { viewModel.load(username, mode) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Surface) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = stringResource(
                    if (mode == FollowListMode.Followers) R.string.profile_followers
                    else R.string.profile_following
                ),
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary
            )
            Spacer(Modifier.height(8.dp))
            if (viewModel.users.isEmpty()) {
                Text(
                    stringResource(R.string.profile_no_users),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(vertical = 30.dp)
                )
            } else {
                LazyColumn {
                    itemsIndexed(viewModel.users, key = { _, u -> u.id }) { index, user ->
                        if (index >= viewModel.users.size - 5) {
                            LaunchedEffect(user.id) { viewModel.loadMore(username, mode) }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenProfile(user.username) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AvatarImage(url = user.avatarUrl, name = user.displayName, size = 40.dp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(user.displayName, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                                Text("@${user.username}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}
