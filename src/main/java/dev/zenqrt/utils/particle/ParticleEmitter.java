package dev.zenqrt.utils.particle;

import com.extollit.linalg.mutable.Vec3d;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import org.jetbrains.annotations.Nullable;

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

    public ParticleEmitter(Particle particle, Vec offset, float speed, int count, boolean force) {
        this(particle, offset, speed, count, force, null);
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
        }

        return particlePacket;
    }

}
