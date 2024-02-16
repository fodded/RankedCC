package me.fodded.core.utils.redis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufOutputStream;
import lombok.RequiredArgsConstructor;
import me.fodded.core.utils.mongodb.GsonSerializer;
import org.redisson.client.protocol.Encoder;

import java.io.IOException;

@RequiredArgsConstructor
public class GsonEncoder implements Encoder {

    private final GsonSerializer gsonSerializer;

    @Override
    public ByteBuf encode(Object in) throws IOException {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        try (ByteBufOutputStream os = new ByteBufOutputStream(out)) {
            os.writeUTF(gsonSerializer.toJson(in));
            os.writeUTF(in.getClass().getName());
        } catch (Exception e) {
            throw new IOException(e);
        }
        return out;
    }
}