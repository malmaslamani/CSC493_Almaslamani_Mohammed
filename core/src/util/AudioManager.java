package util;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager 
{
	public static final AudioManager instance = new AudioManager();
	private Music playingMusic;

	/** singleton: prevent instantiation from other classes
	 *  can be accessed from anywhere.
	 *  @author Mohammed Almaslamani
	 */
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

	/**
	 * to check if the sound checkbox is checked in the Options menu.
	 * @param sound
	 * @param volume
	 * @param pitch
	 * @param pan
	 */
	public void play (Sound sound, float volume, float pitch,float pan) 
	{
		if (!GamePreferences.instance.sound) 
			return;

		sound.play(GamePreferences.instance.volSound * volume,pitch, pan);
	}

	/**
	 * see if music is playing, stop it first.
	 * then will initialize new music if enabled from the options menu. 
	 * @param music
	 * @author Mohammed Almaslamani
	 */
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

	/**
	 * stop the music if it is playing.
	 */
	public void stopMusic () 
	{
		if (playingMusic != null) 
			playingMusic.stop();
	}

	/**
	 * used to allow the Options menu to inform AudioManager when settings have changed
	 */
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

