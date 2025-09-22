package random.meteor.manager;

import random.meteor.global.GlobalSettingGroupManager;
import random.meteor.util.player.RotationUtil;
import random.meteor.util.render.RenderUtil;
import random.meteor.util.world.BlockUtil;

import java.util.ArrayList;
import java.util.List;

public class Managers extends Manager {

    private final List<Manager> managers = new ArrayList<>();

    private void register(Manager manager) {
        if (!managers.contains(manager)) managers.add(manager);
    }
    public <T extends Manager> T getManager(Class<T> klass) {
        return managers.stream()
            .filter(klass::isInstance).findFirst()
            .map(klass::cast).orElse(null);
    }

    @Override
    public void onInitialize() {
        register(new ModuleManager());
        register(new GlobalSettingGroupManager());
        register(new BlockUtil());
        register(new RotationUtil());
        register(new RenderUtil());
        managers.forEach(Manager::onInitialize);

    }
}
