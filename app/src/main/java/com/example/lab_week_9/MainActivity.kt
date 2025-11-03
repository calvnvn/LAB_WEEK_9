package com.example.lab_week_9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState // Import ini
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList // IMPORT TAMBAHAN
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab_week_9.ui.theme.LAB_WEEK_9Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_9Theme {
                // PERBAIKAN: Sesuai Langkah 6, panggil Home() tanpa parameter
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Home()
                }
            }
        }
    }
}

// Sesuai Langkah 2
data class Student(
    var name: String
)

// Sesuai Langkah 3
@Composable
fun Home() {
    val listData = remember {
        mutableStateListOf(
            Student("Tanu"),
            Student("Tina"),
            Student("Tono")
        )
    }

    val inputField: MutableState<Student> = remember { mutableStateOf(Student("")) }

    HomeContent(
        listData = listData,
        inputField = inputField.value,
        onInputValueChange = { input ->
            // PERBAIKAN LOGIKA: Harus menentukan field 'name'
            inputField.value = inputField.value.copy(name = input)
        },
        onButtonClick = {
            if (inputField.value.name.isNotBlank()) {
                listData.add(inputField.value)
                inputField.value = Student("")
            }
        }
    )
}

// PERBAIKAN UTAMA: Parameter dipindahkan ke dalam ()
@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit
) { // Tanda '{' dimulai di sini
    LazyColumn {
        item {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(
                        id = R.string.enter_item
                    )
                )
                TextField(
                    value = inputField.name,
                    keyboardOptions = KeyboardOptions(
                        // PERBAIKAN: Sesuai modul Bagian 2
                        keyboardType = KeyboardType.Text
                    ),

                    onValueChange = {
                        onInputValueChange(it)
                    }
                )

                Button(onClick = { onButtonClick() }) {
                    Text(
                        text = stringResource(
                            id = R.string.button_click
                        )
                    )
                }
            }
        }

        items(listData) { item ->
            Column(
                modifier = Modifier.padding(vertical = 4.dp).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = item.name)
            }
        }
    }
} // Tanda '}' penutup untuk HomeContent


@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    LAB_WEEK_9Theme {
        // PERBAIKAN: Home() yang baru tidak punya parameter [cite: 389, 400]
        Home()
    }
}