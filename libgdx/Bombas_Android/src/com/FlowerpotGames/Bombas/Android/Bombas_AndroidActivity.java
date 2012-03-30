package com.FlowerpotGames.Bombas.Android;

import com.FlowerpotGames.Bombas.BombaGame;
import com.badlogic.gdx.backends.android.AndroidApplication;

import android.content.res.Configuration;
import android.os.Bundle;

public class Bombas_AndroidActivity extends AndroidApplication {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initialize(new BombaGame(), false);
    }
    
	// Handle events like sliding the keyboard in/out since android kills the
	// app
	// to restart it.
	@Override
	public void onConfigurationChanged(Configuration config)
	{
		super.onConfigurationChanged(config);
	}
}