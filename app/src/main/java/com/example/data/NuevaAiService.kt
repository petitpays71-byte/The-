package com.example.data

import com.example.BuildConfig
import com.example.model.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class NuevaAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateAdvice(prompt: String, language: AppLanguage): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        val systemPrompt = when (language) {
            AppLanguage.FR -> """
                Tu es l'Assistant Stratégique IA de NUEVA TECHNOLOGY, la plateforme d'émancipation pour les jeunes entrepreneurs, créateurs et apprenants africains (spécialement au Cameroun et en Afrique centrale/ouest).
                Ton rôle :
                1. Donner des conseils pragmatiques, réalistes et structurés sur les affaires, le e-commerce, l'intégration Mobile Money (Orange Money, MTN MoMo), le freelancing tech, et le marketing digital adapté au contexte africain.
                2. Répondre en français fluide, encourageant, avec des puces claires et des étapes concrètes.
                3. Mentionner les devises en FCFA quand c'est pertinent.
            """.trimIndent()
            AppLanguage.EN -> """
                You are the AI Business Advisor of NUEVA TECHNOLOGY, empowering young African entrepreneurs, creators, and learners (especially in Cameroon and across Africa).
                Your role:
                1. Provide pragmatic, actionable, structured advice on business, e-commerce, Mobile Money integration (Orange Money, MTN MoMo), tech freelancing, and digital marketing tailored to the African reality.
                2. Respond in clear, encouraging English with bullet points and action items.
                3. Reference figures in FCFA where relevant.
            """.trimIndent()
        }

        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext fallbackLocalAdvisor(prompt, language)
        }

        try {
            val rootJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val sysInstructionObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().put("text", systemPrompt))
                    }
                    put("parts", partsArray)
                }
                put("systemInstruction", sysInstructionObj)
            }

            val requestBody = rootJson.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    return@withContext fallbackLocalAdvisor(prompt, language, "Note API: Code ${response.code}")
                }

                val responseBodyStr = response.body?.string() ?: ""
                val jsonResponse = JSONObject(responseBodyStr)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", "Pas de réponse reçue.")
                    }
                }
                return@withContext fallbackLocalAdvisor(prompt, language)
            }
        } catch (e: Exception) {
            return@withContext fallbackLocalAdvisor(prompt, language, e.message)
        }
    }

    private fun fallbackLocalAdvisor(prompt: String, language: AppLanguage, note: String? = null): String {
        val lower = prompt.lowercase()
        val isFr = language == AppLanguage.FR

        val response = when {
            lower.contains("momo") || lower.contains("orange") || lower.contains("paiement") || lower.contains("payment") -> {
                if (isFr) """
                    💡 **Guide Intégration Mobile Money (Orange Money & MTN MoMo) au Cameroun :**
                    
                    1. **Choix du mode d'encaissement :**
                       - *Marchand direct :* Compte Marchand Orange (#150#) ou MTN MoMo (*126#) avec frais réduits (~1.5% - 2%).
                       - *Agrégateur (Monetbil, CinetPay, Notch Pay) :* Une seule API pour encaisser les deux opérateurs simultanément.
                    2. **Sécurisation des transactions :**
                       - Utilisez toujours un Webhook/IPN avec vérification de signature SHA256 côté serveur.
                       - Ne validez jamais une commande sur une simple notification SMS ou capture d'écran client.
                    3. **Conseil NUEVA :**
                       - Intégrez un parcours clair avec pré-remplissage du numéro et rappel de validation USSD sous 60 secondes.
                """.trimIndent()
                else """
                    💡 **Mobile Money Integration Guide (Orange Money & MTN MoMo) in Cameroon:**
                    
                    1. **Collection method:**
                       - *Direct Merchant:* Orange Merchant (#150#) or MTN MoMo (*126#) with low fees (~1.5% - 2%).
                       - *Aggregator (Monetbil, Notch Pay, CinetPay):* Unified API to accept both operators at once.
                    2. **Transaction Security:**
                       - Always verify server-side callbacks/IPNs with cryptographic signatures.
                       - Never approve orders based on customer screenshots or raw SMS.
                    3. **NUEVA Tip:**
                       - Provide clear USSD prompt countdown and auto-status checking.
                """.trimIndent()
            }
            lower.contains("startup") || lower.contains("idée") || lower.contains("idea") || lower.contains("projet") -> {
                if (isFr) """
                    🚀 **3 Idées de Startups Rentables pour Jeunes Africains (Budget < 150 000 FCFA) :**
                    
                    1. **Micro-logistique de proximité & E-Commerce local :**
                       - Livraison express de denrées fraîches du marché aux ménages urbains (Douala, Yaoundé).
                       - Commandes via WhatsApp Business + Paiement Mobile Money.
                    2. **Agence de Création de Contenu & Automatisation IA :**
                       - Aider les commerces locaux à créer des visuels, reels et fiches produits avec les outils d'IA.
                    3. **Plateforme de Cours & Coaching de Compétences Pratiques :**
                       - Monétiser votre expertise (artisanat, programmation, bureautique) avec des sessions payantes à 500 - 2 000 FCFA.
                """.trimIndent()
                else """
                    🚀 **3 Profitable Startup Ideas for African Youth (Budget < 150,000 FCFA):**
                    
                    1. **Last-Mile Local Logistics & Hyperlocal E-Commerce:**
                       - Direct delivery of fresh market produce to urban households via WhatsApp + Mobile Money.
                    2. **AI Content & Social Media Agency:**
                       - Producing reels, graphics, and customer service automation for local businesses.
                    3. **Skill-Sharing & Micro-Workshops:**
                       - Monetize practical digital or vocational skills with 500 - 2,000 FCFA group workshops.
                """.trimIndent()
            }
            lower.contains("pitch") || lower.contains("financement") || lower.contains("investisseur") || lower.contains("funding") -> {
                if (isFr) """
                    🎯 **Structure d'un Pitch d'Impact pour Entreprendre en Afrique :**
                    
                    1. **L'Accroche (30s) :** Quel est le problème douloureux que vous résolvez sur votre marché local ?
                    2. **La Solution & Traction (45s) :** Votre produit, vos premiers clients (ex: 50 utilisateurs payants).
                    3. **Le Modèle Économique :** Prix, commissions Mobile Money, marge nette.
                    4. **La Demande :** Montant recherché et utilisation exacte des fonds.
                """.trimIndent()
                else """
                    🎯 **High-Impact Pitch Structure for African Startups:**
                    
                    1. **The Hook (30s):** What painful local problem are you solving?
                    2. **Solution & Traction (45s):** Your product and existing customer proofs.
                    3. **Business Model:** Pricing, Mobile Money collection, unit margins.
                    4. **The Ask:** Capital needed and exact milestones it will unlock.
                """.trimIndent()
            }
            else -> {
                if (isFr) """
                    💡 **Conseil Stratégique NUEVA TECHNOLOGY :**
                    
                    Pour réussir votre aventure entrepreneuriale ou créative en Afrique :
                    • **Validez avant de construire :** Parlez à 20 clients potentiels avant de dépenser un seul franc.
                    • **Maîtrisez le cash-flow :** Exigez des acomptes via Orange/MTN MoMo pour éviter les impayés.
                    • **Formez-vous en continu :** Utilisez les formations NUEVA pour monter en compétences sur la tech et le commerce digital.
                    
                    *Posez-moi une question précise sur vos tarifs, votre cible ou votre stratégie marketing !*
                """.trimIndent()
                else """
                    💡 **NUEVA TECHNOLOGY Strategic Recommendation:**
                    
                    To succeed as a creator or entrepreneur in Africa:
                    • **Validate before you build:** Talk to 20 prospective buyers before investing funds.
                    • **Protect your cash flow:** Require upfront deposits through Mobile Money.
                    • **Upskill constantly:** Leverage NUEVA masterclasses in tech, design, and digital business.
                    
                    *Ask me a specific question about your pricing, market target, or marketing strategy!*
                """.trimIndent()
            }
        }

        return if (note != null) "$response\n\n*(Info: $note)*" else response
    }
}
