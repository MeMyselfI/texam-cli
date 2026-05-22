# tEXAM CLI

> **⚠️ BETA SOFTWARE — USE AT YOUR OWN RISK**
>
> This CLI is in early beta. It may contain bugs, produce unexpected results, or cause data loss when interacting with your tEXAM server. It has not been tested against all server versions or configurations.
> **Do not use in production exam environments without thorough testing.** No warranties, express or implied, are provided. Use entirely at your own risk.

A command-line interface for the **tEXAM server** — a platform for computer-based exam management and delivery. Designed for automation and AI agent use: all output is JSON.

## Installation

Releases (native binaries, no JVM required) are available at:

**https://github.com/MeMyselfI/texam-cli/releases**

| Platform | Binary |
|----------|--------|
| Linux (x86_64) | `texam-linux-amd64` |
| macOS (Apple Silicon) | `texam-macos-arm64` |
| Windows (x86_64) | `texam-windows-amd64.exe` |

```bash
# Linux / macOS
chmod +x texam-linux-amd64
mv texam-linux-amd64 /usr/local/bin/texam
```

## Quick Start

```bash
# Save credentials (writes to ~/.texam/config)
texam login --url https://127.0.0.1:8443 --user admin --pass secret --insecure

# List all exams — JSON (default, ideal for AI agents)
texam exam list

# List all exams — ad-hoc without saved credentials
texam --url https://127.0.0.1:8443 --user admin --pass secret --insecure exam list

# List all exams — human-readable table
texam --url https://127.0.0.1:8443 --user admin --pass secret --insecure --table exam list

# Pretty-print JSON output
texam exam list --pretty

# Get a specific exam
texam exam get 42
```

Example table output:

```
| ID   | NAME              | STATUS   |
+------+-------------------+----------+
| 1    | Midterm 2025      | active   |
| 2    | Final Exam 2025   | planned  |
```

## Authentication

Credentials are resolved in this priority order (highest first):

1. **CLI flags**: `--url`, `--user`, `--pass`
2. **Environment variables**: `TEXAM_URL`, `TEXAM_USER`, `TEXAM_PASSWORD`, `TEXAM_INSECURE`
3. **Config file**: `~/.texam/config`

```bash
# Using environment variables (ideal for CI/AI agents)
export TEXAM_URL=https://127.0.0.1:8443
export TEXAM_USER=admin
export TEXAM_PASSWORD=secret
texam exam list
```

## Commands

### Exam Management

```bash
texam exam list                                        # List all exams
texam exam get <id>                                    # Get exam details
texam exam create --data '{"name":"Midterm"}'          # Create exam
texam exam update <id> --data '{"name":"Final"}'       # Update exam
texam exam delete <id>                                 # Delete exam
texam exam voucher <id> [--min 60] [--out FILE]        # Download voucher PDF
texam exam events <id>                                 # Get exam events
```

### Examinee Management

```bash
texam examinee list
texam examinee get <id>
texam examinee create --data '{"name":"Doe","firstname":"Jane"}'
texam examinee delete <id>
texam examinee assign --examinee-ids 1,2 --exam-ids 3,4 --mode associate-strict
texam examinee remove <id> --exam-id <examId> [--with-logs]
```

### Client (Device) Management

```bash
texam client list                    # List connected devices
texam client remove-exams            # Remove exams from all clients
texam client remove-disconnected     # Remove disconnected clients
texam client logs <client-id>        # Trigger log download from device
texam client alias <client-id> <name># Set device alias
```

### Logs

```bash
texam log list --exam-id <id> --examinee-id <id>
texam log available --exam-id <id> --examinee-id <id>
```

### Messages

```bash
texam message list [--start 0] [--limit 100]
texam message download [--lang de|en] [--out FILE]
```

### Backend Users

```bash
texam user list
texam user update <id> --data '{...}'
```

### Other

```bash
texam pin list                       # List access PINs
texam pin get <id>
texam media list                     # List media files
texam conf get                       # Server configuration
texam server status                  # Server status (memory, disk)
texam server backup [--out FILE]     # Download full backup ZIP
```

## Global Options

| Option | Description |
|--------|-------------|
| `--url URL` | Server URL (overrides config/env) |
| `--user, -u USER` | Username |
| `--pass, -p PASS` | Password |
| `--insecure, -k` | Skip TLS certificate verification |
| `--pretty` | Pretty-print JSON output |
| `--table` | Render result as human-readable table |
| `--help` | Show help |
| `--version` | Show version |

## Building from Source

Requires Java 21 and Maven 3.9+.

```bash
# Fat JAR (requires JVM)
mvn package
java -jar target/texam-cli-1.0.0-beta.jar exam list

# Native binary (requires GraalVM 21)
mvn -Pnative package
./target/texam exam list
```

## For AI Agents

All output is JSON. Use `--pretty` for human-readable output. Exit codes: `0` = success, `1` = error.

```bash
# Pipe to jq for filtering
texam exam list | jq '.root[] | {id: .id, name: .name}'

# Check success programmatically
result=$(texam exam get 42)
success=$(echo "$result" | jq -r '.success')
```

## License

MIT
