package de.agentsinaction.texam.commands;

import de.agentsinaction.texam.TexamCommand;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.util.concurrent.Callable;

@Command(name = "media", description = "Access media files.",
    mixinStandardHelpOptions = true,
    subcommands = {MediaCommand.ListCommand.class, HelpCommand.class})
public class MediaCommand implements Callable<Integer> {
    @ParentCommand TexamCommand parent;
    @Override public Integer call() { new CommandLine(this).usage(System.out); return 0; }

    @Command(name = "list", description = "List all media. GET /rest/Media")
    static class ListCommand implements Callable<Integer> {
        @ParentCommand MediaCommand cmd;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient().get("/rest/Media"));
            return 0;
        }
    }
}
