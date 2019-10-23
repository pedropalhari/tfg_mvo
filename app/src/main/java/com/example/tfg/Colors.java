package com.example.tfg;


import org.opencv.core.Scalar;

public class Colors {
    static Scalar BLUE = new Scalar(0, 120, 255, 0);
    static Scalar PURPLE = new Scalar(189, 0, 255, 0);
    static Scalar ORANGE = new Scalar(255, 154, 0, 0);
    static Scalar GREEN = new Scalar(1, 255, 31, 0);
    static Scalar YELLOW = new Scalar(227, 255, 0, 0);

    static Scalar getRandomColor() {
        Scalar[] Colors = {BLUE, PURPLE, ORANGE, GREEN, YELLOW};

        return Colors[(int) (Math.random() * Colors.length)];
    }
}