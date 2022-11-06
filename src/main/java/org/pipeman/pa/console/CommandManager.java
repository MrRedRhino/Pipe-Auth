package org.pipeman.pa.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.in;

public class CommandManager {
    private static final Map<String, ICommand> commands = new HashMap<>();

    public static void init() {
        Thread thread = new Thread(CommandManager::readTerminal);
        thread.setDaemon(true);
        thread.start();

        registerCommand(new CommandReload(), "reload");
    }

    private static String readLine() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            String line = reader.readLine();
            if (line.equalsIgnoreCase("q")) System.exit(0);
            return line;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void readTerminal() {
        while (true) {
            String line = readLine();
            String[] split = line.split(" ");
            ICommand command = commands.get(line);
            boolean handled = false;
            try {
                handled = command.execute(split[0], Arrays.copyOfRange(split, 1, split.length));
            } catch (Exception ignored) {
            }
            if (!handled) System.out.println("Unknown command: " + line);
        }
    }

    public static void registerCommand(ICommand command, String name) {
        commands.put(name, command);
    }
}
