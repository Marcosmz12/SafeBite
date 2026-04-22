package com.example.safebite.view.widgets

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import com.example.safebite.R
import com.example.safebite.ui.theme.GreenPrimary
import java.util.Locale

@Composable
fun LanguageSelector() {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    
    val languages = listOf(
        "es" to stringResource(id = R.string.lang_es),
        "en" to stringResource(id = R.string.lang_en),
        "de" to stringResource(id = R.string.lang_de),
        "fr" to stringResource(id = R.string.lang_fr),
        "pt" to stringResource(id = R.string.lang_pt)
    )

    // Obtener idioma actual usando AppCompatDelegate (retrocompatible)
    val currentLanguageCode = AppCompatDelegate.getApplicationLocales().get(0)?.language 
        ?: Locale.getDefault().language

    val currentLanguageName = languages.find { it.first == currentLanguageCode }?.second ?: languages[0].second

    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF5F5F5),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Language, null, tint = GreenPrimary)
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(id = R.string.menu_language),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        currentLanguageName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    languages.forEach { (code, name) ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                changeLanguage(code)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun changeLanguage(localeString: String) {
    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(localeString)
    AppCompatDelegate.setApplicationLocales(appLocale)
}