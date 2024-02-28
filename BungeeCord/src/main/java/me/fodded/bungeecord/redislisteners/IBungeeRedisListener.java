package me.fodded.bungeecord.redislisteners;

public interface IBungeeRedisListener {

    void onMessage(CharSequence channel, Object msg);
}
