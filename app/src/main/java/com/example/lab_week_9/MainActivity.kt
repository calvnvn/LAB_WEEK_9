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
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_9Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    App(navController = navController)
                }
            }
        }
    }
}

// BARU (Sesuai Langkah 4)
@Composable
fun App(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // Rute 1: "home"
        composable("home") {
            Home(
                navigateFromHomeToResult = { listDataString ->
                    navController.navigate("resultContent/?listData=$listDataString")
                }
            )
        }

        // Rute 2: "resultContent"
        composable(
            route = "resultContent/?listData={listData}",
            arguments = listOf(navArgument("listData") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val listData = backStackEntry.arguments?.getString("listData").orEmpty()
            ResultContent(listData = listData)
        }
    }
}

data class Student(
    var name: String
)

// DIPERBARUI (Sesuai Langkah 6 & 8)
@Composable
fun Home(navigateFromHomeToResult: (String) -> Unit) {
    // State logic (ini tidak berubah)
    val listData = remember {
        mutableStateListOf(
            Student("Tanu"),
            Student("Tina"),
            Student("Tono")
        )
    }
    val inputField: MutableState<Student> = remember { mutableStateOf(Student("")) }

    // --- PERUBAHAN DIMULAI DI SINI ---
    HomeContent(
        listData = listData,
        inputField = inputField.value,
        onInputValueChange = { input ->
            inputField.value = inputField.value.copy(name = input)
        },
        onButtonClick = {
            // Ini adalah solusi Tugas 1 (sudah benar)
            if (inputField.value.name.isNotBlank()) {
                listData.add(inputField.value)
                inputField.value = Student("")
            }
        },
        // 'navigateFromHomeToResult' sekarang akan mengonversi ke JSON
        navigateFromHomeToResult = {
            // 1. Siapkan Moshi
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            // 2. Buat adapter untuk tipe List<Student>
            val listType = Types.newParameterizedType(List::class.java, Student::class.java)
            val jsonAdapter: JsonAdapter<List<Student>> = moshi.adapter(listType)

            // 3. Konversi list menjadi string JSON
            val jsonString = jsonAdapter.toJson(listData.toList())

            // 4. Kirim string JSON, bukan string biasa
            navigateFromHomeToResult(jsonString)
        }
    )
}

// DIPERBARUI (Sesuai Langkah 7)
@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit, // <-- PERBAIKAN: HANYA TIPE
    navigateFromHomeToResult: () -> Unit
) {
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

                Row {
                    PrimaryTextButton(
                        text = stringResource(id = R.string.button_click),
                        onClick = { onButtonClick() } // <-- PERBAIKAN: Panggilan valid
                    )
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

@Composable
fun ResultContent(listData: String) { // listData sekarang adalah string JSON

    // 1. Siapkan Moshi (sama seperti di Home)
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val listType = Types.newParameterizedType(List::class.java, Student::class.java)
    val jsonAdapter: JsonAdapter<List<Student>> = moshi.adapter(listType)

    // 2. Parse string JSON kembali menjadi List<Student>
    // Kita gunakan try-catch untuk keamanan jika JSON-nya rusak
    val studentList: List<Student> = try {
        // Jika parsing gagal atau string-nya kosong, kembalikan list kosong
        jsonAdapter.fromJson(listData) ?: emptyList()
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }

    // 3. Tampilkan list menggunakan LazyColumn (sesuai Tugas 2)
    LazyColumn(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (studentList.isEmpty()) {
            item {
                OnBackgroundItemText(text = "Tidak ada data.")
            }
        } else {
            // Gunakan 'items' (plural) untuk me-looping list
            items(studentList) { student ->
                // Tampilkan setiap nama menggunakan elemen UI kita
                Column(
                    modifier = Modifier.padding(vertical = 4.dp).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OnBackgroundItemText(text = student.name)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    LAB_WEEK_9Theme {
        Home(navigateFromHomeToResult = {})
    }
}