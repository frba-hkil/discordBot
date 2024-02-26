package com.discordbot.hotaru.listener;

import com.discordbot.hotaru.Hotaru;
import com.discordbot.hotaru.audiohandler.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Objects;

public class CommandListener implements EventListener {
    private final Hotaru bot;

    public CommandListener(Hotaru bot) {
        this.bot = bot;
    }

    @Override
    public void onEvent(GenericEvent genericEvent) {
        if(genericEvent instanceof SlashCommandInteractionEvent) {
            if(((SlashCommandInteractionEvent) genericEvent).getName().equalsIgnoreCase("play")) {
                try {
                    String input = Objects.requireNonNull(((SlashCommandInteractionEvent) genericEvent)
                            .getOption("link")).getAsString();
                    ((SlashCommandInteractionEvent) genericEvent).reply("わかりました!").queue();

                    bot.loadAndPlay(((SlashCommandInteractionEvent) genericEvent).getChannel().asTextChannel(), input);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            else if(((SlashCommandInteractionEvent) genericEvent).getName().equalsIgnoreCase("skip")) {
                ((SlashCommandInteractionEvent) genericEvent).reply("skipped").queue();
                bot.skipTrack(((SlashCommandInteractionEvent) genericEvent).getChannel().asTextChannel());
            }
            else if(((SlashCommandInteractionEvent) genericEvent).getName().equalsIgnoreCase("stop")) {
                ((SlashCommandInteractionEvent) genericEvent).reply("Stopping hotaru").queue();
                bot.getGuildAudioPlayer(((SlashCommandInteractionEvent) genericEvent).getGuild()).scheduler.clearQueue();
                ((SlashCommandInteractionEvent) genericEvent).getGuild().getAudioManager().closeAudioConnection();
            }
        }
        else if(genericEvent instanceof GuildReadyEvent) {
            Guild guild = ((GuildReadyEvent) genericEvent).getGuild();

            guild.upsertCommand(Commands.slash("play", "pass me a youtube link")
                            .addOption(OptionType.STRING, "link", "yt link", true))
                    .queue();
            guild.upsertCommand(Commands.slash("stop", "stop the music")).queue();
            guild.upsertCommand(Commands.slash("skip", "skip current track")).queue();
        }
    }
}
