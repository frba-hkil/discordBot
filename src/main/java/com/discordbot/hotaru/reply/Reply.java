package com.discordbot.hotaru.reply;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

public class Reply {
    public void reply(Message content) {
        if(content.getContentDisplay().equalsIgnoreCase("h!ping")) {
            content
                    .getChannel()
                    .asGuildMessageChannel()
                    .sendMessage("pong!")
                    .queue();
        }
    }
}
