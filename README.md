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

```bash
./target/papertrail-bot-lite-runner
```

#### Option D : Cloud Deployment

If your cloud supports building from Dockerfile, point the source towards `Dockerfile` (for JVM Build)
or `Dockerfile.native` (for Native Build), found in the project's root.

If your cloud supports using pre-built docker images, you can find the image links in
the container registry.

# Customization Options

The `LITE` edition offers greater customization than the `ORIGINAL` edition.

The following environment variables can be used to customize the behavior of the bot:

## General Configuration

| Variable                       | Description                                                                     | Default Value     |
|--------------------------------|---------------------------------------------------------------------------------|-------------------|
| `APP_NAME`                     | Name displayed by the bot where applicable.                                     | `PaperTrail Lite` |
| `APP_ACTIVITY`                 | Custom activity text displayed as the bot's status.                             | `Logging Events`  |
| `GUILD_AUDIT_LOG_CHANNEL`      | Default channel ID used for all audit log events unless overridden.             | Disabled (`-1`)   |
| `GUILD_MESSAGE_LOG_CHANNEL`    | Default channel ID used for message logging events.                             | Disabled (`-1`)   |
| `GUILD_MESSAGE_RETENTION_DAYS` | Number of days to retain logged messages before they are automatically removed. | `30`              |
| `APP_LOG_LEVEL`                | Controls the verbosity of the application's console logs.                       | `INFO`            |

## Channel Overrides

Channel overrides are useful when you want to route a specific event to a different channel, or disable logging
for a particular event.

By default, all guild events are logged to `GUILD_AUDIT_LOG_CHANNEL`.

If you wish to route specific event categories to different channels, set a custom channel ID to one of the following
environment variables.

You can also set their value to `-1` to disable logging for a particular event category.

Any event category that is not explicitly configured will continue to use the global audit log channel.

| Variable                                 | Event Category                         | Default                   |
|------------------------------------------|----------------------------------------|---------------------------|
| `GUILD_AUTOMOD_LOG_CHANNEL`              | AutoMod actions                        | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_CHANNEL_LOG_CHANNEL`              | Channel creation, deletion and updates | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_CHANNEL_OVERRIDE_LOG_CHANNEL`     | Channel permission overrides           | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_EMOJI_LOG_CHANNEL`                | Emoji events                           | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_UPDATE_LOG_CHANNEL`               | Guild updates                          | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_HOME_SETTINGS_LOG_CHANNEL`        | Home settings updates                  | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_INTEGRATION_LOG_CHANNEL`          | Integration events                     | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_INVITE_LOG_CHANNEL`               | Invite events                          | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_MEMBER_UPDATE_LOG_CHANNEL`        | Member updates                         | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_MESSAGE_ACTION_LOG_CHANNEL`       | Message actions                        | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_MESSAGE_PIN_LOG_CHANNEL`          | Message pin/unpin events               | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_MOD_ACTION_LOG_CHANNEL`           | Moderation actions                     | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_ONBOARDING_LOG_CHANNEL`           | Onboarding updates                     | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_ONBOARDING_PROMPT_LOG_CHANNEL`    | Onboarding prompt updates              | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_ROLE_LOG_CHANNEL`                 | Role events                            | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_SCHEDULED_EVENT_LOG_CHANNEL`      | Scheduled events                       | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_SOUNDBOARD_LOG_CHANNEL`           | Soundboard events                      | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_STAGE_INSTANCE_LOG_CHANNEL`       | Stage instance events                  | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_STICKER_LOG_CHANNEL`              | Sticker events                         | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_THREAD_LOG_CHANNEL`               | Thread events                          | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_UNKNOWN_LOG_CHANNEL`              | Unknown or unsupported audit events    | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_VOICE_CHANNEL_STATUS_LOG_CHANNEL` | Voice channel status updates           | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_WEBHOOK_LOG_CHANNEL`              | Webhook events                         | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_BOOST_LOG_CHANNEL`                | Guild boost events                     | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_MEMBER_LOG_CHANNEL`               | Guild member events                    | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_POLL_LOG_CHANNEL`                 | Poll events                            | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_SECURITY_INCIDENT_LOG_CHANNEL`    | Guild security incident events         | `GUILD_AUDIT_LOG_CHANNEL` |
| `GUILD_VOICE_LOG_CHANNEL`                | Voice state events                     | `GUILD_AUDIT_LOG_CHANNEL` |

Check out an example configuration [here](/.env.example)

## Embed Colors

Customize the colors used for embeds based on the type of event being logged.

| Variable                      | Description                                                                                                                                       | Default Value       |
|-------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|---------------------|
| `EMBED_SUCCESS_COLOR_INT`     | Color used for events that represent additions or positive actions, such as member joins, emoji creation, role creation, and similar events.      | `712458` (Green)    |
| `EMBED_WARNING_COLOR_INT`     | Color used for events that represent modifications or updates, such as member updates, channel updates, emoji updates, and similar events.        | `16776960` (Yellow) |
| `EMBED_DESTRUCTIVE_COLOR_INT` | Color used for events that represent removals or destructive actions, such as member leaves, emoji deletions, role deletions, and similar events. | `16711680` (Red)    |

## Custom Port Configuration

By, default PaperTrail runs on port 8080. If port 8080 is occupied by a different service, or you wish to
run the application on a different port, you can manually set the `PORT` environment variable. Same goes for
`MANAGEMENT_PORT` which will expose the health check endpoints.

| Variable          | Description                                           | Default Value | Optional |
|-------------------|-------------------------------------------------------|---------------|----------|
| `PORT`            | Port Number on which the instance of the bot will run | 8080          | Yes      |
| `MANAGEMENT_PORT` | Port for Health Check Interface                       | 9000          | Yes      |

# Health Check

Health Check is served on `MANAGEMENT_PORT`

| Endpoint          | Description                                                                                 |
|-------------------|---------------------------------------------------------------------------------------------|
| `/q/health`       | Aggregated health status containing all registered checks.                                  |
| `/q/health/live`  | Liveness probe. Verifies that the bot process is healthy and operating normally.            |
| `/q/health/ready` | Readiness probe. Verifies that the bot is connected to Discord and ready to receive events. |

# License

This project is licensed under the [AGPLv3](/LICENSE) license.
