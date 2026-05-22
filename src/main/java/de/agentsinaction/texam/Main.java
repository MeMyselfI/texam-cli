package de.agentsinaction.texam;

import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new TexamCommand())
            .setExecutionExceptionHandler((ex, cmd, parseResult) -> {
                String msg = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
                String jsonMsg = msg.replace("\\", "\\\\").replace("\"", "'").replace("\n", " | ").replace("\r", "");
                System.err.println("{\"success\":false,\"error\":\"" + jsonMsg + "\"}");
                return 1;
            })
            .execute(args);
        System.exit(exitCode);
    }
}
