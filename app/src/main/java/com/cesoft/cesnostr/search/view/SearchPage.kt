package com.cesoft.cesnostr.search.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adidas.mvi.compose.MviScreen
import com.cesoft.cesnostr.R
import com.cesoft.cesnostr.common.LoadingCompo
import com.cesoft.cesnostr.home.view.testEvents
import com.cesoft.cesnostr.search.SearchViewModel
import com.cesoft.cesnostr.search.mvi.SearchIntent
import com.cesoft.cesnostr.search.mvi.SearchState
import com.cesoft.cesnostr.ui.theme.SepMin
import com.cesoft.domain.AppError

@Composable
fun SearchPage(
    navController: NavController,
    viewModel: SearchViewModel,
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
            viewModel.execute(SearchIntent.Close)
        },
    ) { state: SearchState ->
        when(state) {
            is SearchState.Loading -> {
                viewModel.execute(SearchIntent.Load)
                LoadingCompo()
            }
            is SearchState.Init -> {
                SearchInit(state = state, reduce = viewModel::execute)
            }
            is SearchState.Result -> {
                SearchResult(state = state, reduce = viewModel::execute)
            }
        }
    }
}

@Composable
private fun SearchInit(state: SearchState, reduce: (SearchIntent) -> Unit) {
    val searchText = remember { mutableStateOf("") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.alpha(0.75f)
    ) {
        Text(
            text = stringResource(R.string.menu_search),
            modifier = Modifier.padding(SepMin).fillMaxWidth()
        )
        TextField(
            value = searchText.value,
            onValueChange = { searchText.value = it },
            modifier = Modifier.fillMaxWidth().padding(SepMin)
            //label = { Text("") }
        )
        Button(onClick = { reduce(SearchIntent.Search(searchText.value)) }) {
            Text(stringResource(R.string.search))
        }
    }
}

@Composable
private fun SearchResult(state: SearchState.Result, reduce: (SearchIntent) -> Unit) {
    Box(Modifier.fillMaxSize()) {
        SearchResultList(state = state, reduce = reduce)
        SearchInit(state = state, reduce = reduce)
    }
}

@Composable
fun SearchResultList(state: SearchState.Result, reduce: (SearchIntent) -> Unit) {
    LazyColumn(Modifier.padding(SepMin)) {
        item { Spacer(Modifier.size(175.dp)) }
        for(event in state.events) {
            item {
                Text(event.content)
                HorizontalDivider()
            }
        }
    }
}

//--------------------------------------------------------------------------------------------------
@Preview
@Composable
private fun SearchPage_Result_Preview() {
    val events = testEvents.toMutableList()
    for(i in 0..9) events += testEvents
    val state = SearchState.Result(
        events = events,
        error = AppError.NotKnownError
    )
    Surface {
        SearchResult(state = state) {}
    }
}