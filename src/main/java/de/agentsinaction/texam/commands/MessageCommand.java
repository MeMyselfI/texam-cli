package de.agentsinaction.texam.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.agentsinaction.texam.TexamCommand;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.nio.file.*;
import java.util.concurrent.Callable;

@Command(name = "message", description = "Access client messages.",
    mixinStandardHelpOptions = true,
    subcommands = {MessageCommand.ListCommand.class, MessageCommand.DownloadCommand.class, HelpCommand.class})
public class MessageCommand implements Callable<Integer> {
    @ParentCommand TexamCommand parent;
    @Override public Integer call() { new CommandLine(this).usage(System.out); return 0; }

    @Command(name = "list", description = "List messages. GET /rest/Message")
    static class ListCommand implements Callable<Integer> {
        @ParentCommand MessageCommand cmd;
        @Option(names = {"--start"}, defaultValue = "0") int start;
        @Option(names = {"--limit"}, defaultValue = "100") int limit;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient()
                .get("/rest/Message?start=" + start + "&limit=" + limit));
            return 0;
        }
    }

    @Command(name = "download", description = "Download all messages as Excel. GET /rest/download-all-messages")
    static class DownloadCommand implements Callable<Integer> {
        @ParentCommand MessageCommand cmd;
        @Option(names = {"--lang"}, defaultValue = "de") String lang;
        @Option(names = {"--out", "-o"}) Path out;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            byte[] data = cmd.parent.resolveClient().getBytes("/rest/download-all-messages?lang=" + lang);
            Path target = out != null ? out : Path.of("messages.xlsx");
            Files.write(target, data);
            ObjectNode result = cmd.parent.resolveClient().getMapper().createObjectNode();
            result.put("success", true).put("file", target.toAbsolutePath().toString()).put("bytes", data.length);
            cmd.parent.printJson(result);
            return 0;
        }
    }
}
