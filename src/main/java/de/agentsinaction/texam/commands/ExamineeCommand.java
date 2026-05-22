package de.agentsinaction.texam.commands;

import com.fasterxml.jackson.databind.JsonNode;
import de.agentsinaction.texam.TexamCommand;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.util.concurrent.Callable;

@Command(name = "examinee", description = "Manage examinees (students).",
    mixinStandardHelpOptions = true,
    subcommands = {ExamineeCommand.ListCommand.class, ExamineeCommand.GetCommand.class,
        ExamineeCommand.CreateCommand.class, ExamineeCommand.DeleteCommand.class,
        ExamineeCommand.AssignCommand.class, ExamineeCommand.RemoveCommand.class,
        HelpCommand.class})
public class ExamineeCommand implements Callable<Integer> {
    @ParentCommand TexamCommand parent;
    @Override public Integer call() { new CommandLine(this).usage(System.out); return 0; }

    @Command(name = "list", description = "List all examinees. GET /rest/Examinee")
    static class ListCommand implements Callable<Integer> {
        @ParentCommand ExamineeCommand cmd;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient().get("/rest/Examinee"));
            return 0;
        }
    }

    @Command(name = "get", description = "Get examinee details. GET /rest/Examinee/{id}")
    static class GetCommand implements Callable<Integer> {
        @ParentCommand ExamineeCommand cmd;
        @Parameters(paramLabel = "ID") long id;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient().get("/rest/Examinee/" + id));
            return 0;
        }
    }

    @Command(name = "create", description = "Create an examinee. POST /rest/Examinee")
    static class CreateCommand implements Callable<Integer> {
        @ParentCommand ExamineeCommand cmd;
        @Option(names = {"--data"}, required = true, description = "Examinee JSON") String data;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            JsonNode body = cmd.parent.resolveClient().getMapper().readTree(data);
            cmd.parent.printJson(cmd.parent.resolveClient().post("/rest/Examinee", body));
            return 0;
        }
    }

    @Command(name = "delete", description = "Delete an examinee. DELETE /rest/Examinee/{id}")
    static class DeleteCommand implements Callable<Integer> {
        @ParentCommand ExamineeCommand cmd;
        @Parameters(paramLabel = "ID") long id;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient().delete("/rest/Examinee/" + id));
            return 0;
        }
    }

    @Command(name = "assign", description = "Assign examinees to exams. POST /rest/examinees/{ids}/exams/{ids}/{mode}")
    static class AssignCommand implements Callable<Integer> {
        @ParentCommand ExamineeCommand cmd;
        @Option(names = {"--examinee-ids"}, required = true, description = "Comma-separated examinee IDs") String examineeIds;
        @Option(names = {"--exam-ids"}, required = true, description = "Comma-separated exam IDs") String examIds;
        @Option(names = {"--mode"}, defaultValue = "associate-strict",
                description = "Mode: associate-strict, associate") String mode;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            String path = "/rest/examinees/" + examineeIds + "/exams/" + examIds + "/" + mode;
            cmd.parent.printJson(cmd.parent.resolveClient().post(path, "{}"));
            return 0;
        }
    }

    @Command(name = "remove", description = "Remove examinee from exam. DELETE /rest/Exam/{examID}/Examinee/{id}")
    static class RemoveCommand implements Callable<Integer> {
        @ParentCommand ExamineeCommand cmd;
        @Parameters(paramLabel = "EXAMINEE_ID") long id;
        @Option(names = {"--exam-id"}, required = true, description = "Exam ID") long examId;
        @Option(names = {"--with-logs"}, description = "Also delete associated logs") boolean withLogs;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            String path = "/rest/Exam/" + examId + "/Examinee/" + id + (withLogs ? "/with-logs" : "");
            cmd.parent.printJson(cmd.parent.resolveClient().delete(path));
            return 0;
        }
    }
}
