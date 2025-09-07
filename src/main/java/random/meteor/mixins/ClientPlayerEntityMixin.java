package random.meteor.mixins;

import com.mojang.authlib.GameProfile;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import random.meteor.events.RotationSendPacketEvent;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    public void sendMovementPackets(ClientPlayNetworkHandler instance, Packet<?> packet) {
        RotationSendPacketEvent event = new RotationSendPacketEvent();
// manually redirect the yaws and pithcs to the value send from the client  so even IF meteor sends their bombaclat packets we end up redirecting it anyways RM priority rotation HAS to be first


        if (MeteorClient.EVENT_BUS.post(event).isCancelled()) {
            packet = (packet instanceof PlayerMoveC2SPacket.Full)
                ? new PlayerMoveC2SPacket.Full(getPos(), event.getRedirectYaw(), event.getRedirectPitch(), isOnGround(), horizontalCollision)
                : (packet instanceof PlayerMoveC2SPacket.LookAndOnGround)
                ? new PlayerMoveC2SPacket.LookAndOnGround(event.getRedirectYaw(), event.getRedirectPitch(), isOnGround(), horizontalCollision)
                : packet;
        }

        instance.sendPacket(packet);

    }
}
