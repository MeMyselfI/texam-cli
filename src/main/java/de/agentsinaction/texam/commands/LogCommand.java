package de.agentsinaction.texam.commands;

import de.agentsinaction.texam.TexamCommand;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.util.concurrent.Callable;

@Command(name = "log", description = "Access exam logs.",
    mixinStandardHelpOptions = true,
    subcommands = {LogCommand.ListCommand.class, LogCommand.AvailableCommand.class, HelpCommand.class})
public class LogCommand implements Callable<Integer> {
    @ParentCommand TexamCommand parent;
    @Override public Integer call() { new CommandLine(this).usage(System.out); return 0; }

    @Command(name = "list", description = "Get all logs for exam/examinee. GET /rest/Log/{examID}/{examineeID}/all")
    static class ListCommand implements Callable<Integer> {
        @ParentCommand LogCommand cmd;
        @Option(names = {"--exam-id"}, required = true) long examId;
        @Option(names = {"--examinee-id"}, required = true) long examineeId;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient()
                .get("/rest/Log/" + examId + "/" + examineeId + "/all"));
            return 0;
        }
    }

    @Command(name = "available", description = "Check if logs are available. GET /rest/Log/{examID}/{examineeID}/available")
    static class AvailableCommand implements Callable<Integer> {
        @ParentCommand LogCommand cmd;
        @Option(names = {"--exam-id"}, required = true) long examId;
        @Option(names = {"--examinee-id"}, required = true) long examineeId;
        @Override public Integer call() throws Exception {
            cmd.parent.requireAuth();
            cmd.parent.printJson(cmd.parent.resolveClient()
                .get("/rest/Log/" + examId + "/" + examineeId + "/available"));
            return 0;
        }
    }
}
