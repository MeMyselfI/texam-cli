package de.agentsinaction.texam.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.agentsinaction.texam.TexamCommand;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.nio.file.*;
import java.util.concurrent.Callable;

@Command(name = "exam", description = "Manage exams.",
    mixinStandardHelpOptions = true,
    subcommands = {ExamCommand.ListCommand.class, ExamCommand.GetCommand.class,
        ExamCommand.CreateCommand.class, ExamCommand.UpdateCommand.class,
        ExamCommand.DeleteCommand.class, ExamCommand.VoucherCommand.class,
        ExamCommand.EventsCommand.class, HelpCommand.class})
public class ExamCommand implements Callable<Integer> {
    @ParentCommand TexamCommand parent;
    @Override public Integer call() { new CommandLine(this).usage(System.out); return 0; }

    @Command(name = "list", description = "List all exams. GET /rest/Exam")
    static class ListCommand implements Callable<Integer> {
        @ParentCommand ExamCommand cmd;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient().get("/rest/Exam"));
            return 0;
        }
    }

    @Command(name = "get", description = "Get exam details. GET /rest/Exam/{id}")
    static class GetCommand implements Callable<Integer> {
        @ParentCommand ExamCommand cmd;
        @Parameters(paramLabel = "ID") long id;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient().get("/rest/Exam/" + id));
            return 0;
        }
    }

    @Command(name = "create", description = "Create an exam. POST /rest/Exam")
    static class CreateCommand implements Callable<Integer> {
        @ParentCommand ExamCommand cmd;
        @Option(names = {"--data"}, required = true, description = "Exam JSON") String data;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            JsonNode body = cmd.parent.resolveClient().getMapper().readTree(data);
            cmd.parent.printJson(cmd.parent.resolveClient().post("/rest/Exam", body));
            return 0;
        }
    }

    @Command(name = "update", description = "Update an exam. PUT /rest/Exam/{id}")
    static class UpdateCommand implements Callable<Integer> {
        @ParentCommand ExamCommand cmd;
        @Parameters(paramLabel = "ID") long id;
        @Option(names = {"--data"}, required = true, description = "Exam JSON") String data;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            JsonNode body = cmd.parent.resolveClient().getMapper().readTree(data);
            cmd.parent.printJson(cmd.parent.resolveClient().put("/rest/Exam/" + id, body));
            return 0;
        }
    }

    @Command(name = "delete", description = "Delete an exam. DELETE /rest/Exam/{id}")
    static class DeleteCommand implements Callable<Integer> {
        @ParentCommand ExamCommand cmd;
        @Parameters(paramLabel = "ID") long id;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient().delete("/rest/Exam/" + id));
            return 0;
        }
    }

    @Command(name = "voucher", description = "Download exam voucher as PDF. GET /rest/Exam/{id}/voucher/min/{min}/pdf")
    static class VoucherCommand implements Callable<Integer> {
        @ParentCommand ExamCommand cmd;
        @Parameters(paramLabel = "ID") long id;
        @Option(names = {"--min"}, defaultValue = "60", description = "Exam duration in minutes") int min;
        @Option(names = {"--out", "-o"}, description = "Output PDF file") Path out;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            byte[] data = cmd.parent.resolveClient().getBytes("/rest/Exam/" + id + "/voucher/min/" + min + "/pdf");
            Path target = out != null ? out : Path.of("voucher-" + id + ".pdf");
            Files.write(target, data);
            ObjectNode result = cmd.parent.resolveClient().getMapper().createObjectNode();
            result.put("success", true).put("file", target.toAbsolutePath().toString()).put("bytes", data.length);
            cmd.parent.printJson(result);
            return 0;
        }
    }

    @Command(name = "events", description = "Get events for an exam. GET /rest/Exam/{id}/Events")
    static class EventsCommand implements Callable<Integer> {
        @ParentCommand ExamCommand cmd;
        @Parameters(paramLabel = "ID") long id;
        @Option(names = {"--start"}, defaultValue = "0") int start;
        @Option(names = {"--limit"}, defaultValue = "100") int limit;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient()
                .get("/rest/Exam/" + id + "/Events?start=" + start + "&limit=" + limit));
            return 0;
        }
    }
}
