package kh.edu.istad.stadoor.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

@Command(
        name = "stadoor",
        description = "Stadoor tunnel client.",
        mixinStandardHelpOptions = true,
        version = "stadoor-cli 0.1.0",
        subcommands = {
                StadoorCli.LoginCommand.class,
                StadoorCli.LogoutCommand.class,
                StadoorCli.StatusCommand.class,
                StadoorCli.TunnelCommand.class
        }
)
public class StadoorCli implements Callable<Integer> {

    static final ObjectMapper JSON = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final Path DEFAULT_CONFIG_DIR = Path.of(System.getProperty("user.home"), ".stadoor");
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    @Option(names = "--server", defaultValue = "http://localhost:8080", description = "Tunnel server base URL.")
    String serverUrl;

    @Option(names = "--config-dir", description = "Config directory. Defaults to ~/.stadoor.")
    Path configDir = DEFAULT_CONFIG_DIR;

    public static void main(String[] args) {
        int code = new CommandLine(new StadoorCli()).execute(args);
        System.exit(code);
    }

    @Override
    public Integer call() {
        CommandLine.usage(this, System.out);
        return 0;
    }

    Path sessionFile() {
        return configDir.resolve("session.json");
    }

    String endpoint(String path) {
        String base = serverUrl.endsWith("/") ? serverUrl.substring(0, serverUrl.length() - 1) : serverUrl;
        return base + path;
    }

    JsonNode readSession() throws IOException {
        Path sessionFile = sessionFile();
        if (!Files.exists(sessionFile)) {
            throw new IllegalStateException("Run `stadoor login` first.");
        }

        JsonNode session = JSON.readTree(sessionFile.toFile());
        String expiresAt = session.path("expiresAt").asText(null);
        if (expiresAt != null && !expiresAt.isBlank() && Instant.parse(expiresAt).isBefore(Instant.now())) {
            throw new IllegalStateException("Session expired. Run `stadoor login` again.");
        }
        return session;
    }

    String authorizationHeader() throws IOException {
        JsonNode session = readSession();
        String username = session.path("username").asText();
        String token = session.path("pat").asText(null);
        if (token == null || token.isBlank()) {
            token = session.path("token").asText(null);
        }
        if (username == null || username.isBlank() || token == null || token.isBlank()) {
            throw new IllegalStateException("Session file is missing username or token. Run `stadoor login` again.");
        }
        return basicAuth(username, token);
    }

    @Command(name = "login", description = "Login with IAM username and personal access token.", mixinStandardHelpOptions = true)
    static class LoginCommand implements Callable<Integer> {

        @ParentCommand
        StadoorCli parent;

        @Option(names = {"-u", "--username"}, required = true, description = "IAM username.")
        String username;

        @Option(names = {"-t", "--token"}, required = true, interactive = true, arity = "0..1",
                description = "IAM personal access token. Prompts when value is omitted.")
        String token;

        @Override
        public Integer call() throws Exception {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("username", username);
            body.put("token", token);
            body.put("hostInfo", hostInfo());

            JsonNode response = post(parent.endpoint("/api/auth/login"), body, null);
            Files.createDirectories(parent.configDir);
            JSON.writerWithDefaultPrettyPrinter().writeValue(parent.sessionFile().toFile(), response);

            System.out.printf("Logged in as %s%n", response.path("username").asText(username));
            System.out.printf("Session: %s%n", response.path("sessionId").asText(""));
            System.out.printf("Expires: %s%n", response.path("expiresAt").asText(""));
            return 0;
        }
    }

    @Command(name = "logout", description = "Remove the saved local session.", mixinStandardHelpOptions = true)
    static class LogoutCommand implements Callable<Integer> {

        @ParentCommand
        StadoorCli parent;

        @Override
        public Integer call() throws Exception {
            Files.deleteIfExists(parent.sessionFile());
            System.out.println("Logged out.");
            return 0;
        }
    }

    @Command(name = "status", description = "Show the saved local session.", mixinStandardHelpOptions = true)
    static class StatusCommand implements Callable<Integer> {

        @ParentCommand
        StadoorCli parent;

        @Override
        public Integer call() throws Exception {
            JsonNode session = parent.readSession();
            System.out.printf("User: %s%n", session.path("username").asText(""));
            System.out.printf("Email: %s%n", session.path("email").asText(""));
            System.out.printf("Session: %s%n", session.path("sessionId").asText(""));
            System.out.printf("Device: %s%n", session.path("hostInfo").path("deviceId").asText(""));
            System.out.printf("Expires: %s%n", session.path("expiresAt").asText(""));
            return 0;
        }
    }

    @Command(
            name = "tunnel",
            description = "Manage tunnel routes.",
            mixinStandardHelpOptions = true,
            subcommands = {
                    CreateTunnelCommand.class,
                    ListTunnelCommand.class,
                    ActiveTunnelCommand.class,
                    DisconnectTunnelCommand.class
            }
    )
    static class TunnelCommand implements Callable<Integer> {

        @ParentCommand
        StadoorCli parent;

        @Override
        public Integer call() {
            CommandLine.usage(this, System.out);
            return 0;
        }
    }

    @Command(name = "create", description = "Create one or more public routes for a local port.", mixinStandardHelpOptions = true)
    static class CreateTunnelCommand implements Callable<Integer> {

        @ParentCommand
        TunnelCommand tunnel;

        @Option(names = {"-s", "--subdomain"}, required = true,
                description = "Route namespace, for example Astor.")
        String subdomain;

        @Option(names = {"-p", "--local-port"}, required = true,
                description = "Local service port to expose.")
        int localPort;

        @Option(names = "--route-count", description = "Number of generated route keys.")
        Integer routeCount;

