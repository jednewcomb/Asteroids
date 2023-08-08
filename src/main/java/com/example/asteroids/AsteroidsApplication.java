package com.example.asteroids;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AsteroidsApplication extends Application {

    public static int WIDTH = 600;
    public static int HEIGHT = 400;

    @Override
    public void start(Stage window) throws Exception {
        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);

        Ship ship = new Ship(WIDTH / 2, HEIGHT / 2);
        pane.getChildren().add(ship.getCharacter());

        Scene view = new Scene(pane);
        handleActions(view, pane, ship);

        window.setTitle("Asteroids!");
        window.setScene(view);
        window.show();

    }

    public void handleActions(Scene view, Pane pane, Ship ship) {
        Text text = new Text(10, 20, "Points: 0");
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

            long lastUpdate = 0;
            @Override

            public void handle(long now) {

                //maybe you could add a timer which causes
                //a higher likelihood for asteroids to be made


                if (Math.random() < 0.010) {
                    Asteroid asteroid = new Asteroid(WIDTH, HEIGHT);
                    if (!asteroid.collide(ship)) {
                        asteroids.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                    }
                }

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

                    //With our long lastUpdate, we can use the animation timer
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
                        lastUpdate = now ;
                    }

                }

                ship.move();
                asteroids.forEach(Asteroid::move);
                projectiles.forEach(Projectile::move);

                //this needs to be improved
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
     * game which are no longer relevant, like a projectile
     * which is off-screen or an asteroid that's been destroyed.
     *
     * @param asteroids - The array containing asteroids.
     * @param projectiles - The array containing projectiles.
     * @param pane - Pane where game occurs.
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

    public List<Asteroid> createAsteroids() {
        List<Asteroid> asteroids = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < 10; i++) {
            Asteroid asteroid
                    = new Asteroid(rand.nextInt(WIDTH), rand.nextInt(HEIGHT));
            asteroids.add(asteroid);
        }

        return asteroids;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
