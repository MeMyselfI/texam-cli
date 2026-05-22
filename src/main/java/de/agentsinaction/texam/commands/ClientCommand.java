package de.agentsinaction.texam.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.agentsinaction.texam.TexamCommand;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.util.concurrent.Callable;

@Command(name = "client", description = "Manage connected exam devices.",
    mixinStandardHelpOptions = true,
    subcommands = {ClientCommand.ListCommand.class, ClientCommand.RemoveExamsCommand.class,
        ClientCommand.RemoveDisconnectedCommand.class, ClientCommand.LogsCommand.class,
        ClientCommand.AliasCommand.class, HelpCommand.class})
public class ClientCommand implements Callable<Integer> {
    @ParentCommand TexamCommand parent;
    @Override public Integer call() { new CommandLine(this).usage(System.out); return 0; }

    @Command(name = "list", description = "List all connected clients. GET /rest/Client")
    static class ListCommand implements Callable<Integer> {
        @ParentCommand ClientCommand cmd;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient().get("/rest/Client"));
            return 0;
        }
    }

    @Command(name = "remove-exams", description = "Remove exams from all clients. POST /rest/clients/remove-exams")
    static class RemoveExamsCommand implements Callable<Integer> {
        @ParentCommand ClientCommand cmd;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient().post("/rest/clients/remove-exams", "{}"));
            return 0;
        }
    }

    @Command(name = "remove-disconnected", description = "Remove disconnected clients. POST /rest/clients/remove-disconnected")
    static class RemoveDisconnectedCommand implements Callable<Integer> {
        @ParentCommand ClientCommand cmd;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient().post("/rest/clients/remove-disconnected", "{}"));
            return 0;
        }
    }

    @Command(name = "logs", description = "Trigger log download from a client. POST /rest/client/{id}/download-logs")
    static class LogsCommand implements Callable<Integer> {
        @ParentCommand ClientCommand cmd;
        @Parameters(paramLabel = "CLIENT_ID") String id;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient().post("/rest/client/" + id + "/download-logs", "{}"));
            return 0;
        }
    }

    @Command(name = "alias", description = "Set alias/name for a client. POST /rest/Client/{id}/alias/{alias}")
    static class AliasCommand implements Callable<Integer> {
        @ParentCommand ClientCommand cmd;
        @Parameters(paramLabel = "CLIENT_ID", index = "0") String id;
        @Parameters(paramLabel = "ALIAS", index = "1") String alias;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient().post("/rest/Client/" + id + "/alias/" + alias, "{}"));
            return 0;
        }
    }
}
