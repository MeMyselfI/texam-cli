package de.agentsinaction.texam.commands;

import de.agentsinaction.texam.TexamCommand;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.util.concurrent.Callable;

@Command(name = "conf", description = "Show server configuration.",
    mixinStandardHelpOptions = true,
    subcommands = {ConfCommand.GetCommand.class, HelpCommand.class})
public class ConfCommand implements Callable<Integer> {
    @ParentCommand TexamCommand parent;
    @Override public Integer call() { new CommandLine(this).usage(System.out); return 0; }

    @Command(name = "get", description = "Get server configuration. GET /rest/conf")
    static class GetCommand implements Callable<Integer> {
        @ParentCommand ConfCommand cmd;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient().get("/rest/conf"));
            return 0;
        }
    }
}
