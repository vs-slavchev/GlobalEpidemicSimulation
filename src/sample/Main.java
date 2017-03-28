package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle( "Demo" );

        Group root = new Group();
        Scene theScene = new Scene( root );
        primaryStage.setScene( theScene );

        Canvas canvas = new Canvas( 512, 512 );
        root.getChildren().add( canvas );

        GraphicsContext graphics = canvas.getGraphicsContext2D();

        final long startNanoTime = System.nanoTime();

        new AnimationTimer()
        {
            private Queue<Point2D> points = new LinkedBlockingQueue<>();
            private Random rng = new Random();
            private double removeChance = 0.01;

            public void handle(long currentNanoTime)
            {
                logic();
                rendering();

                //aroundTheWorld(currentNanoTime);
            }

            private void logic() {
                if (rng.nextDouble() <= 0.02) {
                    points.add(new Point2D(rng.nextInt(500), rng.nextInt(500)));
                }

                if (rng.nextDouble() <= removeChance) {
                    points.poll();
                }

                if (points.size() > 500) {
                    removeChance = 0.03;
                }

                if (points.size() < 50) {
                    removeChance = 0.01;
                }
            }

            private void rendering() {
                graphics.setFill(Color.BLACK);
                graphics.fillRect(0, 0, 512, 512);
                graphics.setFill(Color.RED);
                points.stream().forEach(point -> graphics.fillOval(point.getX(), point.getY(), 20, 20));
                graphics.setFill(Color.BLUE);
                graphics.setFont(new Font("Arial", 30));
                graphics.fillText("number: " + points.size(), 10, 40);
            }

            private void aroundTheWorld(long currentNanoTime) {
                double t = (currentNanoTime - startNanoTime) / 1000000000.0;

                double x = 232 + 128 * Math.cos(t);
                double y = 232 + 128 * Math.sin(t);

                // background image clears canvas
                graphics.setFill(Color.BLACK);
                graphics.fillRect(0, 0, 512, 512);
                graphics.setFill(Color.BLUE);
                graphics.fillOval(x, y, 20, 20);
                graphics.setFill(Color.YELLOW);
                graphics.fillOval( 196, 196, 60, 60 );
            }
        }.start();

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
