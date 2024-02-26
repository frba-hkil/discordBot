package com.discordbot.hotaru;

import com.discordbot.hotaru.audiohandler.GuildMusicManager;
import com.discordbot.hotaru.listener.CommandListener;
import com.discordbot.hotaru.listener.PingListener;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.HashMap;
import java.util.Map;

public class Hotaru extends ListenerAdapter {

    public static void main(String[] args) throws InterruptedException {
        Hotaru self = new Hotaru();

        JDABuilder.createDefault(args[0])
        .enableIntents(GatewayIntent.MESSAGE_CONTENT)
        .addEventListeners(new PingListener(),
                new CommandListener(self), self)
        .build()
        .awaitReady();

    }
    private final AudioPlayerManager manager;
    private final Map<Long, GuildMusicManager> musicManagers;

    private Hotaru() {
        this.musicManagers = new HashMap<>();
        this.manager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(manager);
        AudioSourceManagers.registerLocalSource(manager);
    }

    public synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if(musicManager == null) {
            musicManager = new GuildMusicManager(manager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
        return musicManager;
    }

    public void loadAndPlay(final TextChannel channel, final String url) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        manager.loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                channel.sendMessage("Added to queue: "
                        + audioTrack.getInfo().title
                        + " - "
                        + audioTrack.getInfo().author).queue();
                play(channel.getGuild(), musicManager, audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                channel.sendMessage("Added playlist: \"" + audioPlaylist.getName() + "\" to queue").queue();
                for(AudioTrack track : audioPlaylist.getTracks()) {
                    play(channel.getGuild(), musicManager, track);
                }
            }

            @Override
            public void noMatches() {
                channel.sendMessage("no match for: " + url).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                channel.sendMessage("Load failed. check logs").queue();
                System.out.println(e.getMessage() + e.getCause());
            }
        });
    }
    public void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
        connectToFirstVoiceChannel(guild.getAudioManager());

        musicManager.scheduler.queue(track);
    }

    public void skipTrack(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();
    }

//    public void emptyQueue() {
//
//    }

    public static void connectToFirstVoiceChannel(AudioManager manager) {
        if(!manager.isConnected()) {
            for(VoiceChannel vc : manager.getGuild().getVoiceChannels()) {
                manager.openAudioConnection(vc);
                break;
            }
        }
    }
}
