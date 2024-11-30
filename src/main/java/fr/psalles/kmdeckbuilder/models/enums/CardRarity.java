package fr.psalles.kmdeckbuilder.models.enums;

public enum CardRarity {
    COMMUNE(30),
    PEU_COMMUNE(50),
    RARE(100),
    KROSMIQUE(400),
    INFINITE(1000);

    private final int dust;

    public int getDust() {
        return dust;
    }

    CardRarity(int dust) {
        this.dust = dust;
    }
}
