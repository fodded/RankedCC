package me.fodded.core.commands;

import me.fodded.core.managers.ranks.RankType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {
    String name();
    String usage();
    String description() default "No description provided";
    RankType rank() default RankType.DEFAULT;
}
