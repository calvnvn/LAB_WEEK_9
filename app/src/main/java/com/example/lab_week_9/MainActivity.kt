package com.example.lab_week_9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row // IMPORT BARU
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController // IMPORT BARU
import androidx.navigation.NavType // IMPORT BARU
import androidx.navigation.compose.NavHost // IMPORT BARU
import androidx.navigation.compose.composable // IMPORT BARU
import androidx.navigation.compose.rememberNavController // IMPORT BARU
import androidx.navigation.navArgument // IMPORT BARU
import com.example.lab_week_9.ui.theme.LAB_WEEK_9Theme
// Import elemen kustom Anda dari Part 3 (sesuai instruksi Part 4)
import com.example.lab_week_9.ui.theme.OnBackgroundItemText
import com.example.lab_week_9.ui.theme.OnBackgroundTitleText
import com.example.lab_week_9.ui.theme.PrimaryTextButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_9Theme {
                // DIPERBARUI (Sesuai Langkah 5)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Buat NavController
                    val navController = rememberNavController()
                    // 2. Panggil Composable 'App' sebagai root baru
                    App(navController = navController)
                }
            }
        }
    }
}

// BARU (Sesuai Langkah 4)
// 'App' adalah root composable baru yang berisi Navigasi
@Composable
fun App(navController: NavHostController) {
    // NavHost adalah 'peta' navigasi aplikasi Anda
    NavHost(
        navController = navController,
        startDestination = "home" // Halaman pertama yang dibuka
    ) {
        // Rute 1: "home"
        composable("home") {
            // Menampilkan 'Home' dan memberinya lambda navigasi
            Home(
                navigateFromHomeToResult = { listDataString ->
                    // Perintah untuk pindah ke rute "resultContent"
                    // sambil mengirim data 'listDataString'
                    navController.navigate("resultContent/?listData=$listDataString")
                }
            )
        }

        // Rute 2: "resultContent"
        composable(
            route = "resultContent/?listData={listData}",
            arguments = listOf(navArgument("listData") {
                type = NavType.StringType // Menentukan tipe argumen
            })
        ) { backStackEntry ->
            // Mengambil argumen dari rute
            val listData = backStackEntry.arguments?.getString("listData").orEmpty()
            // Menampilkan 'ResultContent' dengan data yang diterima
            ResultContent(listData = listData)
        }
    }
}

// Sesuai Langkah 2 (dari kode Anda)
data class Student(
    var name: String
)

// DIPERBARUI (Sesuai Langkah 6)
// 'Home' sekarang menerima lambda untuk navigasi
@Composable
fun Home(navigateFromHomeToResult: (String) -> Unit) {
    // State logic dari kode Anda (Bagian 2)
    val listData = remember {
        mutableStateListOf(
            Student("Tanu"),
            Student("Tina"),
            Student("Tono")
        )
    }

    val inputField: MutableState<Student> = remember { mutableStateOf(Student("")) }

    // DIPERBARUI (Sesuai Langkah 8)
    // Berikan lambda navigasi ke HomeContent
    HomeContent(
        listData = listData,
        inputField = inputField.value,
        onInputValueChange = { input ->
            inputField.value = inputField.value.copy(name = input)
        },
        onButtonClick = {
            if (inputField.value.name.isNotBlank()) {
                listData.add(inputField.value)
                inputField.value = Student("")
            }
        },
        // Teruskan data list (yg diubah jadi String) ke fungsi navigasi
        navigateFromHomeToResult = {
            navigateFromHomeToResult(listData.toList().toString())
        }
    )
}

// DIPERBARUI (Sesuai Langkah 7)
// 'HomeContent' sekarang menerima lambda untuk tombol "Finish"
@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    navigateFromHomeToResult: () -> Unit // Parameter baru
) {
    // DIPERBARUI (Sesuai Langkah 9)
    // Menggunakan Elemen UI dari Part 3 (OnBackgroundTitleText, PrimaryTextButton)
    LazyColumn {
        item {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundTitleText(
                    text = stringResource(
                        id = R.string.enter_item
                    )
                )

                TextField(
                    value = inputField.name,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    onValueChange = {
                        onInputValueChange(it)
                    }
                )

                // Tombol dibungkus 'Row' (Horizontal)
                Row {
                    // Tombol 1: Submit
                    PrimaryTextButton(
                        text = stringResource(id = R.string.button_click),
                        onClick = { onButtonClick() }
                    )
                    // Tombol 2: Finish (BARU)
                    PrimaryTextButton(
                        text = stringResource(id = R.string.button_navigate),
                        onClick = { navigateFromHomeToResult() }
                    )
                }
            }
        }

        items(listData) { item ->
            Column(
                modifier = Modifier.padding(vertical = 4.dp).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundItemText(text = item.name)
            }
        }
    }
}

// BARU (Sesuai Langkah 10)
// Halaman tujuan navigasi
@Composable
fun ResultContent(listData: String) {
    Column(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Menampilkan data String yang diterima
        OnBackgroundItemText(text = listData)
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    LAB_WEEK_9Theme {
        // PERBAIKAN: 'Home' sekarang butuh lambda, berikan lambda kosong
        // agar preview tidak error
        Home(navigateFromHomeToResult = {})
    }
}