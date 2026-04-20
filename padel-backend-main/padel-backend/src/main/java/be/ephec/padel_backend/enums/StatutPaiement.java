package be.ephec.padel_backend.enums;

public enum StatutPaiement {
    EN_ATTENTE,   // paiement initié mais pas encore confirmé
    VALIDE,       // paiement confirmé → place confirmée
    REMBOURSE     // place libérée (ex: non payé la veille → remboursement)
}
