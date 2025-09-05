package random.meteor.manager;

import java.util.ArrayList;
import java.util.List;

public class Managers implements Manager {

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
        managers.add(new ModuleManager());
        managers.forEach(Manager::onInitialize);
    }
}
