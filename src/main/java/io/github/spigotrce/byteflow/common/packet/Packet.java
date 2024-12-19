package io.github.spigotrce.byteflow.common.packet;

import com.google.common.io.ByteArrayDataOutput;
import io.github.spigotrce.byteflow.common.PacketListener;

public interface Packet<T extends PacketListener> {
    PacketType<? extends Packet<T>> getPacketId();
    void apply(T listener);
    void write(ByteArrayDataOutput out);
}
