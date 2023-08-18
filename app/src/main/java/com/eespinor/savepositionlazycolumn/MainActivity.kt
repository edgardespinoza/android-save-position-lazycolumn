package com.eespinor.savepositionlazycolumn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eespinor.savepositionlazycolumn.ui.theme.SavePositionLazyColumnTheme
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

class MainActivity : ComponentActivity() {

    private val prefs by lazy {
        applicationContext.getSharedPreferences("prefs", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollPosition = prefs.getInt("scroll_position",0 )

        setContent {
            SavePositionLazyColumnTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val lazyListState = rememberLazyListState(
                            initialFirstVisibleItemIndex = scrollPosition
                    )

                    LaunchedEffect(lazyListState) {
                        snapshotFlow {
                            lazyListState.firstVisibleItemIndex
                        }
                            .debounce(500L)
                            .collectLatest { index ->
                                println("Saving scroll position")
                                prefs.edit()
                                    .putInt("scroll_position", index)
                                    .apply()
                            }
                    }

                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(100) {
                            Text(
                                text = "item $it",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
