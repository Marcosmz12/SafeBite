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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.safebite.controller.ProductController
import com.example.safebite.ui.theme.GreenPrimary
import com.example.safebite.ui.theme.GreenSoft

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(navController: NavHostController, productController: ProductController) {
    val colors = MaterialTheme.colorScheme

    // Obtenemos el producto del controlador
    val product = productController.scannedProduct.value

    // ── GUARDIÁN: SI EL PRODUCTO ES NULO, MOSTRAMOS CARGA ──────────────────
    if (product == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = GreenPrimary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Buscando información...", color = Color.Gray)
            }
        }
        return // Salimos de la función para no ejecutar el resto
    }

    // A partir de aquí, Kotlin ya sabe que 'product' NO es nulo (Smart Cast)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del producto", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBackIosNew, contentDescription = "Atrás")
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
                .padding(padding) // Usamos el padding del scaffold
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
                    model = product.image_front_url,
                    contentDescription = product.product_name ?: "Producto sin nombre",
                    modifier = Modifier
                        .size(220.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            // ── CUERPO DE INFORMACIÓN ────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = product.product_name ?: "Producto sin nombre",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.onBackground,
                    lineHeight = 32.sp
                )

                Text(
                    text = product.brands ?: product.store ?: "Marca desconocida",
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
                        label = "Procesado NOVA",
                        value = product.nova_group?.toString() ?: "?",
                        containerColor = when (product.nova_group) {
                            1 -> Color(0xFF00AA44)
                            2 -> Color(0xFFFDC300)
                            3 -> Color(0xFFF37021)
                            4 -> Color(0xFFE63E11)
                            else -> Color.Gray
                        }
                    )

                    if (!product.quantity.isNullOrEmpty()) {
                        HealthBadge(
                            label = "Cantidad",
                            value = product.quantity,
                            containerColor = Color(0xFFF5F5F5),
                            contentColor = Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── SECCIÓN DE ALÉRGENOS ──────────────────────────────────────
                SectionHeader(title = "Alérgenos detectados", icon = Icons.Outlined.WarningAmber)
                Spacer(modifier = Modifier.height(12.dp))

                if (product.allergens_tags.isNullOrEmpty()) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "✅ No se han detectado alérgenos conocidos.",
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
                SectionHeader(title = "Ingredientes", icon = Icons.Outlined.Info)
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = product.ingredients_text_es
                            ?: "La lista de ingredientes no está disponible.",
                        fontSize = 15.sp,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Justify,
                        lineHeight = 22.sp
                    )
                }
            }
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