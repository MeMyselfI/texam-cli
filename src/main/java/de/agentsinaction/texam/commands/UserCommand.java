package de.agentsinaction.texam.commands;

import com.fasterxml.jackson.databind.JsonNode;
import de.agentsinaction.texam.TexamCommand;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.util.concurrent.Callable;

@Command(name = "user", description = "Manage backend users.",
    mixinStandardHelpOptions = true,
    subcommands = {UserCommand.ListCommand.class, UserCommand.UpdateCommand.class, HelpCommand.class})
public class UserCommand implements Callable<Integer> {
    @ParentCommand TexamCommand parent;
    @Override public Integer call() { new CommandLine(this).usage(System.out); return 0; }

    @Command(name = "list", description = "List backend users. GET /rest/Backenduser/view/Public")
    static class ListCommand implements Callable<Integer> {
        @ParentCommand UserCommand cmd;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient().get("/rest/Backenduser/view/Public"));
            return 0;
        }
    }

    @Command(name = "update", description = "Update a backend user. PUT /rest/Backenduser/view/Public/{id}")
    static class UpdateCommand implements Callable<Integer> {
        @ParentCommand UserCommand cmd;
        @Parameters(paramLabel = "ID") long id;
        @Option(names = {"--data"}, required = true) String data;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            JsonNode body = cmd.parent.resolveClient().getMapper().readTree(data);
            cmd.parent.printJson(cmd.parent.resolveClient().put("/rest/Backenduser/view/Public/" + id, body));
            return 0;
        }
    }
}
