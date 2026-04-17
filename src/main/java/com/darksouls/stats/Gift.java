package com.darksouls.stats;

/**
 * Starting gifts from Dark Souls 1. The actual in-world effects (items granted,
 * ring equipped, etc.) are not implemented yet — only the selection + persistence.
 */
public enum Gift {
    NONE            ("Nenhuma"),
    DIVINE_BLESSING ("Bênção Divina"),
    BLACK_FIREBOMB  ("Bombas de Fogo Negras"),
    TWIN_HUMANITIES ("Humanidades Gêmeas"),
    BINOCULARS      ("Binóculo"),
    PENDANT         ("Pingente"),
    MASTER_KEY      ("Chave Mestra"),
    TINY_BEINGS_RING("Anel do Ser Minúsculo"),
    OLD_WITCHS_RING ("Anel da Velha Bruxa"),
    LIFE_RING       ("Anel da Vida");

    public final String displayName;

    Gift(String displayName) {
        this.displayName = displayName;
    }

    public static Gift fromOrdinal(int ord) {
        Gift[] values = values();
        return (ord < 0 || ord >= values.length) ? NONE : values[ord];
    }
}
