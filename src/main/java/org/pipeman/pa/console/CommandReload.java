package org.pipeman.pa.console;

import org.pipeman.pa.config.Config;
import org.pipeman.pa.users.Users;

public class CommandReload implements ICommand {
    @Override
    public boolean execute(String... args) {
        if (args.length != 1) {
            System.out.println("Usage: reload <users|config>");
            return true;
        }

        switch (args[0]) {
            case "users" -> {
                System.out.println("Reloading users.json...");
                try {
                    Users.reloadCache();
                    System.out.println("Reload successful");
                } catch (Exception e) {
                    System.err.println("Failed to reload users.json");
                    e.printStackTrace();
                }
            }
            case "config" -> {
                System.out.println("Reloading config.json...");
                int serverPort = Config.conf().serverPort;
                Config.INSTANCE.reload();
                if (Config.conf().serverPort != serverPort)
                    System.out.println("Server port changed, restart the server for that change to take effect");
                System.out.println("Reload successful");
            }
            default -> System.out.println("Usage: reload <users|config>");
        }
        return true;
    }
}
