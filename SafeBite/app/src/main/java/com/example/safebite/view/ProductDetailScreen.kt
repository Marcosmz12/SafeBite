package com.example.safebite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.safebite.R
import com.example.safebite.controller.ProductController
import com.example.safebite.ui.theme.GreenPrimary
import com.example.safebite.ui.theme.GreenSoft

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(navController: NavHostController, productController: ProductController) {
    val colors = MaterialTheme.colorScheme

    // Obtenemos el producto seleccionado (ya sea del escáner o de la lista)
    val product = productController.selectedProduct.value ?: productController.scannedProduct.value

    if (product == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = GreenPrimary)
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(id = R.string.loading), color = Color.Gray)
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.product_details), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBackIosNew, contentDescription = stringResource(id = R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = colors.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(bottom = 32.dp)
        ) {
            // ── CABECERA CON IMAGEN ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        Brush.verticalGradient(listOf(GreenSoft, Color.White))
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(220.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(id = R.drawable.logo_safebite)
                )
            }

            // ── CUERPO DE INFORMACIÓN ────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = product.name,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.onBackground,
                    lineHeight = 32.sp
                )

                Text(
                    text = product.brands ?: product.store,
                    fontSize = 18.sp,
                    color = GreenPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ── SECCIÓN SALUD ─────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HealthBadge(
                        label = "Nutri-Score",
                        value = product.nutriscore_grade?.uppercase() ?: "?",
                        containerColor = when (product.nutriscore_grade?.lowercase()) {
                            "a" -> Color(0xFF038141)
                            "b" -> Color(0xFF85BB2F)
                            "c" -> Color(0xFFFECB02)
                            "d" -> Color(0xFFEE8100)
                            "e" -> Color(0xFFE63E11)
                            else -> Color.Gray
                        }
                    )

                    HealthBadge(
                        label = "Eco-Score",
                        value = product.ecoscore_grade?.uppercase() ?: "?",
                        containerColor = when (product.ecoscore_grade?.lowercase()) {
                            "a" -> Color(0xFF038141)
                            "b" -> Color(0xFF85BB2F)
                            "c" -> Color(0xFFFECB02)
                            "d" -> Color(0xFFEE8100)
                            "e" -> Color(0xFFE63E11)
                            else -> Color.Gray
                        }
                    )

                    HealthBadge(
                        label = "NOVA",
                        value = product.nova_group?.toString() ?: "?",
                        containerColor = when (product.nova_group) {
                            1 -> Color(0xFF00AA44)
                            2 -> Color(0xFFFDC300)
                            3 -> Color(0xFFF37021)
                            4 -> Color(0xFFE63E11)
                            else -> Color.Gray
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── SECCIÓN DE ALÉRGENOS ──────────────────────────────────────
                SectionHeader(title = stringResource(id = R.string.allergens_detected), icon = Icons.Outlined.WarningAmber)
                Spacer(modifier = Modifier.height(12.dp))

                if (product.allergens_tags.isNullOrEmpty()) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            stringResource(id = R.string.no_allergens_detected),
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        product.allergens_tags?.forEach { tag ->
                            val cleanTag = tag.replace("en:", "")
                                .replace("es:", "")
                                .replaceFirstChar { it.uppercase() }

                            SuggestionChip(
                                onClick = { },
                                label = { Text(cleanTag, fontWeight = FontWeight.Bold) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = Color(0xFFFFEBEE),
                                    labelColor = Color(0xFFD32F2F)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ── SECCIÓN DE INGREDIENTES ───────────────────────────────────
                SectionHeader(title = stringResource(id = R.string.ingredients_title), icon = Icons.Outlined.Info)
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = product.ingredients_text_es ?: product.ingredients_text ?: stringResource(id = R.string.no_ingredients_available),
                        fontSize = 15.sp,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Justify,
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ── INFORMACIÓN NUTRICIONAL ───────────────────────────────────
                SectionHeader(title = stringResource(id = R.string.nutritional_info), icon = Icons.Outlined.Info)
                Spacer(modifier = Modifier.height(12.dp))

                product.nutriments?.let { nutriments ->
                    NutritionalTable(nutriments)
                } ?: Text(
                    "Información nutricional no disponible",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun NutritionalTable(nutriments: com.example.safebite.model.Nutriments) {
    val items = listOf(
        "Energía" to "${nutriments.energyKcal ?: 0} kcal",
        "Grasas" to "${nutriments.fat ?: 0} g",
        " - Saturadas" to "${nutriments.saturatedFat ?: 0} g",
        "Carbohidratos" to "${nutriments.carbohydrates ?: 0} g",
        " - Azúcares" to "${nutriments.sugars ?: 0} g",
        "Fibra" to "${nutriments.fiber ?: 0} g",
        "Proteínas" to "${nutriments.proteins ?: 0} g",
        "Sal" to "${nutriments.salt ?: 0} g"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        items.forEach { (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(label, fontSize = 14.sp, color = Color.DarkGray)
                Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
            }
            if (label != "Sal") HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
        }
    }
}

@Composable
fun HealthBadge(
    label: String,
    value: String,
    containerColor: Color,
    contentColor: Color = Color.White
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Surface(
            color = containerColor,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = value,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                color = contentColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title.uppercase(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            color = Color.Gray,
            letterSpacing = 1.sp
        )
    }
}
