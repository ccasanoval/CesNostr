package com.cesoft.cesnostr.login.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cesoft.cesnostr.R
import com.cesoft.cesnostr.common.InetImage
import com.cesoft.cesnostr.common.StyledField
import com.cesoft.cesnostr.login.mvi.LoginIntent
import com.cesoft.cesnostr.login.mvi.LoginState
import com.cesoft.cesnostr.ui.theme.SepMax
import com.cesoft.cesnostr.ui.theme.SepMed
import com.cesoft.cesnostr.ui.theme.SepMin

@Composable
internal fun SignInSuccess(
    state: LoginState.SignInSuccess,
    reduce: (LoginIntent) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(SepMin)) {
        item { Spacer(Modifier.height(SepMed)) }
        state.metadata?.asRecord()?.let {
            item { StyledField(stringResource(R.string.nick), it.displayName) }
            item { StyledField(stringResource(R.string.name), it.name) }
            item { StyledField(stringResource(R.string.wallet), it.lud16) }
            item { StyledField(stringResource(R.string.about), it.about) }
            item { StyledField(stringResource(R.string.website), it.website) }
            item { StyledField("lud06: ", it.lud06) }
            item { StyledField("nip05: ", it.nip05) }
            item { Spacer(Modifier.height(SepMax)) }
            item { StyledField(stringResource(R.string.avatar), it.picture) }
            item {
                InetImage(
                    image = it.picture,
                    modifier = Modifier.size(50.dp)
                )
            }
            item { Spacer(Modifier.height(SepMax)) }
            item { StyledField(stringResource(R.string.banner), it.banner) }
            item {
                InetImage(
                    image = it.banner,
                    modifier = Modifier
                )
            }
        } ?: run {
            item { Text(stringResource(R.string.error_metadata)) }
        }
        item { Spacer(Modifier.height(SepMax*2)) }
        item { ButtonBar(reduce) }
        item { Spacer(Modifier.height(SepMed*2)) }
    }
}

@Composable
private fun ButtonBar(
    reduce: (LoginIntent) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding()
    ) {
        Button(
            modifier = Modifier.padding(SepMin).weight(.5f),
            onClick = { reduce(LoginIntent.Accept) }
        ) {
            Text(stringResource(android.R.string.ok))
        }
        Button(
            onClick = { reduce(LoginIntent.Cancel) },
            modifier = Modifier.padding(SepMin),
        ) {
            Text(stringResource(android.R.string.cancel))
        }
    }
}