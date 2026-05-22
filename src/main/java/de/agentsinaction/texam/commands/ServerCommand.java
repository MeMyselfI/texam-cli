package de.agentsinaction.texam.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.agentsinaction.texam.TexamCommand;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.nio.file.*;
import java.util.concurrent.Callable;

@Command(name = "server", description = "Server management.",
    mixinStandardHelpOptions = true,
    subcommands = {ServerCommand.StatusCommand.class, ServerCommand.BackupCommand.class, HelpCommand.class})
public class ServerCommand implements Callable<Integer> {
    @ParentCommand TexamCommand parent;
    @Override public Integer call() { new CommandLine(this).usage(System.out); return 0; }

    @Command(name = "status", description = "Get server status (memory, disk). GET /rest/status")
    static class StatusCommand implements Callable<Integer> {
        @ParentCommand ServerCommand cmd;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient().get("/rest/status"));
            return 0;
        }
    }

    @Command(name = "backup", description = "Download backup of all files/screenshots as ZIP. GET /rest/files/backup")
    static class BackupCommand implements Callable<Integer> {
        @ParentCommand ServerCommand cmd;
        @Option(names = {"--out", "-o"}) Path out;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            byte[] data = cmd.parent.resolveClient().getBytes("/rest/files/backup");
            Path target = out != null ? out : Path.of("texam-backup.zip");
            Files.write(target, data);
            ObjectNode result = cmd.parent.resolveClient().getMapper().createObjectNode();
            result.put("success", true).put("file", target.toAbsolutePath().toString()).put("bytes", data.length);
            cmd.parent.printJson(result);
            return 0;
        }
    }
}
