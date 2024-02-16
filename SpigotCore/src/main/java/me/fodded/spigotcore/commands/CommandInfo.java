package me.fodded.spigotcore.commands;

import me.fodded.core.managers.ranks.Rank;

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
    Rank rank() default Rank.DEFAULT;
}
