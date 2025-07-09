package com.cesoft.cesnostr.follow.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.adidas.mvi.compose.MviScreen
import com.cesoft.cesnostr.common.LoadingCompo
import com.cesoft.cesnostr.follow.FollowViewModel
import com.cesoft.cesnostr.follow.vmi.FollowIntent
import com.cesoft.cesnostr.follow.vmi.FollowState
import com.cesoft.cesnostr.home.view.bitcoinMetadata
import com.cesoft.cesnostr.home.view.opusMetadata
import com.cesoft.cesnostr.ui.theme.SepMax
import com.cesoft.cesnostr.ui.theme.SepMed
import com.cesoft.domain.AppError
import com.cesoft.cesnostr.R
import com.cesoft.cesnostr.common.ErrorHeader
import com.cesoft.cesnostr.common.toHumanMessage
import com.cesoft.cesnostr.ui.theme.FontSizeMed

@Composable
fun FollowPage(
    navController: NavController,
    viewModel: FollowViewModel,
) {
    LaunchedEffect(true) {
        android.util.Log.e("FollowPage", "------------- ${viewModel.state.value}")
    }
    val context = LocalContext.current
    MviScreen(
        state = viewModel.state,
        onSideEffect = { sideEffect ->
            viewModel.consumeSideEffect(
                sideEffect = sideEffect,
                navController = navController,
                context = context
            )
        },
        onBackPressed = {
            viewModel.execute(FollowIntent.Close)
        },
    ) { state: FollowState ->
        when(state) {
            is FollowState.Loading -> {
                viewModel.execute(FollowIntent.Load)
                LoadingCompo()
            }
            is FollowState.Init -> {
                FollowInit(state = state, reduce = viewModel::execute)
            }
        }
    }
}

@Composable
private fun FollowInit(
    state: FollowState.Init,
    reduce: (FollowIntent) -> Unit
) {
    val isErrorVisible = remember { mutableStateOf(true) }
    LaunchedEffect(state.error) { isErrorVisible.value = true }
    Column {
        if(state.error != null && isErrorVisible.value) {
            ErrorHeader(
                state.error.toHumanMessage(LocalContext.current),
                isErrorVisible
            )
        }
        android.util.Log.e("Aaa", "fetch------- ${state.list.isEmpty()} ----------- ")
        if(state.list.isEmpty()) {
            Text(
                text = stringResource(R.string.follow_empty_list),
                fontWeight = FontWeight.Bold,
                fontSize = FontSizeMed,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(SepMax)
            )
            return
        }
        Text(
            text = stringResource(R.string.following_authors),
            fontWeight = FontWeight.Bold,
            fontSize = FontSizeMed,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(SepMax)
        )
        LazyColumn(
            modifier = Modifier
                .padding(SepMed)
                .fillMaxSize()
        ) {
            for (author in state.list) {
                item {
                    FollowListItem(author, reduce)
                }
            }
        }
    }
}

//--------------------------------------------------------------------------------------------------
@Preview
@Composable
private fun FollowInit_Preview() {
    val state = FollowState.Init(
        list = listOf(
            bitcoinMetadata,
            opusMetadata,
        ),
        error = AppError.InvalidMetadata
    )
    Surface {
        FollowInit(state) { }
    }
}