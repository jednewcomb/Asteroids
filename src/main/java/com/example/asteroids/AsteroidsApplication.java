package com.example.asteroids;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
public class AsteroidsApplication extends Application {

    public final static int WIDTH = 600;
    public final static int HEIGHT = 400;
    public long lastUpdate = 0;
    public double difficultyTimer = 0.010;
    public long difficultyUpdate = 0;
    final long since = 30_000_000_000L;

    /**
     * This method is where the logic controlling the
     * control events and animation timer of the game.
     *
     * @param view - The scene on which our pane sits.
     * @param pane - The pane on which the game occurs.
     * @param ship - The user ship.
     */
    public void handleActions(Scene view, Pane pane, Ship ship) {
        Text text = new Text(10, 20, "Points: 0");
        text.setFill(Color.WHITE);
        pane.getChildren().add(text);

        AtomicInteger points = new AtomicInteger();

        Map<KeyCode, Boolean> pressedKeys = new HashMap<>();
        List<Projectile> projectiles = new ArrayList<>();
        List<Asteroid> asteroids = createAsteroids();

        for (Asteroid a : asteroids) {
            pane.getChildren().add(a.getCharacter());
        }

        view.setOnKeyPressed(event -> pressedKeys.put(event.getCode(), Boolean.TRUE));

        view.setOnKeyReleased(event -> pressedKeys.put(event.getCode(), Boolean.FALSE));

        new AnimationTimer() {
            @Override
            public void handle(long now) {

                if (pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    ship.turnLeft();
                }

                if (pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    ship.turnRight();
                }

                if (pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    ship.accelerate();
                }

                if (pressedKeys.getOrDefault(KeyCode.SPACE, false)) {

                    //With long lastUpdate, we can use the animation timer
                    //to check that the last time a projectile was shot was
                    //500 milliseconds (.5 seconds)
                    if (now - lastUpdate >= 500_000_000) {
                        Projectile projectile
                                = new Projectile((int) ship.getCharacter().getTranslateX(),
                                (int) ship.getCharacter().getTranslateY());
                        projectile.getCharacter().setRotate(ship.getCharacter().getRotate());
                        projectiles.add(projectile);
                        projectile.accelerate();
                        projectile.setMovement(projectile.getMovement().normalize().multiply(3));
                        pane.getChildren().add(projectile.getCharacter());
                        lastUpdate = now;
                    }
                }

                //Timer to increase asteroid spawn rate every 30 seconds
                if (now - difficultyUpdate >= since) {
                    if (difficultyTimer <= 0.050) {
                        difficultyTimer += 0.001;
                    }
                    difficultyUpdate = now;
                }

                if (Math.random() < difficultyTimer) {
                    Asteroid asteroid = new Asteroid(WIDTH, HEIGHT);
                    if (!asteroid.collide(ship)) {
                        asteroids.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                    }
                }

                ship.move();
                asteroids.forEach(Asteroid::move);
                projectiles.forEach(Projectile::move);

                //Check projectiles and asteroids for collisions
                projectiles.forEach(projectile -> {
                    asteroids.forEach(asteroid -> {
                        if (projectile.collide(asteroid)) {
                            projectile.setAliveStatus(false);
                            asteroid.setAliveStatus(false);
                        }
                    });

                    if (!projectile.getAliveStatus()) {
                        text.setText("points: " + points.addAndGet(100));
                    }

                    if (!projectile.isOnScreen()) {
                        pane.getChildren().remove(projectile.getCharacter());
                    }
                });

                cleanUp(asteroids, projectiles, pane);

                asteroids.forEach(asteroid -> {
                    if (ship.collide(asteroid)) {
                        stop();
                    }
                });
            }

        }.start();
    }

    /**
     * This is a cleanup method which removes objects from the
     * lists which are no longer relevant, like a projectile
     * which is off-screen or an asteroid that's been destroyed.
     *
     * @param asteroids   - The array containing asteroids.
     * @param projectiles - The array containing projectiles.
     * @param pane        - Pane where game occurs.
     */
    public void cleanUp(List<Asteroid> asteroids, List<Projectile> projectiles,
                        Pane pane) {

        projectiles.stream()
                .filter(projectile -> !projectile.getAliveStatus())
                .forEach(projectile -> pane.getChildren()
                        .remove(projectile.getCharacter()));

        projectiles.removeAll(projectiles.stream()
                .filter(projectile -> !projectile.getAliveStatus())
                .collect(Collectors.toList()));

        projectiles.removeAll(projectiles.stream()
                .filter(projectile -> !projectile.isOnScreen())
                .collect(Collectors.toList()));

        asteroids.stream()
                .filter(asteroid -> !asteroid.getAliveStatus())
                .forEach(asteroid -> pane.getChildren()
                        .remove(asteroid.getCharacter()));

        asteroids.removeAll(asteroids.stream()
                .filter(asteroid -> !asteroid.getAliveStatus())
                .collect(Collectors.toList()));

    }

    /**
     * Create the first 15 asteroids found in the game,
     * add them to an arraylist then add them to pane.
     *
     * @return asteroids - The ArrayList of asteroids.
     */
    public List<Asteroid> createAsteroids() {
        List<Asteroid> asteroids = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < 15; i++) {
            int spawnX = rand.nextInt(WIDTH);
            int spawnY = rand.nextInt(HEIGHT);

            //padding loops to ensure we don't spawn
            //an asteroid right on our ship, because
            //that would be pretty lame, man.
            while (spawnX >= (WIDTH / 2) - 50 && spawnX <= (WIDTH / 2) + 50) {
                spawnX = rand.nextInt();
            }

            while (spawnY >= (HEIGHT / 2) - 50 && spawnX <= (HEIGHT / 2) + 50) {
                spawnY = rand.nextInt();
            }

            Asteroid asteroid
                    = new Asteroid(spawnX, spawnY);
            asteroids.add(asteroid);
        }

        return asteroids;
    }

    /**
     * Create the game window as well as the user ship.
     *
     * @param window - The stage on which our scene sits.
     * @throws Exception - We don't want the game stopped
     *                   by these pesky exceptions!
     */
    @Override
    public void start(Stage window) throws Exception {
        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);

        Ship ship = new Ship(WIDTH / 2, HEIGHT / 2);
        pane.getChildren().add(ship.getCharacter());

        Scene view = new Scene(pane);
        view.setFill(Color.BLACK);
        handleActions(view, pane, ship);

        window.setTitle("Asteroids!");
        window.setScene(view);
        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
