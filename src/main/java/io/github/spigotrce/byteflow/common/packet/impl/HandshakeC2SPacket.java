package io.github.spigotrce.byteflow.common.packet.impl;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import io.github.spigotrce.byteflow.common.packet.Packet;
import io.github.spigotrce.byteflow.common.packet.PacketType;
import io.github.spigotrce.byteflow.server.ClientHandler;

public record HandshakeC2SPacket(int pvn, String token) implements Packet<ClientHandler> {
    public HandshakeC2SPacket(int pvn, String token) {
        this.pvn = pvn;
        this.token = token;
    }

    public HandshakeC2SPacket(ByteArrayDataInput input) {
        this(input.readInt(), input.readUTF());
    }

    @Override
    public PacketType<? extends Packet<ClientHandler>> getPacketId() {
        return new PacketType<HandshakeC2SPacket>("handshake_c2s");
    }

    @Override
    public void apply(ClientHandler listener) {
    }

    @Override
    public void write(ByteArrayDataOutput out) {
        out.writeInt(pvn);
        out.writeUTF(token);
    }
}