        @Option(names = "--route-key", split = ",",
                description = "Explicit key or path. Repeat or comma-separate, for example /Astor/123$4,/Astor/1257$.")
        List<String> routeKeys;

        @Option(names = "--basic-auth", description = "Enable basic auth on the public tunnel route.")
        boolean basicAuth;

        @Option(names = "--auth-username", description = "Basic auth username for public route.")
        String authUsername;

        @Option(names = "--auth-password", interactive = true, arity = "0..1",
                description = "Basic auth password for public route.")
        String authPassword;

        @Override
        public Integer call() throws Exception {
            StadoorCli root = tunnel.parent;
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("subdomain", subdomain);
            body.put("localPort", localPort);
            body.put("basicAuth", basicAuth);
            body.put("authUsername", authUsername);
            body.put("authPassword", authPassword);
            body.put("routeCount", routeCount);
            body.put("routeKeys", routeKeys);

            JsonNode response = post(root.endpoint("/api/tunnels/generate"), body, root.authorizationHeader());
            System.out.printf("Tunnel: %s%n", response.path("subdomain").asText(subdomain));
            System.out.printf("Status: %s%n", response.path("status").asText(""));
            System.out.printf("SSH: %s%n", response.path("sshCommand").asText(""));
            response.path("routes").forEach(route ->
                    System.out.printf("%s -> localhost:%d%n", route.path("tunnelUrl").asText(), localPort));
            return 0;
        }
    }

    @Command(name = "list", description = "List tunnels owned by the logged-in user.", mixinStandardHelpOptions = true)
    static class ListTunnelCommand implements Callable<Integer> {

        @ParentCommand
        TunnelCommand tunnel;

        @Override
        public Integer call() throws Exception {
            StadoorCli root = tunnel.parent;
            JsonNode response = get(root.endpoint("/api/tunnels"), root.authorizationHeader());
            printJsonOrList(response);
            return 0;
        }
    }

    @Command(name = "active", description = "List active in-memory tunnels on the server.", mixinStandardHelpOptions = true)
    static class ActiveTunnelCommand implements Callable<Integer> {

        @ParentCommand
        TunnelCommand tunnel;

        @Override
        public Integer call() throws Exception {
            JsonNode response = get(tunnel.parent.endpoint("/api/tunnels/active"), null);
            printJsonOrList(response);
            return 0;
        }
    }

    @Command(name = "disconnect", description = "Disconnect and deactivate a tunnel route.", mixinStandardHelpOptions = true)
    static class DisconnectTunnelCommand implements Callable<Integer> {

        @ParentCommand
        TunnelCommand tunnel;

        @Option(names = "--tunnel-name", required = true, description = "Tunnel name/subdomain.")
        String tunnelName;

        @Option(names = "--keygen", required = true, description = "Route keygen.")
        String keygen;

        @Override
        public Integer call() throws Exception {
            StadoorCli root = tunnel.parent;
            delete(root.endpoint("/api/tunnels/" + tunnelName + "/" + keygen), root.authorizationHeader());
            System.out.printf("Disconnected %s/%s%n", tunnelName, keygen);
            return 0;
        }
    }

    static JsonNode post(String uri, Map<String, Object> body, String authorization) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)));
        return sendJson(request, authorization);
    }

    static JsonNode get(String uri, String authorization) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET();
        return sendJson(request, authorization);
    }

    static void delete(String uri, String authorization) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .DELETE();
        HttpResponse<String> response = send(request, authorization);
        if (response.statusCode() >= 400) {
            throw new IllegalStateException("Server returned " + response.statusCode() + ": " + response.body());
        }
    }

    static JsonNode sendJson(HttpRequest.Builder request, String authorization) throws Exception {
        HttpResponse<String> response = send(request, authorization);
        if (response.statusCode() >= 400) {
            throw new IllegalStateException("Server returned " + response.statusCode() + ": " + response.body());
        }
        if (response.body() == null || response.body().isBlank()) {
            return JSON.createObjectNode();
        }
        return JSON.readTree(response.body());
    }

    static HttpResponse<String> send(HttpRequest.Builder request, String authorization) throws Exception {
        request.header("Accept", "application/json");
        if (authorization != null) {
            request.header("Authorization", authorization);
        }
        return HTTP.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    static void printJsonOrList(JsonNode node) throws IOException {
        if (!node.isArray()) {
            System.out.println(JSON.writerWithDefaultPrettyPrinter().writeValueAsString(node));
            return;
        }
        if (node.isEmpty()) {
            System.out.println("No tunnels found.");
            return;
        }
        for (JsonNode item : node) {
            System.out.println(JSON.writerWithDefaultPrettyPrinter().writeValueAsString(item));
        }
    }

    static String basicAuth(String username, String token) {
        return "Basic " + Base64.getEncoder()
                .encodeToString((username + ":" + token).getBytes(StandardCharsets.UTF_8));
    }

    static Map<String, Object> hostInfo() throws Exception {
        InetAddress localHost = InetAddress.getLocalHost();
        String hostName = localHost.getHostName();

        Map<String, Object> hostInfo = new LinkedHashMap<>();
        hostInfo.put("deviceId", stableDeviceId(hostName));
        hostInfo.put("hostName", hostName);
        hostInfo.put("osName", System.getProperty("os.name"));
        hostInfo.put("osVersion", System.getProperty("os.version"));
        hostInfo.put("osArch", System.getProperty("os.arch"));
        hostInfo.put("ipAddress", localHost.getHostAddress());
        return hostInfo;
    }

    static String stableDeviceId(String hostName) {
        String seed = hostName + ":" + System.getProperty("user.name") + ":" + System.getProperty("os.name");
        return UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8)).toString();
    }
}
