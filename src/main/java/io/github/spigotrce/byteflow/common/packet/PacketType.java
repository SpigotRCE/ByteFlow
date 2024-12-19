package io.github.spigotrce.byteflow.common.packet;

public record PacketType<T extends Packet<?>>(String id) {
    public PacketType(String id) {
        this.id = id;
    }

    public String toString() {
        return id;
    }
}
