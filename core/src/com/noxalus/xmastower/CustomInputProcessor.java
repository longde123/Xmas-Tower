package com.noxalus.xmastower;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.noxalus.xmastower.entities.Gift;

public class CustomInputProcessor implements InputProcessor{

    private final String TAG = "CustomInputProcessor";
    private XmasTower _game;
    private Sound _currentPlayedSound;

    public CustomInputProcessor(XmasTower game)
    {
        _game = game;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        //body.applyForceToCenter(0f,10f,true);
        Gdx.app.log(TAG, "Touch position: " + x + ", " + y);

        if (pointer > 0 || _game.MouseJoint != null)
            return false;

        // translate the mouse coordinates to World coordinates
        _game.FixtureTestPoint.set(x, y, 0);
        _game.Camera.unproject(_game.FixtureTestPoint);
        _game.FixtureTestPoint.set(_game.FixtureTestPoint.x / Config.PIXELS_TO_METERS, _game.FixtureTestPoint.y / Config.PIXELS_TO_METERS, 0);

        // ask the World which bodies are within the given
        // bounding box around the mouse pointer
        _game.HitBody = null;
        float value = 0.1f;
        _game.World.QueryAABB(
                _game.FixtureQueryCallback,
                _game.FixtureTestPoint.x - value,
                _game.FixtureTestPoint.y - value,
                _game.FixtureTestPoint.x + value,
                _game.FixtureTestPoint.y + value
        );

        // if we hit something we create a new mouse joint
        // and attach it to the hit body.
        if (_game.MouseJoint == null && _game.HitBody != null) {
            _currentPlayedSound = Assets.grabSounds[MathUtils.random(0, Assets.grabSounds.length - 1)];
            _currentPlayedSound.play();
            MouseJointDef def = new MouseJointDef();
            def.bodyA = _game.HitBody;
            def.bodyB = _game.HitBody;
            def.collideConnected = true;
            def.target.set(_game.FixtureTestPoint.x, _game.FixtureTestPoint.y);
            def.maxForce = (10000.0f / Config.PIXELS_TO_METERS) * _game.HitBody.getMass();

            Gdx.app.log(TAG, "Create a new mouse joint");
            _game.MouseJoint = (MouseJoint) _game.World.createJoint(def);
            _game.HitBody.setAwake(true);
        }

        return false;
    }


    @Override
    public boolean touchDragged (int x, int y, int pointer) {

        Gdx.app.log(TAG, "Touch dragged pointer: " + pointer);
        Gdx.app.log(TAG, "Touch dragged: " + x + ", " + y);

        if (pointer > 0)
            return false;

        if (_game.MouseJoint != null) {
            _game.Camera.unproject(_game.FixtureTestPoint.set(x, y, 0));
            _game.MouseJoint.setTarget(_game.MouseJointTarget.set(
                    _game.FixtureTestPoint.x / Config.PIXELS_TO_METERS,
                    _game.FixtureTestPoint.y / Config.PIXELS_TO_METERS
            ));
        }

        return false;
    }

    @Override
    public boolean touchUp (int x, int y, int pointer, int button) {
        if (_currentPlayedSound != null)
        {
            _currentPlayedSound.stop();
            _currentPlayedSound = null;
        }

        if (_game.MouseJoint != null && _game.HitBody != null) {
            Gdx.app.log(TAG, "Remove mouse joint from touch up");

            ((Gift)(_game.HitBody.getUserData())).isSelected(false);
            _game.World.destroyJoint(_game.MouseJoint);
            _game.MouseJoint = null;
            _game.HitBody = null;
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}