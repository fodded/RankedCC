package me.fodded.core.managers.ranks;

import lombok.Getter;

@Getter
public class Rank {

    private RankType rank;
    private String prefix;
    private int priority;

    public Rank(RankType rank, String prefix, int priority) {
        this.rank = rank;
        this.prefix = prefix;
        this.priority = priority;
    }
}