package com.example.asteroids;

import java.util.Random;
import javafx.scene.shape.Polygon;


/**
 *
 * @author Jed
 */
public class Asteroid extends Character {

    private double rotationalMovement;

    public Asteroid(int x, int y) {
        super(new AsteroidFactory().create(), x, y);

        Random rand = new Random();

        super.getCharacter().setRotate(rand.nextInt(360));

        int accelerationValue = 1 + rand.nextInt(10);

        for (int i = 0; i < accelerationValue; i++) {
            accelerate();
        }

        this.rotationalMovement = .5 - rand.nextDouble();

    }

    @Override
    public void move() {
        super.move();
        super.getCharacter().setRotate(super.getCharacter().getRotate()
                + this.rotationalMovement);

    }

}
