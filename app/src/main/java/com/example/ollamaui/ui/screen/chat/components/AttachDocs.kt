package com.example.ollamaui.ui.screen.chat.components

import android.content.Intent
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ollamaui.domain.readers.Readers
import com.example.ollamaui.ui.common.sha256Hash
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachDocs(
    isEnabled: Boolean,
    onDispose: () -> Unit,
    onSelectClick: (String?, String?, String, String, String) -> Unit,
    exceededAlert: (Int) -> Unit,
) {
    val context = LocalContext.current
    var isImporting = remember { mutableStateOf(false) }
    var fileSize by remember { mutableIntStateOf(0) }
    var fileName by remember { mutableStateOf("") }
    var percentage by remember { mutableIntStateOf(0) }


    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { activityResult ->
        activityResult.data?.data?.let { uri ->
            var documentType = ""
            var sha256Hash = ""

            context.contentResolver.query(uri, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                cursor.moveToFirst()
                fileName = cursor.getString(nameIndex)
                documentType = fileName.split(".").last()
                fileSize = cursor.getString(sizeIndex).toInt()
            }
            if(fileSize <= 5*1024*1024) {
                context.contentResolver.openInputStream(uri)?.let { inputStream ->
                    isImporting.value = true
                    CoroutineScope(Dispatchers.IO).launch {
                        val (result, error) = Readers.getReaderForDocType(documentType = documentType)
                            .readFromInputStream(inputStream = inputStream, process = { percentage = it})
                        result?.let { sha256Hash = sha256Hash(it) ?: "" }
                        withContext(Dispatchers.IO) {
                            onSelectClick(result, error, documentType, fileName, sha256Hash)
                            inputStream.close()
                            isImporting.value = false
                        }
                    }
                }
            } else{
                exceededAlert(fileSize)
            }

        }
    }
    AnimatedVisibility(visible = isImporting.value) {
        BasicAlertDialog(
            onDismissRequest = {},
        ) {
            Surface(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(shape = RoundedCornerShape(20))
                    .size(300.dp, 150.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text("Importing file...")
                    Text(fileName, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                    Text("Total size: $fileSize bytes", style = MaterialTheme.typography.bodySmall)
                    Text("Done: $percentage/100%", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(20.dp))
                    LinearProgressIndicator()
                }
            }
        }
    }
    if(isEnabled) {
        launcher.launch(Intent(Intent.ACTION_GET_CONTENT).apply { type = "*/*" })
    }
    DisposableEffect(isEnabled) {
        onDispose { onDispose() }
    }
}
