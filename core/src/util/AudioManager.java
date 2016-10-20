package util;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

//overloading these methods is that
//you can make some parameters optional.

public class AudioManager 
{
	public static final AudioManager instance = new AudioManager();
	private Music playingMusic;

	// singleton: prevent instantiation from other classes
	//and can be accessed from anywhere we want.
	private AudioManager () { }

	public void play (Sound sound) 
	{
		play(sound, 1);
	}

	public void play (Sound sound, float volume) 
	{
		play(sound, volume, 1);
	}

	public void play (Sound sound, float volume, float pitch) 
	{
		play(sound, volume, pitch, 0);
	}

	//check if the checkbox of the music is on or off 
	public void play (Sound sound, float volume, float pitch,float pan) 
	{
		if (!GamePreferences.instance.sound) 
			return;

		sound.play(GamePreferences.instance.volSound * volume,pitch, pan);
	}

	//If music is already playing, it is stopped first
	//then new music is initialized for playback and started if Music is enabled in the game settings
	public void play (Music music) 
	{
		stopMusic();
		playingMusic = music;
		
		if (GamePreferences.instance.music) 
		{
			music.setLooping(true);
			music.setVolume(GamePreferences.instance.volMusic);
			music.play();
		}
	}
	
	public void stopMusic () 
	{
		if (playingMusic != null) 
			playingMusic.stop();
	}
	
	public void onSettingsUpdated () 
	{
		if (playingMusic == null) 
			return;
		
		playingMusic.setVolume(GamePreferences.instance.volMusic);
		
		if (GamePreferences.instance.music) 
		{
			if (!playingMusic.isPlaying()) 
				playingMusic.play();
		} 
		else 
		{
			playingMusic.pause();
		}
	}
}