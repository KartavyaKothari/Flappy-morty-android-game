package kartavya.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyMorty extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Texture gameOver;

	BitmapFont font;

	//ShapeRenderer shapeRenderer;
	Circle circle;
	Rectangle topTubeRectangle[];
	Rectangle bottomTubeRectangle[];

	Texture topPipe;
	Texture bottomPipe;

	Texture birds[];
	int flapState;
	float birdY;
	float gravity = 1.8f;
	float velocityBird;
	float velocityPipe = 8;
	float gap = 500;
	float thrust = -20;
	float maxPipeOffset;
	int activeScoringTube;
	int score;
	int bestScore;

	Random random;

	int noOfPipes = 4;
	float distanceBetweenPipes;
	LongRick longRicks[] = new LongRick[noOfPipes];

	int gameState;

	Preferences preferences;

	public void resetPipe(int i){
		longRicks[i].pipeX = distanceBetweenPipes*noOfPipes;
		longRicks[i].pipeOffset = (random.nextFloat()-0.5f)*maxPipeOffset;
	}

	public void startGame(){
		activeScoringTube = 0;
		score = 0;
		flapState = 0;
		gameState = 0;
		velocityBird = 0;

		topTubeRectangle = new Rectangle[noOfPipes];
		bottomTubeRectangle = new Rectangle[noOfPipes];

		birdY = Gdx.graphics.getHeight()/2-birds[flapState].getHeight()/2;

		for(int i = 0 ; i < noOfPipes ; i++){
			longRicks[i] = new LongRick((random.nextFloat()-0.5f)*maxPipeOffset,
					Gdx.graphics.getWidth() + i*distanceBetweenPipes);
			/*longRicks[i].pipeX = Gdx.graphics.getWidth() + i*distanceBetweenPipes;
			longRicks[i].pipeOffset = (random.nextFloat()-0.5f)*maxPipeOffset;*/
			topTubeRectangle[i] = new Rectangle();
			bottomTubeRectangle[i] = new Rectangle();
		}

		gameState = 0;
	}
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("bg.png");
		gameOver = new Texture("gameover.png");

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		birds = new Texture[2];
		birds[0]=new Texture("bird.png");
		birds[1]=new Texture("bird2.png");
		topPipe = new Texture("toptube.png");
		bottomPipe = new Texture("bottomtube.png");

		//shapeRenderer = new ShapeRenderer();
		circle = new Circle();

		maxPipeOffset = Gdx.graphics.getHeight()/2-gap/2;
		random = new Random();
		distanceBetweenPipes = Gdx.graphics.getWidth()*3/4;

		preferences = Gdx.app.getPreferences("bestData");
		bestScore = preferences.getInteger("bestScore",0);

		startGame();
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(img,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gameState == 1){
			if(longRicks[activeScoringTube].pipeX<Gdx.graphics.getWidth()/2){
				score++;

				if(bestScore < score){
					bestScore = score;
					preferences.putInteger("bestScore",bestScore);
					preferences.flush();
				}

				Gdx.app.log("Score",""+score);

				if(activeScoringTube<noOfPipes-1){
					activeScoringTube++;
				}else activeScoringTube = 0;
			}

			if(Gdx.input.justTouched()){
				velocityBird = thrust;
			}
			if(birdY>0) {
				birdY -= velocityBird;
				velocityBird += gravity;
			}else {
				gameState = 2;
			}
			if(flapState==0){
				flapState=1;
			}else flapState = 0;

			for(int i = 0 ; i < noOfPipes ; i++){
				if(longRicks[i].pipeX<0-topPipe.getWidth())resetPipe(i);

				longRicks[i].pipeX -= velocityPipe;
				batch.draw(topPipe, longRicks[i].pipeX,Gdx.graphics.getHeight()/2 + gap/2 + longRicks[i].pipeOffset);
				batch.draw(bottomPipe, longRicks[i].pipeX,Gdx.graphics.getHeight()/2-gap/2-bottomPipe.getHeight() + longRicks[i].pipeOffset);

				topTubeRectangle[i] = new Rectangle(longRicks[i].pipeX,Gdx.graphics.getHeight()/2 + gap/2 + longRicks[i].pipeOffset,topPipe.getWidth(),topPipe.getHeight());
				bottomTubeRectangle[i] = new Rectangle(longRicks[i].pipeX,Gdx.graphics.getHeight()/2-gap/2-bottomPipe.getHeight() + longRicks[i].pipeOffset,topPipe.getWidth(),topPipe.getHeight());
			}
		}else if(gameState == 0){
			if(Gdx.input.justTouched()){
				gameState = 1;
			}
		}else if(gameState == 2){

			batch.draw(gameOver,Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2,Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2);

			if(Gdx.input.justTouched()){
				startGame();
			}
		}

		batch.draw(birds[flapState],Gdx.graphics.getWidth()/2-birds[flapState].getWidth()/2,birdY);

		font.draw(batch,""+score,100,200);
		font.draw(batch,""+bestScore,Gdx.graphics.getWidth()-150,Gdx.graphics.getHeight()-100);

		batch.end();

		circle.set(Gdx.graphics.getWidth()/2,birdY+birds[flapState].getHeight()/2,birds[flapState].getWidth()/2);

		/*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.BLUE);
		shapeRenderer.circle(circle.x,circle.y,circle.radius);*/

		for (int i = 0 ; i < noOfPipes ; i++){
			/*shapeRenderer.rect(longRicks[i].pipeX,Gdx.graphics.getHeight()/2 + gap/2 + longRicks[i].pipeOffset,topPipe.getWidth(),topPipe.getHeight());
			shapeRenderer.rect(longRicks[i].pipeX,Gdx.graphics.getHeight()/2-gap/2-bottomPipe.getHeight() + longRicks[i].pipeOffset,topPipe.getWidth(),topPipe.getHeight());*/

			if(Intersector.overlaps(circle,bottomTubeRectangle[i])||Intersector.overlaps(circle,topTubeRectangle[i])){
				Gdx.app.log("Collision","Yes");
				gameState = 2;
			}
		}

		//shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
