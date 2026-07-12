# Overview

A lightweight edition of PaperTrail designed for users who want to self-host the bot for a single Discord server.
Unlike the Original edition, PaperTrail Lite runs as a single application and requires no DB or custom API
service.

Read the differences between the `LITE` and the `ORIGINAL` version
[here](https://github.com/eggy03/PaperTrailBot/blob/main/DEPLOYMENT.md)

# Self-Hosting Guide

## Step 1: Setting up the Bot In Discord

Log on to the [Discord Developer Portal](https://discord.com/developers/applications) and create an application.

The application can have any name, avatar, banner and description but the following scopes, permissions and intents are
needed
for it to work properly:

**Installation Contexts**

1) Guild Install

**Scopes**

1) applications.commands
2) bot

**Permissions**

1) Manage Server
2) Read Message History
3) Send Messages
4) Send Messages In Threads
5) View Audit Log
6) View Channels

**Privileged Gateway Intents**

1) Presence Intent
2) Server Members Intent
3) Message Content Intent

Note down the `BOT TOKEN` since it will be shown only once and will be required in the later steps.

## Step 2: Deploying the Bot

### Get Required Secrets

| Variable    | Description                                                                  | Default Value | Optional |
|-------------|------------------------------------------------------------------------------|---------------|----------|
| `TOKEN`     | Discord application bot token (from the Developer Portal)                    | N/A           | No       |
| `REDIS_URL` | Redis/Valkey URL of the format `redis://[:password@]host[:port][/db-number]` | N/A           | No       |

### Deployment Options

#### Option A : Deploy Using Pre-Built Docker Images

The GitHub Container Registry
has pre-built docker images for both JVM and Native versions the bot which you can use.

You may choose either one.

Make sure you have the `.env` file containing the required secrets in the root of the folder
you're executing the following commands from:

```bash
# JVM
docker run -d --name papertrail-bot-lite --env-file .env ghcr.io/eggy03/papertrail-bot-lite:latest
```

```bash
# Native
docker run -d --name papertrail-bot-lite-native --env-file .env ghcr.io/eggy03/papertrail-bot-lite-native:latest
```

#### Option B : Building From Source With Docker

```bash
git clone https://github.com/eggy03/PaperTrailBot-Lite.git
cd PaperTrailBot-Lite
```

```bash
# JVM
docker build -t papertrail-bot-lite .
docker run -d --name papertrail-bot-lite --env-file .env papertrail-bot-lite
```

```bash
# Native
docker build -f Dockerfile.native -t papertrail-bot-lite-native .
docker run -d --name papertrail-bot-lite-native --env-file .env papertrail-bot-lite-native
```

> [!NOTE]
>
> While the above sub-options use `--env-file .env` for examples, you can also pass environment variables directly
> via `docker -e KEY:"VALUE"`

#### Option C : Building From Source Without Docker

```bash
git clone https://github.com/eggy03/PaperTrailBot-Lite.git
cd PaperTrailBot-Lite
```

