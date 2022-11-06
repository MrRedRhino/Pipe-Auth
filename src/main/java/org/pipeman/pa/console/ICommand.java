package org.pipeman.pa.console;

public interface ICommand {
    boolean execute(String command, String... args);
}
