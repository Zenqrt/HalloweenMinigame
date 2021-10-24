package dev.zenqrt.utils.particle;

import com.extollit.linalg.mutable.Vec3d;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.utils.binary.BinaryWriter;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ParticleEmitter {

    private Particle particle;
    private Vec offset;
    private float speed;
    private int count;
    private boolean force;
    private byte[] data;

    public ParticleEmitter(Particle particle, Vec offset, float speed, int count, boolean force, byte[] data) {
        this.particle = particle;
        this.offset = offset;
        this.speed = speed;
        this.count = count;
        this.force = force;
        this.data = data;
    }

    public ParticleEmitter(Particle particle, Vec offset, float speed, int count, boolean force, Consumer<BinaryWriter> dataWriter) {
        this(particle, offset, speed, count, force, (byte[]) null);

        if(dataWriter != null) {
            var writer = new BinaryWriter();
            dataWriter.accept(writer);
            this.data = writer.toByteArray();
        }
    }

    public ParticleEmitter(Particle particle, Vec offset, float speed, int count, boolean force) {
        this(particle, offset, speed, count, force, (byte[]) null);
    }

    public ParticlePacket createPacket(Point position) {
        var particlePacket = new ParticlePacket();
        particlePacket.particleId = particle.id();
        particlePacket.particleCount = count;
        particlePacket.particleData = speed;
        particlePacket.longDistance = force;
        particlePacket.x = position.x();
        particlePacket.y = position.y();
        particlePacket.z = position.z();
        particlePacket.offsetX = (float) offset.x();
        particlePacket.offsetY = (float) offset.y();
        particlePacket.offsetZ = (float) offset.z();

        if(data != null) {
            particlePacket.data = data;
        } else {
            particlePacket.data = new byte[0];
        }

        return particlePacket;
    }

}
