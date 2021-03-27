package com.guillermo.leif.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.guillermo.leif.DropGame;
import com.guillermo.leif.controllers.MidiListener;
import com.guillermo.leif.controllers.Op1Controller;

import javax.sound.midi.MidiUnavailableException;
import java.util.Iterator;

import static com.guillermo.leif.utility.Constants.*;

public class MidiDropsGameScreen implements Screen {
    /*Game State*/
    private static int bucketSpeed = MAX_BUCKET_SPEED;
    private static int moveBucketDirection = 0;
    private static long lastDropTime; // In nanoseconds
    private static boolean isPaused = false;
    private static int dropsGathered = 0;
    final DropGame game;
    private Op1Controller op1Controller;
    private OrthographicCamera camera;
    /*Meta-Assets*/
    private Texture dropImage;
    private Texture bucketImage;
    private Sound dropSound;
    private Music rainMusic;
    /*Interactive Assets*/
    private Rectangle bucket;
    private Array<Rectangle> raindrops;


    public MidiDropsGameScreen(final DropGame game) {
        this.game = game;
        this.op1Controller = new Op1Controller(this);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEW_WIDTH, VIEW_HEIGHT);
        game.batch = new SpriteBatch();

        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = new Texture(Gdx.files.internal("droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        // start the playback of the background music immediately
        rainMusic.setLooping(true);

        initializeBucket();

        raindrops = new Array<>();
        spawnRaindrop();


        MidiListener midiListener = new MidiListener(this);
        try {
            midiListener.beginListening();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void pause() {
        isPaused = true;
    }

    @Override
    public void resume() {
        isPaused = false;
    }

    @Override
    public void hide() {

    }


    @Override
    public void show() {
        rainMusic.play();
    }

    @Override
    public void render(float delta) {
        if (!isPaused) {
            handleMovement();
            updateRaindrops();
        }

        // clear the screen with a dark blue color. The
        // arguments to clear are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        ScreenUtils.clear(0, 0, 0.2f, 1);
        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the bucket and
        // all drops
        drawBatch();
    }

    private void drawBatch() {
        game.batch.begin();

        game.batch.draw(bucketImage, bucket.x, bucket.y);
        for (Rectangle raindrop : raindrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
        game.batch.dispose();
    }

    public void setMoveBucketDirection(int moveBucketDirection) {
        this.moveBucketDirection = moveBucketDirection;
    }

    public int getBucketSpeed() {
        return bucketSpeed;
    }

    public void setBucketSpeed(int bucketSpeed) {
        this.bucketSpeed = bucketSpeed;
    }

    public void togglePause() {
        if (this.isPaused) {
            this.resume();
        } else {
            this.pause();
        }
    }

    private void initializeBucket() {
        bucket = new Rectangle();
        bucket.x = (VIEW_WIDTH / 2) - (BUCKET_DIAMETER / 2);
        bucket.y = 20;
        bucket.width = BUCKET_DIAMETER;
        bucket.height = BUCKET_DIAMETER;
    }

    public void midiReceived(byte[] midiMessage, long timestamp) {
        op1Controller.midiRecieved(midiMessage, timestamp);
    }

    private void handleMovement() {
        if (Gdx.input.isTouched()) {
            handleTouchOrClickMovement();
        } else if (moveBucketDirection != 0) {
            handleEncoderMovement();
        } else {
            handleKeyMovement();
        }
        enforceScreenBoundsOnBucket();
    }

    private void enforceScreenBoundsOnBucket() {
        if (bucket.x < 0) {
            bucket.x = 0;
        }
        if (bucket.x > VIEW_WIDTH - BUCKET_DIAMETER) {
            bucket.x = VIEW_WIDTH - BUCKET_DIAMETER;
        }
    }

    private void handleTouchOrClickMovement() {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        bucket.x = (int) (touchPos.x - BUCKET_DIAMETER / 2);
    }

    private void handleEncoderMovement() {
        System.out.println("Direction : " + moveBucketDirection);
        bucket.x = bucket.x + (moveBucketDirection * bucketSpeed * Gdx.graphics.getDeltaTime());
        moveBucketDirection = 0;
    }

    private void handleKeyMovement() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            bucket.x += 200 * Gdx.graphics.getDeltaTime();
    }


    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, VIEW_WIDTH - 64);
        raindrop.y = VIEW_HEIGHT;
        raindrop.width = DROP_DIAMETER;
        raindrop.height = DROP_DIAMETER;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    private void updateRaindrops() {
        if (TimeUtils.nanoTime() - lastDropTime > TIME_BETWEEN_RAIN_DROPS) {
            spawnRaindrop();
        }
        for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if (raindrop.y + DROP_DIAMETER < 0) {
                iter.remove();
            } else {
                handleDropBucketCatch(raindrop, iter);
            }
        }
    }

    private void handleDropBucketCatch(Rectangle raindrop,
                                       Iterator<Rectangle> iter) {
        if (raindrop.overlaps(bucket)) {
            dropSound.play();
            iter.remove();
            dropsGathered++;
        }
    }


}
