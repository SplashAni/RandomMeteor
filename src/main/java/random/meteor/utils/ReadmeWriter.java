package random.meteor.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import static random.meteor.Main.MANAGER;

public class ReadmeWriter {
    public void write() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("test.md"));
        writer.write(getReadme());
    }

    public String getReadme() {

        StringBuilder builder = new StringBuilder();

        builder.append("# Random Meteor Addon\n" +
            "\n" +
            "An addon that adds random but useful modules to meteor\n" +
            "\n" +
            "[Discord (suggest stuff)](https://discord.gg/dNyVgyvsYG)\n" +
            "\n" +
            "### Modules");

        MANAGER.modules.forEach(mod -> {
            if (mod.showcase != null)
                builder.append(String.format("- **[%s](%s)**: %s\n", getFormatted(mod.name), mod.showcase, mod.description));
            else {
                builder.append(String.format("- **%s**: %s\n", getFormatted(mod.name), mod.description));
            }
        });

        MANAGER.commands.forEach(command -> builder.append(String.format("- **%s**: %s\n", command.getName(), command.getDescription())));


        return builder.toString();
    }

    public static String getFormatted(String name) {
        return Arrays.stream(name.split("-"))
            .filter(part -> !part.isEmpty())
            .map(part -> Character.toUpperCase(part.charAt(0)) + (part.length() > 1 ? part.substring(1).toLowerCase() : ""))
            .reduce((part1, part2) -> part1 + " " + part2)
            .orElse("");
    }
}
