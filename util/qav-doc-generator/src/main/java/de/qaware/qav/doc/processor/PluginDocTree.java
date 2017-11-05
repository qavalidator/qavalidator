package de.qaware.qav.doc.processor;

import com.google.common.collect.Maps;
import de.qaware.qav.doc.model.CommandDoc;
import de.qaware.qav.doc.model.PluginDoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class keeps track of the plugin documentation; the plugin documentation is structured as a simple tree:
 * <pre>
 *     Plugin 1
 *        +-- Command 1.1
 *        |      +-- name,
 *        |      +-- description,
 *        |      +-- parameters
 *        |              + param 1: Name, description
 *        |              + param 2: Name, description
 *        |      +-- result
 *        +-- Command 1.2
 *        +-- Command 1.3
 *     Plugin 2
 *        ... etc.
 * </pre>
 *
 * @author QAware GmbH
 */
public class PluginDocTree {

    private final Map<String, PluginDoc> plugins = Maps.newHashMap();
    private final Map<String, List<CommandDoc>> commands = Maps.newHashMap();

    /**
     * Adds a plugin with the given name.
     *
     * @param name   name
     * @param plugin the plugin
     */
    public void addPlugin(String name, PluginDoc plugin) {
        checkNotNull(name, "name");
        checkNotNull(plugin, "plugin");
        plugins.put(name, plugin);
    }

    /**
     * Adds a command in the given plugin.
     *
     * @param pluginName the plugin name
     * @param command    the {@link CommandDoc}
     */
    public void addCommand(String pluginName, CommandDoc command) {
        checkNotNull(pluginName, "pluginName");
        checkNotNull(command, "command");

        getCommands(pluginName).add(command);
    }

    /**
     * @return a set with all plugin names
     */
    public Set<String> getPluginNames() {
        return plugins.keySet();
    }

    /**
     * Gets the specified {@link PluginDoc}.
     *
     * @param name the name
     * @return the {@link PluginDoc}
     */
    public PluginDoc getPlugin(String name) {
        return plugins.get(name);
    }

    /**
     * Gets the specified command list of all commands which belong to one plugin;
     * it's a list of {@link CommandDoc}s.
     *
     * @param pluginName the plugin name
     * @return the list, or an empty list if it does not exist
     */
    public List<CommandDoc> getCommands(String pluginName) {
        return commands.computeIfAbsent(pluginName, k -> new ArrayList<>());
    }
}
