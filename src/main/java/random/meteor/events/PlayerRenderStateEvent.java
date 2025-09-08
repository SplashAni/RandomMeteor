package random.meteor.events;

import net.minecraft.client.render.entity.state.PlayerEntityRenderState;

public class PlayerRenderStateEvent {
    PlayerEntityRenderState state;

    public PlayerRenderStateEvent(PlayerEntityRenderState state) {
        this.state = state;
    }

    public PlayerEntityRenderState getState() {
        return state;
    }
}
