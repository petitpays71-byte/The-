package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.NuevaRepository
import com.example.localization.NuevaStrings
import com.example.model.AppLanguage
import com.example.model.MarketplaceProduct
import com.example.ui.components.ElectricButton
import com.example.ui.components.FuturisticCard
import com.example.ui.theme.*

@Composable
fun MarketplaceScreen(
    repository: NuevaRepository
) {
    val lang by repository.currentLanguage.collectAsState()
    val user by repository.currentUser.collectAsState()
    val products by repository.marketplaceProducts.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Tous") }
    var showPublishDialog by remember { mutableStateOf(false) }
    var viewingProduct by remember { mutableStateOf<MarketplaceProduct?>(null) }
    var successNotice by remember { mutableStateOf<String?>(null) }

    val categories = if (lang == AppLanguage.FR) {
        listOf("Tous", "Logiciels & Code", "Design & Graphisme", "E-Books & Données", "Services Pro")
    } else {
        listOf("All", "Software & Code", "Design & Graphics", "E-Books & Data", "Pro Services")
    }

    val filteredProducts = products.filter { prod ->
        val matchesSearch = prod.title.contains(searchQuery, ignoreCase = true) ||
                prod.description.contains(searchQuery, ignoreCase = true) ||
                prod.category.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "Tous" || selectedCategory == "All" || prod.category.contains(selectedCategory, ignoreCase = true)
        matchesSearch && matchesCategory
    }

    Scaffold(
        containerColor = MidnightBase,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showPublishDialog = true },
                containerColor = ElectricBlue,
                contentColor = MidnightBase,
                modifier = Modifier.testTag("publish_product_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Publier un produit")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("marketplace_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (lang == AppLanguage.FR) "Marketplace des Créateurs" else "Creators Marketplace",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite
                        )
                        Text(
                            text = if (lang == AppLanguage.FR)
                                "Achetez et vendez vos créations, templates et services en FCFA."
                            else
                                "Buy and sell creator assets, templates, and services in FCFA.",
                            color = TextLight,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("marketplace_search_input"),
                    placeholder = { Text(if (lang == AppLanguage.FR) "Rechercher un produit, template, service..." else "Search products, templates, services...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null, tint = TextMuted)
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = MidnightBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Category filters
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            modifier = Modifier.clickable { selectedCategory = cat },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) ElectricBlue else MidnightCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ElectricBlue else MidnightBorder)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) MidnightBase else TextLight,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            if (successNotice != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SuccessGreen.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(successNotice!!, color = TextWhite, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            IconButton(onClick = { successNotice = null }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextMuted)
                            }
                        }
                    }
                }
            }

            if (filteredProducts.isEmpty()) {
                item {
                    FuturisticCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = TextMuted, modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (lang == AppLanguage.FR) "Aucun produit ne correspond à votre recherche" else "No products found matching your search",
                                color = TextLight,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            items(filteredProducts, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    lang = lang,
                    onClick = { viewingProduct = product }
                )
            }
        }
    }

    // Publish Product Dialog
    if (showPublishDialog) {
        PublishProductModal(
            isMemberActive = user.isMembershipActive,
            userPhone = user.phone,
            lang = lang,
            onDismiss = { showPublishDialog = false },
            onSubmit = { title, desc, price, cat, phone ->
                repository.publishProduct(title, desc, price, cat, phone)
                showPublishDialog = false
                successNotice = if (user.isMembershipActive)
                    (if (lang == AppLanguage.FR) "Produit publié immédiatement sur la Marketplace !" else "Product published instantly on Marketplace!")
                else
                    (if (lang == AppLanguage.FR) "Produit soumis ! En attente d'approbation administrateur (Adhésion en attente)." else "Product submitted! Pending admin moderation (Membership pending).")
            }
        )
    }

    // View Product Detail Dialog
    if (viewingProduct != null) {
        val prod = viewingProduct!!
        Dialog(onDismissRequest = { viewingProduct = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = RoundedCornerShape(6.dp), color = CyberBlue.copy(alpha = 0.2f)) {
                            Text(
                                text = prod.category,
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        IconButton(onClick = { viewingProduct = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(prod.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                    Text("${prod.price} FCFA", color = ElectricBlue, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(vertical = 4.dp))

                    Text(prod.description, color = TextLight, fontSize = 13.sp, lineHeight = 18.sp)

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = MidnightCardElevated
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(if (lang == AppLanguage.FR) "Vendeur Certifié :" else "Verified Seller:", color = TextMuted, fontSize = 11.sp)
                            Text(prod.sellerName, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Contact : ${prod.sellerPhone}", color = ElectricBlue, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ElectricButton(
                        text = if (lang == AppLanguage.FR) "Contacter le Vendeur via MoMo" else "Contact Seller via MoMo",
                        onClick = { viewingProduct = null },
                        icon = Icons.AutoMirrored.Filled.Chat,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "contact_seller_btn"
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: MarketplaceProduct, lang: AppLanguage, onClick: () -> Unit) {
    FuturisticCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(6.dp), color = CyberBlue.copy(alpha = 0.2f)) {
                        Text(
                            text = product.category,
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (product.isUserProduct) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(6.dp), color = MtnMoMo.copy(alpha = 0.2f)) {
                            Text(
                                text = if (lang == AppLanguage.FR) "MON PRODUIT" else "MY PRODUCT",
                                color = MtnMoMo,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (!product.isApproved) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(6.dp), color = WarningAmber.copy(alpha = 0.2f)) {
                            Text(
                                text = if (lang == AppLanguage.FR) "MODÉRATION" else "PENDING",
                                color = WarningAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(product.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextWhite)
                Text(
                    product.description,
                    color = TextLight,
                    fontSize = 12.sp,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = "Vendeur : ${product.sellerName}",
                    color = TextMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${product.price} FCFA",
                    color = ElectricBlue,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = MtnMoMo, modifier = Modifier.size(13.dp))
                    Text(product.rating.toString(), color = TextLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PublishProductModal(
    isMemberActive: Boolean,
    userPhone: String,
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onSubmit: (title: String, desc: String, price: Long, category: String, phone: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Logiciels & Code") }
    var phone by remember { mutableStateOf(userPhone) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MidnightSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == AppLanguage.FR) "Publier un Produit" else "Publish a Product",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextMuted)
                    }
                }

                if (!isMemberActive) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = WarningAmber.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = if (lang == AppLanguage.FR)
                                "Notice : Votre compte n'a pas encore validé les 1 000 FCFA d'adhésion. Votre produit devra être approuvé manuellement par l'administrateur."
                            else
                                "Notice: Your membership is pending (1,000 FCFA). Your listing will require manual admin approval before public listing.",
                            color = WarningAmber,
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; errorMessage = null },
                    label = { Text(if (lang == AppLanguage.FR) "Titre du produit / service" else "Product / Service Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("product_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = MidnightBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it; errorMessage = null },
                    label = { Text("Description") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("product_desc_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = MidnightBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it; errorMessage = null },
                        label = { Text(if (lang == AppLanguage.FR) "Prix (FCFA)" else "Price (FCFA)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("product_price_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = MidnightBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it; errorMessage = null },
                        label = { Text(if (lang == AppLanguage.FR) "Tél. Vendeur" else "Seller Phone") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = MidnightBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                }

                if (errorMessage != null) {
                    Text(errorMessage!!, color = ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text(NuevaStrings.cancel(lang))
                    }
                    ElectricButton(
                        text = if (lang == AppLanguage.FR) "Publier" else "Publish",
                        onClick = {
                            val parsedPrice = priceStr.toLongOrNull()
                            if (title.isBlank() || description.isBlank()) {
                                errorMessage = if (lang == AppLanguage.FR) "Veuillez remplir le titre et la description." else "Please enter title and description."
                            } else if (parsedPrice == null || parsedPrice <= 0) {
                                errorMessage = if (lang == AppLanguage.FR) "Prix invalide." else "Invalid price."
                            } else {
                                onSubmit(title.trim(), description.trim(), parsedPrice, category, phone.trim())
                            }
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "confirm_publish_product_btn"
                    )
                }
            }
        }
    }
}
