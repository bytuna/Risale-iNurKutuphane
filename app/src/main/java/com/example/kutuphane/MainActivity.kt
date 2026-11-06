package com.example.kutuphane

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.kutuphane.ui.LibraryScreen
import com.example.kutuphane.ui.ReaderScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transparent = android.graphics.Color.TRANSPARENT
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(transparent, transparent),
            navigationBarStyle = SystemBarStyle.light(transparent, transparent),
        )
        val prefs = Prefs(applicationContext)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF7B2412),
                    onPrimary = Color.White,
                    surface = Color(0xFFFFFBF2),
                ),
            ) {
                App(prefs)
            }
        }
    }
}

@Composable
private fun App(prefs: Prefs) {
    val context = LocalContext.current
    var shelves by remember { mutableStateOf<List<Shelf>?>(null) }

    fun reloadShelves() {
        shelves = Repository.loadLibrary(context, prefs)
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            reloadShelves()
        }
    }

    var openBookId by rememberSaveable { mutableStateOf<String?>(null) }
    var searchQueryTarget by rememberSaveable { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    val list = shelves
    if (list == null) {
        Box(Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF7B2412))
        }
    } else {
        val openBook = remember(openBookId, list) {
            list.flatMap { it.books }.firstOrNull { it.id == openBookId }
        }
        if (openBook != null) {
            ReaderScreen(
                initialBook = openBook,
                allShelves = list,
                prefs = prefs,
                searchQuery = searchQueryTarget,
                onBack = {
                    openBookId = null
                    searchQueryTarget = null
                    reloadShelves()
                }
            )
        } else {
            LibraryScreen(
                shelves = list,
                listState = listState,
                prefs = prefs,
                onReloadShelves = { reloadShelves() },
                onOpenBook = { book ->
                    prefs.addRecentBook(book.id)
                    searchQueryTarget = null
                    openBookId = book.id
                },
                onOpenBookAtPageIndex = { book, pageIndex, query ->
                    prefs.setPosition(book.id, pageIndex)
                    prefs.addRecentBook(book.id)
                    searchQueryTarget = query
                    openBookId = book.id
                },
                onResetBook = { book ->
                    prefs.setPosition(book.id, 0)
                },
                onRemoveRecentBook = { book ->
                    prefs.removeRecentBook(book.id)
                    reloadShelves()
                }
            )
        }
    }
}