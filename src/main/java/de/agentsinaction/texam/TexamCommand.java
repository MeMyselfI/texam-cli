package de.agentsinaction.texam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.agentsinaction.texam.client.ApiClient;
import de.agentsinaction.texam.commands.*;
import de.agentsinaction.texam.config.Config;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Command(
    name = "texam",
    version = "texam-cli 1.0.9",
    mixinStandardHelpOptions = true,
    description = {"CLI for the tEXAM server (computer-based exam management). Output is JSON by default.",
                   "@|bold,red WARNING: BETA SOFTWARE — USE AT YOUR OWN RISK.|@",
                   "May cause data loss. Not for use in production without testing."},
    subcommands = {
        LoginCommand.class,
        ExamCommand.class,
        ExamineeCommand.class,
        ClientCommand.class,
        LogCommand.class,
        MessageCommand.class,
        UserCommand.class,
        PinCommand.class,
        MediaCommand.class,
        ConfCommand.class,
        ServerCommand.class,
        CommandLine.HelpCommand.class
    }
)
public class TexamCommand implements Callable<Integer> {

    @Option(names = {"--url"}, description = "Server URL (overrides config/env)", scope = ScopeType.INHERIT)
    public String url;

    @Option(names = {"--user", "-u"}, description = "Username (overrides config/env)", scope = ScopeType.INHERIT)
    public String user;

    @Option(names = {"--pass", "-p"}, description = "Password (overrides config/env)", scope = ScopeType.INHERIT)
    public String pass;

    @Option(names = {"--insecure", "-k"}, description = "Skip TLS certificate verification", scope = ScopeType.INHERIT)
    public Boolean insecure;

    @Option(names = {"--pretty"}, description = "Pretty-print JSON output", scope = ScopeType.INHERIT)
    public boolean pretty;

    @Option(names = {"--table"}, description = "Render root array as a table (human-readable)", scope = ScopeType.INHERIT)
    public boolean table;

    private Config config;
    private ApiClient apiClient;

    public Config resolveConfig() {
        if (config == null) {
            config = Config.load();
            config.applyOverrides(url, user, pass, insecure);
        }
        return config;
    }

    public ApiClient resolveClient() {
        if (apiClient == null) apiClient = new ApiClient(resolveConfig());
        return apiClient;
    }

    public void requireAuth() {
        if (!resolveConfig().isValid()) {
            throw new ParameterException(new CommandLine(this),
                "Not logged in. Run: texam login --url <url> --user <user> --pass <password>");
        }
    }

    public void printJson(JsonNode node) {
        if (table) { printTable(node); return; }
        try {
            ObjectMapper m = resolveClient().getMapper();
            System.out.println(pretty ? m.writerWithDefaultPrettyPrinter().writeValueAsString(node)
                                      : m.writeValueAsString(node));
        } catch (Exception e) { System.err.println("Error serializing output: " + e.getMessage()); }
    }

    public void printJson(Object obj) {
        try {
            ObjectMapper m = resolveClient().getMapper();
            if (table) { printTable(m.valueToTree(obj)); return; }
            System.out.println(pretty ? m.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
                                      : m.writeValueAsString(obj));
        } catch (Exception e) { System.err.println("Error serializing output: " + e.getMessage()); }
    }

    private void printTable(JsonNode node) {
        JsonNode rows = node;
        if (node.has("root") && node.get("root").isArray()) rows = node.get("root");

        if (!rows.isArray() || rows.isEmpty()) {
            try { System.out.println(resolveClient().getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(node)); }
            catch (Exception e) { System.err.println("Error serializing output: " + e.getMessage()); }
            return;
        }

        List<String> cols = new ArrayList<>();
        rows.get(0).fieldNames().forEachRemaining(cols::add);

        int[] widths = new int[cols.size()];
        for (int i = 0; i < cols.size(); i++) widths[i] = cols.get(i).length();
        for (JsonNode row : rows)
            for (int i = 0; i < cols.size(); i++) {
                JsonNode cell = row.get(cols.get(i));
                int len = cell == null ? 0 : cellValue(cell).length();
                if (len > widths[i]) widths[i] = Math.min(len, 60);
            }

        printTableRow(cols.stream().map(String::toUpperCase).toList(), widths);
        printTableSeparator(widths);
        for (JsonNode row : rows) {
            List<String> cells = new ArrayList<>();
            for (String col : cols) {
                JsonNode cell = row.get(col);
                String val = cell == null ? "" : cellValue(cell);
                cells.add(val.length() > 60 ? val.substring(0, 57) + "..." : val);
            }
            printTableRow(cells, widths);
        }
    }

    private String cellValue(JsonNode n) {
        if (n.isNull()) return "";
        if (n.isTextual() || n.isNumber() || n.isBoolean()) return n.asText();
        return n.toString();
    }

    private void printTableRow(List<String> cells, int[] widths) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.size(); i++) sb.append("| ").append(pad(cells.get(i), widths[i])).append(" ");
        System.out.println(sb.append("|"));
    }

    private void printTableSeparator(int[] widths) {
        StringBuilder sb = new StringBuilder();
        for (int w : widths) sb.append("+").append("-".repeat(w + 2));
        System.out.println(sb.append("+"));
    }

    private String pad(String s, int width) {
        return s.length() >= width ? s : s + " ".repeat(width - s.length());
    }

    @Override
    public Integer call() { new CommandLine(this).usage(System.out); return 0; }
}
