# Diskord - A Kotlin Discord SDK
[![Maven Central](https://img.shields.io/maven-central/v/com.jessecorbett/diskord.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.jessecorbett%22%20AND%20a:%22diskord%22)
[![Discord](https://img.shields.io/discord/424046347428167688.svg?style=flat-square)](https://discord.gg/UPTWsZ5)

A Kotlin client for Discord bots with a simple and concise DSL

Built as a lean client using coroutines that gets the intricacies of rate limits, async, and data models out of your way in a clean and easy to use SDK

Using Diskord? Send me a tweet about it [@JesseLCorbett](https://twitter.com/JesseLCorbett) or drop by the [discord server](https://discord.gg/UPTWsZ5)

## How do I import this?

It is strongly recommended to use Gradle version 6 or higher

```kotlin
// Kotlin build.gradle.kts
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.jessecorbett:diskord-bot:2.0.0")
    // or, if you only want the low level implementation
    implementation("com.jessecorbett:diskord-core:2.0.0")
}
```

Note: The `diskord-bot` artifact bundles `org.slf4j:slf4j-simple` to provide basic logging to STDOUT with no
configuration. This can be excluded in favor of your own slf4 logger using gradle exclusion:

```kotlin
// Kotlin build.gradle.kts
configurations {
  implementation {
    exclude("org.slf4j", "slf4j-simple")
  }
}
```

## How do I use this?

[Diskord Dokka documentation](https://jesselcorbett.gitlab.io/index.html)

The library is packaged into two artifacts.

`diskord-core` is the low level implementation of the Discord API.
[Read more](https://gitlab.com/jesselcorbett/diskord/-/blob/master/diskord-core/README.md)

`diskord-bot` provides an easier to use API for common bot functions.
[Read more](https://gitlab.com/jesselcorbett/diskord/-/blob/master/diskord-bot/README.md)

### Simple Example

```kotlin
import com.jessecorbett.diskord.bot.*

suspend fun main() {
    bot(TOKEN) {
        events {
            onGuildMemberAdd {
                channel(WELCOME_CHANNEL_ID).sendMessage("Welcome to the server, ${it.user?.username}!")
            }
        }
      
        commands {
            command("ping") {
                it.respond("pong")
            }
        }
    }
}
```


## FAQ
* Does this support voice chat?
    * No, voice chat is not supported at this time. If you need it I recommend checking out another SDK
* Is this library done?
    * Diskord is actively maintained, but the Discord API is always changing and there may be some lag between an API change and Diskord getting updated
    * If you want to speed things along, PRs are welcome and tickets appreciated
* Can I contact you to ask a question/contribute to the project/report a bug?
    * [We've got a discord server for just that](https://discord.gg/UPTWsZ5)
* What if I'm hip and cool, and I want to use a newer more ~~unstable~~ exciting version?
    * You can use our development versions by using the snapshot repository
    * Include https://oss.sonatype.org/content/repositories/snapshots/ in your gradle repositories
