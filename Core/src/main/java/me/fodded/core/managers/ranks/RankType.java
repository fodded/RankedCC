package me.fodded.core.managers.ranks;

import com.google.gson.annotations.SerializedName;

public enum RankType {

    @SerializedName("ADMIN")
    ADMIN,
    @SerializedName("MODERATOR")
    MODERATOR,
    @SerializedName("HELPER")
    HELPER,
    @SerializedName("MVPPLUS")
    MVPPLUS,
    @SerializedName("MVP")
    MVP,
    @SerializedName("VIPPLUS")
    VIPPLUS,
    @SerializedName("VIP")
    VIP,
    @SerializedName("DEFAULT")
    DEFAULT
}
