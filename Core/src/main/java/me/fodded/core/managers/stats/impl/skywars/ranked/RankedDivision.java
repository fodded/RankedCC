package me.fodded.core.managers.stats.impl.skywars.ranked;

public enum RankedDivision {

    NONE("", -1),
    WOOD("&8Wood Division", 101), // > #2501 // > #101
    STONE("&7Stone Division", 75), // #1001-#2500 // #75-#100
    IRON("&fIron Division", 51), // #501-#1000 // #51-#75
    GOLD("&6Gold Division", 26), // #101-#500 // #26-#50
    DIAMOND("&bDiamond Division", 11), // #11-#100 //#11-#25
    MASTERS("&2Masters Division", 1); // #1 - # 10

    private String name;
    private int position;

    RankedDivision(String name, int position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }
}
