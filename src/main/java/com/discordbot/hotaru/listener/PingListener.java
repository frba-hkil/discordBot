package com.discordbot.hotaru.listener;

import com.discordbot.hotaru.reply.Reply;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public class PingListener implements EventListener {
    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if(genericEvent instanceof MessageReceivedEvent) {
            Message msg = ((MessageReceivedEvent) genericEvent)
                            .getMessage();
            Reply rep = new Reply();
            rep.reply(msg);
        }
    }
}
