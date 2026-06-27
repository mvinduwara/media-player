package com.mediaplayerapp.ui.component;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;

import java.util.Random;

public class SpectrumVisualizer extends Pane {

    private static final int BAR_COUNT = 48;
    private static final Color BAR_COLOR_LOW = Color.web("#7C3AED");
    private static final Color BAR_COLOR_HIGH = Color.web("#C084FC");
    private static final Color REFLECT_COLOR = Color.web("#7C3AED", 0.2);

    private final Canvas canvas;
    private AnimationTimer timer;
    private final double[] heights = new double[BAR_COUNT];
    private final double[] targets = new double[BAR_COUNT];
    private final double[] velocities = new double[BAR_COUNT];
    private final Random random = new Random();
    private boolean active = false;

    public SpectrumVisualizer(double width, double height) {
        canvas = new Canvas(width, height);
        getChildren().add(canvas);
        setPrefSize(width, height);
        setMinSize(width, height);
        initBars();
        startAnimation();
    }

    private void initBars() {
        for (int i = 0; i < BAR_COUNT; i++) {
            heights[i] = 2;
            targets[i] = 2;
            velocities[i] = 0;
        }
    }

    private void startAnimation() {
        timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate < 16_000_000) return;
                lastUpdate = now;

                if (active) {
                    updateTargets();
                } else {
                    idleTargets();
                }
                animate();
                render();
            }
        };
        timer.start();
    }

    private void updateTargets() {
        double canvasH = canvas.getHeight();
        for (int i = 0; i < BAR_COUNT; i++) {
            if (random.nextDouble() < 0.3) {
                double center = BAR_COUNT / 2.0;
                double dist = Math.abs(i - center) / center;
                double baseAmp = canvasH * (0.5 + random.nextDouble() * 0.4);
                double falloff = 1 - dist * 0.6;
                targets[i] = Math.max(4, baseAmp * falloff * (0.4 + random.nextDouble() * 0.6));
            }
        }
    }

    private void idleTargets() {
        for (int i = 0; i < BAR_COUNT; i++) {
            if (random.nextDouble() < 0.08) {
                targets[i] = 2 + random.nextDouble() * 8;
            }
        }
    }

    private void animate() {
        for (int i = 0; i < BAR_COUNT; i++) {
            double diff = targets[i] - heights[i];
            velocities[i] = velocities[i] * 0.6 + diff * 0.15;
            heights[i] += velocities[i];
            heights[i] = Math.max(2, heights[i]);
            targets[i] *= 0.85;
            targets[i] = Math.max(2, targets[i]);
        }
    }

    private void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        double midY = h * 0.6;

        gc.clearRect(0, 0, w, h);

        double totalBarWidth = w * 0.85;
        double barWidth = totalBarWidth / BAR_COUNT;
        double gap = barWidth * 0.25;
        double bw = barWidth - gap;
        double startX = (w - totalBarWidth) / 2.0;

        for (int i = 0; i < BAR_COUNT; i++) {
            double x = startX + i * barWidth;
            double barH = Math.min(heights[i], midY - 2);
            double normalizedH = barH / (midY - 2);

            LinearGradient grad = new LinearGradient(
                    0, midY - barH, 0, midY,
                    false, CycleMethod.NO_CYCLE,
                    new Stop(0, BAR_COLOR_HIGH),
                    new Stop(1, BAR_COLOR_LOW)
            );

            gc.setFill(grad);
            gc.fillRoundRect(x, midY - barH, bw, barH, 2, 2);

            double reflectH = barH * 0.3;
            LinearGradient reflectGrad = new LinearGradient(
                    0, midY, 0, midY + reflectH,
                    false, CycleMethod.NO_CYCLE,
                    new Stop(0, REFLECT_COLOR),
                    new Stop(1, Color.TRANSPARENT)
            );
            gc.setFill(reflectGrad);
            gc.fillRoundRect(x, midY, bw, reflectH, 2, 2);
        }
    }

    public void setActive(boolean active) {
        this.active = active;
        if (!active) initBars();
    }

    public void resize(double width, double height) {
        canvas.setWidth(width);
        canvas.setHeight(height);
        setPrefSize(width, height);
    }

    public void dispose() {
        if (timer != null) timer.stop();
    }
}