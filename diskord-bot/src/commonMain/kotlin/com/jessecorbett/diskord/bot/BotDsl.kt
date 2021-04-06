package com.jessecorbett.diskord.bot

import com.jessecorbett.diskord.AutoGateway
import com.jessecorbett.diskord.api.gateway.EventDispatcher
import com.jessecorbett.diskord.api.gateway.model.GatewayIntents
import com.jessecorbett.diskord.internal.client.RestClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import mu.KLogger
import mu.KotlinLogging

public class BotBase {
    public val logger: KLogger = KotlinLogging.logger {}

    /**
     * Modules are plugins which implement bot functionality
     */
    public var modules: List<BotModule> = emptyList()
        private set

    init {
        // Simple module for logging bot state
        registerModule { dispatcher, _ ->
            dispatcher.onReady { logger.info { "Bot has started and is ready for events" } }
            dispatcher.onResume { logger.info { "Bot has resumed a previous websocket session" } }
        }
    }

    public fun registerModule(botModule: BotModule) {
        modules = modules + botModule
    }

    public fun interface BotModule {
        public fun register(dispatcher: EventDispatcher<Unit>, context: BotContext)
    }
}

/**
 * Function to initiate a bot using the diskord-bot DSL
 *
 * @param token Discord bot API token
 * @param builder Function to build the bot
 */
public suspend fun bot(token: String, builder: BotBase.() -> Unit) {
    val client = RestClient.default(token)

    // Contains the rest client and provides the context for related bot utils
    val virtualContext: BotContext = object : BotContext {
        override val client: RestClient
            get() = client
    }

    // Container for modules and utils like the logger
    val base = BotBase().apply { builder() }

    // Compute intents from the provided modules
    val intentsComputer = GatewayIntentsComputer()
    base.modules.forEach { it.register(intentsComputer, virtualContext) }
    val intents = intentsComputer.intents
        .map { GatewayIntents(it.mask) }
        .reduceOrNull { a, b -> a + b }
        ?: GatewayIntents.NON_PRIVILEGED

    // Create the real dispatcher and register the modules with it
    val dispatcher = EventDispatcher.build(CoroutineScope(Dispatchers.Default))
    base.modules.forEach { it.register(dispatcher, virtualContext) }

    // Create the autogateway using what we've constructed
    val gateway = AutoGateway(
        token = token,
        intents = intents,
        restClient = client,
        eventDispatcher = dispatcher
    )

    // Start the gateway, block, and gracefully close after
    gateway.start()
    gateway.block()
    gateway.stop()
}
