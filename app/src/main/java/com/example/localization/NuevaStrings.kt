package com.example.localization

import com.example.model.AppLanguage

object NuevaStrings {

    fun appName(): String = "NUEVA TECHNOLOGY"

    fun slogan(lang: AppLanguage): String = when (lang) {
        AppLanguage.FR -> "Propulser la nouvelle génération d'entrepreneurs & créateurs africains"
        AppLanguage.EN -> "Empowering the next generation of African entrepreneurs & creators"
    }

    // Tabs
    fun tabHome(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Accueil" else "Home"
    fun tabCourses(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Formations" else "Courses"
    fun tabMarketplace(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Marketplace" else "Market"
    fun tabWallet(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Portefeuille" else "Wallet"
    fun tabAi(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Assistant IA" else "AI Advisor"

    // Top & Menu items
    fun adminDashboard(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Administration" else "Admin Dashboard"
    fun referral(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Parrainage" else "Referrals"
    fun cryptoInfo(lang: AppLanguage): String = if (lang == AppLanguage.FR) "NUEVA CRYPTO" else "NUEVA CRYPTO"
    fun profile(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Mon Profil" else "My Profile"
    fun help(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Aide & FAQ" else "Help & FAQ"
    fun terms(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Conditions d'utilisation" else "Terms of Service"
    fun privacy(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Politique de confidentialité" else "Privacy Policy"

    // Membership & Rules
    fun membershipActive(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Membre Vérifié" else "Verified Member"
    fun membershipPending(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Activation en attente (1 000 FCFA)" else "Pending Activation (1,000 FCFA)"
    fun membershipNotice(lang: AppLanguage): String = when (lang) {
        AppLanguage.FR -> "L'activation du compte membre nécessite des frais d'enregistrement de 1 000 FCFA, vérifiés manuellement par l'administration ou via Mobile Money."
        AppLanguage.EN -> "Account membership activation requires a 1,000 FCFA registration fee, manually verified by administration or through Mobile Money."
    }
    fun activateMembership(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Activer mon compte (1 000 FCFA)" else "Activate Membership (1,000 FCFA)"

    // Wallet & Payments
    fun balance(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Solde Disponible" else "Available Balance"
    fun pendingBalance(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Solde en Attente" else "Pending Balance"
    fun deposit(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Dépôt Mobile Money" else "Mobile Money Deposit"
    fun withdraw(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Demander un Retrait" else "Request Withdrawal"
    fun minWithdrawalNotice(minAmount: Long, lang: AppLanguage): String = when (lang) {
        AppLanguage.FR -> "Montant minimum de retrait : $minAmount FCFA. Tout retrait nécessite l'approbation d'un administrateur."
        AppLanguage.EN -> "Minimum withdrawal request: $minAmount FCFA. All withdrawals require administrator approval."
    }
    fun orangeMoney(lang: AppLanguage): String = "Orange Money Cameroun"
    fun mtnMomo(lang: AppLanguage): String = "MTN Mobile Money Cameroun"
    fun ussdHelp(provider: String, lang: ComposeStringLangHelper): String = ""

    // Rules
    fun businessRulesTitle(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Règles & Tarifs Officiels" else "Official Rules & Pricing"
    fun businessRulesText(regFee: Long, confPrice: Long, refReward: Long, minWith: Long, lang: AppLanguage): String = when (lang) {
        AppLanguage.FR -> """
            • Frais d'adhésion : $regFee FCFA (activation unique après validation)
            • Conférences & Formations Premium : $confPrice FCFA (prix fixé par l'admin)
            • Récompense de parrainage : $refReward FCFA par filleul éligible et vérifié
            • Retrait minimum : $minWith FCFA (validation admin obligatoire)
            • Sécurité : Aucun paiement n'est validé sur simple capture d'écran. Les utilisateurs ne peuvent pas approuver leurs propres opérations.
        """.trimIndent()
        AppLanguage.EN -> """
            • Membership fee: $regFee FCFA (one-time activation upon validation)
            • Premium conferences & training: $confPrice FCFA (configured by admin)
            • Referral reward: $refReward FCFA per verified eligible referral
            • Minimum withdrawal: $minWith FCFA (mandatory admin approval)
            • Security: No payment is approved based solely on a screenshot. Users cannot approve their own transactions.
        """.trimIndent()
    }

    // AI Advisor
    fun aiAdvisorTitle(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Assistant Stratégique IA" else "AI Business Advisor"
    fun aiAdvisorSubtitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.FR -> "Propulsé par Google Gemini pour vous conseiller sur vos projets, finances et idées de startups en Afrique."
        AppLanguage.EN -> "Powered by Google Gemini to guide your projects, finances, and startup ideas in Africa."
    }
    fun askAiPlaceholder(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Posez une question à l'IA NUEVA..." else "Ask the NUEVA AI a question..."
    fun send(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Envoyer" else "Send"

    // Crypto
    fun cryptoDisclaimerTitle(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Avertissement Légal & Réglementaire" else "Legal & Regulatory Disclaimer"
    fun cryptoDisclaimer(lang: AppLanguage): String = when (lang) {
        AppLanguage.FR -> "Cette section NUEVA CRYPTO est purement informative et éducative. Conformément à la législation en vigueur en zone CEMAC/Cameroun, aucune transaction, placement ou investissement en cryptomonnaie réelle n'est autorisé sur la plateforme."
        AppLanguage.EN -> "This NUEVA CRYPTO section is strictly informational and educational. In accordance with regulations in the CEMAC region and Cameroon, no real-money crypto trading, placement, or investment is enabled on this platform."
    }

    // Common actions
    fun cancel(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Annuler" else "Cancel"
    fun confirm(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Confirmer" else "Confirm"
    fun submit(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Soumettre" else "Submit"
    fun close(lang: AppLanguage): String = if (lang == AppLanguage.FR) "Fermer" else "Close"
    fun testModeBadge(lang: AppLanguage): String = if (lang == AppLanguage.FR) "MODE TEST CAMEROUN" else "CAMEROON TEST MODE"
    fun switchLanguage(lang: AppLanguage): String = if (lang == AppLanguage.FR) "English" else "Français"
}

typealias ComposeStringLangHelper = AppLanguage