```bash
# JVM
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

```bash
# Native
./mvnw clean package -Dnative
```

The built application will be found in the `target` folder of the project.

#### Option D : Cloud Deployment

If your cloud supports building from Dockerfile, point the source towards `Dockerfile` (for JVM Build)
or `Dockerfile.native` (for Native Build), found in the project's root.

If your cloud supports using pre-built docker images, you can find the image links in
the container registry.

# Customization Options

The `LITE` edition offers greater customization than the `ORIGINAL` edition.

The following environment variables can be used to customize the behavior of the bot:

## General Configuration

| Variable                       | Description                                                                     | Default Value               |
|--------------------------------|---------------------------------------------------------------------------------|-----------------------------|
| `APP_NAME`                     | Name displayed by the bot where applicable.                                     | `PaperTrail Lite`           |
| `CUSTOM_ACTIVITY`              | Custom activity text displayed as the bot's status.                             | Current application version |
| `AUDIT_LOG_CHANNEL`            | Default channel ID used for all audit log events unless overridden.             | Disabled (`-1`)             |
| `MESSAGE_LOG_CHANNEL`          | Default channel ID used for message logging events.                             | Disabled (`-1`)             |
| `GUILD_MESSAGE_RETENTION_DAYS` | Number of days to retain logged messages before they are automatically removed. | `30`                        |

Note that since messages are stored in-memory, they will be deleted if the bot restarts or a new instance is started.

## Audit Log Channel Overrides

By default, all audit log events are delivered to `AUDIT_LOG_CHANNEL`.
If you wish to route specific event categories to different channels, you can define one or more of the following
environment variables.
You can also set the value to `-1` to disable logging for a particular event category.
Any event category that is not explicitly configured will continue to use the global audit log channel.

| Variable                                    | Event Category                         | Default             |
|---------------------------------------------|----------------------------------------|---------------------|
| `AUTOMOD_ACTION_LOG_CHANNEL`                | AutoMod actions                        | `AUDIT_LOG_CHANNEL` |
| `CHANNEL_ACTION_LOG_CHANNEL`                | Channel creation, deletion and updates | `AUDIT_LOG_CHANNEL` |
| `CHANNEL_OVERRIDE_ACTION_LOG_CHANNEL`       | Channel permission overrides           | `AUDIT_LOG_CHANNEL` |
| `EMOJI_ACTION_LOG_CHANNEL`                  | Emoji events                           | `AUDIT_LOG_CHANNEL` |
| `GUILD_UPDATE_ACTION_LOG_CHANNEL`           | Guild updates                          | `AUDIT_LOG_CHANNEL` |
| `HOME_SETTINGS_ACTION_LOG_CHANNEL`          | Home settings updates                  | `AUDIT_LOG_CHANNEL` |
| `INTEGRATION_ACTION_LOG_CHANNEL`            | Integration events                     | `AUDIT_LOG_CHANNEL` |
| `INVITE_ACTION_LOG_CHANNEL`                 | Invite events                          | `AUDIT_LOG_CHANNEL` |
| `MEMBER_UPDATE_ACTION_LOG_CHANNEL`          | Member updates                         | `AUDIT_LOG_CHANNEL` |
| `MESSAGE_ACTION_LOG_CHANNEL`                | Message actions                        | `AUDIT_LOG_CHANNEL` |
| `MESSAGE_PIN_ACTION_LOG_CHANNEL`            | Message pin/unpin events               | `AUDIT_LOG_CHANNEL` |
| `MOD_ACTION_ACTION_LOG_CHANNEL`             | Moderation actions                     | `AUDIT_LOG_CHANNEL` |
| `ONBOARDING_ACTION_LOG_CHANNEL`             | Onboarding updates                     | `AUDIT_LOG_CHANNEL` |
| `ONBOARDING_PROMPT_ACTION_LOG_CHANNEL`      | Onboarding prompt updates              | `AUDIT_LOG_CHANNEL` |
| `ROLE_ACTION_LOG_CHANNEL`                   | Role events                            | `AUDIT_LOG_CHANNEL` |
| `SCHEDULED_EVENT_ACTION_LOG_CHANNEL`        | Scheduled events                       | `AUDIT_LOG_CHANNEL` |
| `SOUNDBOARD_ACTION_LOG_CHANNEL`             | Soundboard events                      | `AUDIT_LOG_CHANNEL` |
| `STAGE_INSTANCE_ACTION_LOG_CHANNEL`         | Stage instance events                  | `AUDIT_LOG_CHANNEL` |
| `STICKER_ACTION_LOG_CHANNEL`                | Sticker events                         | `AUDIT_LOG_CHANNEL` |
| `THREAD_ACTION_LOG_CHANNEL`                 | Thread events                          | `AUDIT_LOG_CHANNEL` |
| `UNKNOWN_ACTION_LOG_CHANNEL`                | Unknown or unsupported audit events    | `AUDIT_LOG_CHANNEL` |
| `VOICE_CHANNEL_STATUS_ACTION_LOG_CHANNEL`   | Voice channel status updates           | `AUDIT_LOG_CHANNEL` |
| `WEBHOOK_ACTION_LOG_CHANNEL`                | Webhook events                         | `AUDIT_LOG_CHANNEL` |
| `GUILD_BOOST_EVENT_LOG_CHANNEL`             | Guild boost events                     | `AUDIT_LOG_CHANNEL` |
| `GUILD_MEMBER_EVENT_LOG_CHANNEL`            | Guild member events                    | `AUDIT_LOG_CHANNEL` |
| `GUILD_POLL_EVENT_LOG_CHANNEL`              | Poll events                            | `AUDIT_LOG_CHANNEL` |
| `GUILD_SECURITY_INCIDENT_EVENT_LOG_CHANNEL` | Guild security incident events         | `AUDIT_LOG_CHANNEL` |
| `GUILD_VOICE_EVENT_LOG_CHANNEL`             | Voice state events                     | `AUDIT_LOG_CHANNEL` |

Check out an example configuration [here](/.env.example)

# Health Check

| Endpoint          | Description                                                                                 |
|-------------------|---------------------------------------------------------------------------------------------|
| `/q/health`       | Aggregated health status containing all registered checks.                                  |
| `/q/health/live`  | Liveness probe. Verifies that the bot process is healthy and operating normally.            |
| `/q/health/ready` | Readiness probe. Verifies that the bot is connected to Discord and ready to receive events. |

# Custom Port Configuration

By, default PaperTrail runs on port 8080. If port 8080 is occupied by a different service, or you wish to
run the application on a different port, you can manually set the `PORT` environment variable. Same goes for
`MANAGEMENT_PORT` which will expose the health check endpoints.

| Variable          | Description                                           | Default Value | Optional |
|-------------------|-------------------------------------------------------|---------------|----------|
| `PORT`            | Port Number on which the instance of the bot will run | 8080          | Yes      |
| `MANAGEMENT_PORT` | Port for Health Check Interface                       | 9000          | Yes      |


# License

This project is licensed under the [AGPLv3](/LICENSE) license.
