package org.pipeman.pa.console;

import org.pipeman.pa.users.Users;

public class CommandReload implements ICommand {
    @Override
    public boolean execute(String command, String... args) {
        System.out.println("Reloading users.json...");
        try {
            Users.reloadCache();
            System.out.println("Reload successful");
        } catch (Exception e) {
            System.err.println("Failed to reload users.json");
            e.printStackTrace();
        }
        return true;
    }
}
