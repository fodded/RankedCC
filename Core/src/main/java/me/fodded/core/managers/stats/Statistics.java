package me.fodded.core.managers.stats;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public abstract class Statistics {
    public abstract Statistics getStatistics(UUID uniqueId);
}