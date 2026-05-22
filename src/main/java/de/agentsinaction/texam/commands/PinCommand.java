package de.agentsinaction.texam.commands;

import de.agentsinaction.texam.TexamCommand;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.util.concurrent.Callable;

@Command(name = "pin", description = "Manage exam access PINs.",
    mixinStandardHelpOptions = true,
    subcommands = {PinCommand.ListCommand.class, PinCommand.GetCommand.class, HelpCommand.class})
public class PinCommand implements Callable<Integer> {
    @ParentCommand TexamCommand parent;
    @Override public Integer call() { new CommandLine(this).usage(System.out); return 0; }

    @Command(name = "list", description = "List all PINs. GET /rest/Pin")
    static class ListCommand implements Callable<Integer> {
        @ParentCommand PinCommand cmd;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient().get("/rest/Pin"));
            return 0;
        }
    }

    @Command(name = "get", description = "Get a PIN by ID. GET /rest/Pin/{id}")
    static class GetCommand implements Callable<Integer> {
        @ParentCommand PinCommand cmd;
        @Parameters(paramLabel = "ID") long id;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient().get("/rest/Pin/" + id));
            return 0;
        }
    }
}
