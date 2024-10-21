package com.example.ollamaui.ui.screen.chat.components

import android.content.Intent
import android.os.Build
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.example.ollamaui.domain.readers.Readers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AttachDocs(isEnabled: Boolean, onDispose: () -> Unit, onSelectClick: (String?, String?, String, String) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { activityResult ->
        activityResult.data?.data?.let { uri ->
            var fileName = ""
            var documentType = ""

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.contentResolver.query(uri, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    fileName = cursor.getString(nameIndex)
                    documentType = fileName.split(".").last()
                }
            }else{
                context.contentResolver.query(uri,null,null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    fileName = cursor.getString(nameIndex)
                    documentType = fileName.split(".").last()
                }
            }

            context.contentResolver.openInputStream(uri)?.let { inputStream ->
                CoroutineScope(Dispatchers.IO).launch {
                    val (result, error) = Readers.getReaderForDocType(documentType = documentType).readFromInputStream(inputStream)

                    withContext(Dispatchers.IO){
                        onSelectClick(result, error, documentType, fileName)
                        inputStream.close()
                    }
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
