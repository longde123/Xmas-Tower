package com.noxalus.xmastower.android;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.example.games.basegameutils.GameHelper;
import com.noxalus.xmastower.XmasTower;
import com.noxalus.xmastower.gameservices.ActionResolver;
import com.google.android.gms.games.Games;

public class AndroidLauncher extends AndroidApplication implements GameHelper.GameHelperListener, ActionResolver {
	private GameHelper gameHelper;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new XmasTower(this), config);

		if (gameHelper == null) {
			gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
			gameHelper.enableDebugLog(true);
		}

		gameHelper.setup(this);
	}

	@Override
	public void onStart(){
		super.onStart();
		gameHelper.onStart(this);
	}

	@Override
	public void onStop(){
		super.onStop();
		gameHelper.onStop();
	}

	@Override
	public void onActivityResult(int request, int response, Intent data) {
		super.onActivityResult(request, response, data);
		gameHelper.onActivityResult(request, response, data);
	}

	@Override
	public boolean getSignedInGPGS() {
		return gameHelper.isSignedIn();
	}

	@Override
	public void loginGPGS() {
		try {
			runOnUiThread(new Runnable(){
				public void run() {
					gameHelper.beginUserInitiatedSignIn();
				}
			});
		} catch (final Exception ex) {
		}
	}

	@Override
	public void submitScoreGPGS(int score) {
		Games.Leaderboards.submitScore(gameHelper.getApiClient(), getString(R.string.leaderboard_leaderboard), score);
	}

	@Override
	public void unlockAchievementGPGS(Achievement achievement) {
		String achievementId = "";

		switch (achievement) {
			case ACHIEVEMENT_1_M:
				achievementId = getString(R.string.achievement_1_m);
				break;
			case ACHIEVEMENT_10_M:
				achievementId = getString(R.string.achievement_10_m);
				break;
			case ACHIEVEMENT_100_M:
				achievementId = getString(R.string.achievement_100_m);
				break;
			case ACHIEVEMENT_1_KM:
				achievementId = getString(R.string.achievement_1_km);
				break;
			case ACHIEVEMENT_ITS_TOO_SMALL:
				achievementId = getString(R.string.achievement_its_too_small);
				break;
			case ACHIEVEMENT_ITS_TOO_BIG:
				achievementId = getString(R.string.achievement_its_too_big);
				break;
			case ACHIEVEMENT_IM_FEELING_DIZZY:
				achievementId = getString(R.string.achievement_im_feeling_dizzy);
				break;
		}

		if (!achievementId.equals(""))
			Games.Achievements.unlock(gameHelper.getApiClient(), achievementId);
	}

	@Override
	public void getLeaderboardGPGS() {
		if (gameHelper.isSignedIn()) {
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(), getString(R.string.leaderboard_leaderboard)), 100);
		} else if (!gameHelper.isConnecting()) {
			loginGPGS();
		}
	}

	@Override
	public void getAchievementsGPGS() {
		if (gameHelper.isSignedIn()) {
			startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), 100);
		} else if (!gameHelper.isConnecting()) {
			loginGPGS();
		}
	}

	@Override
	public void onSignInFailed() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSignInSucceeded() {
		// TODO Auto-generated method stub
	}
}
