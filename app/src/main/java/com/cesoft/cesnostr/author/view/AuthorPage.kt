package com.cesoft.cesnostr.author.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.adidas.mvi.compose.MviScreen
import com.cesoft.cesnostr.R
import com.cesoft.cesnostr.author.AuthorViewModel
import com.cesoft.cesnostr.author.vmi.AuthorIntent
import com.cesoft.cesnostr.author.vmi.AuthorState
import com.cesoft.cesnostr.common.LinkifyText
import com.cesoft.cesnostr.common.LoadingCompo
import com.cesoft.cesnostr.home.view.AuthorIconSize
import com.cesoft.cesnostr.home.view.bitcoinMetadata
import com.cesoft.cesnostr.ui.theme.FontSizeBig
import com.cesoft.cesnostr.ui.theme.SepMax
import com.cesoft.cesnostr.ui.theme.SepMed
import com.cesoft.cesnostr.ui.theme.SepMin
import com.cesoft.domain.AppError

@Composable
fun AuthorPage(
    navController: NavController,
    viewModel: AuthorViewModel,
) {
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
            viewModel.execute(AuthorIntent.Close)
        },
    ) { state: AuthorState ->
        PageContent(state, viewModel::execute)
    }
}

@Composable
private fun PageContent(
    state: AuthorState,
    reduce: (AuthorIntent) -> Unit
) {
    Column(Modifier.padding(SepMax)) {
        ToolbarCompo { reduce(AuthorIntent.Close) }
        when (state) {
            is AuthorState.Loading -> {
                reduce(AuthorIntent.Load)
                LoadingCompo()
            }
            is AuthorState.Init -> {
                AuthorInit(state = state, reduce = reduce)
            }
        }
    }
}

@Composable
private fun ToolbarCompo(onBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = SepMed).clickable { onBack()  }
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.close),
            modifier = Modifier.padding(top = SepMed, bottom = SepMed, end = SepMax)
        )
        Text(
            text = stringResource(R.string.author),
            fontWeight = FontWeight.Bold,
            fontSize = FontSizeBig
        )
    }
}

@Composable
private fun AuthorInit(
    state: AuthorState.Init,
    reduce: (AuthorIntent) -> Unit
) {
    AuthorCompo(state.metadata)
}

//--------------------------------------------------------------------------------------------------
@Preview
@Composable
private fun PageContent_Preview() {
    val state = AuthorState.Init(
        metadata = bitcoinMetadata,
        error = AppError.InvalidMetadata
    )
    Surface(Modifier.fillMaxSize()) {
        PageContent(state) {}
    }
}