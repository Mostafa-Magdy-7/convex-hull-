package com.example.convexhull;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private List<Point2D> points = new ArrayList<>(); // create list to store mouse positions
    private final List<Point2D> hull = new ArrayList<>(); // create final list to store vertices of convex hull
    private Pane pane = new Pane();

    @Override
    public void start(Stage primaryStage) {
        double width = 400;
        double height = 400;

        Button finishButton = new Button("Finish"); // create Finish button
        finishButton.setTranslateX(10);
        finishButton.setTranslateY(10);
        finishButton.setOnAction(e -> { // add action event handler to Finish button
            if (points.size() >= 3) { // check if there are at least 3 points to compute the convex hull
                hull.clear(); // clear the list of vertices of the convex hull
                hull.addAll(computeConvexHull(points)); // compute the convex hull and add the vertices to the final list
                drawPolygon(hull, pane); // draw the polygon of the convex hull
            }
        });

        pane.setOnMouseClicked(e -> { // add mouse click event handler to pane
            if (e.getButton() == MouseButton.PRIMARY) { // check if left mouse button is clicked
                double x = e.getX();
                double y = e.getY();
                Circle circle = new Circle(x, y, 5, Color.RED); // create new circle at mouse position
                points.add(new Point2D(x, y)); // add mouse position to list

                Text text = new Text("(" + x + ", " + y + ")"); // create new text object with coordinates
                text.setX(x);
                text.setY(y - 10); // position text above the circle
                pane.getChildren().addAll(circle, text); // add circle and text to pane
            }
        });

        primaryStage.setScene(new Scene(pane, width, height));
        primaryStage.setTitle("Click to see position..");
        primaryStage.show();

        pane.getChildren().add(finishButton); // add Finish button to pane
    }

    private List<Point2D> computeConvexHull(List<Point2D> points) {
        List<Point2D> hull = new ArrayList<>(); // create an empty list to store the vertices of the convex hull
        int n = points.size();

        if (n < 3) { // check if there are at least 3 points to compute the convex hull
            return hull;
        }

        // Find the point with the lowest y-coordinate (and the leftmost x-coordinate if there are ties) and call it the "pivot".
        Point2D pivot = points.get(0);
        for (int i = 1; i < n; i++) {
            if (points.get(i).getY() < pivot.getY() || (points.get(i).getY() == pivot.getY() && points.get(i).getX() < pivot.getX())) {
                pivot = points.get(i);
            }
        }

        // Sort the remaining points by the angle they make with the line segment between the pivot and the point with the highest y-coordinate (and the rightmost x-coordinate if there are ties), in counterclockwise order.
        List<Point2D> sortedPoints = new ArrayList<>(points);
        sortedPoints.remove(pivot);
        Point2D finalPivot = pivot;
        sortedPoints.sort((p1, p2) -> Double.compare(p1.angle(finalPivot), p2.angle(finalPivot)));

        // Create an empty stack and push the pivot and the first point from the sorted list onto it.
        hull.add(pivot);
        hull.add(sortedPoints.get(0));

        // For each remaining point in the sorted list, if it turns left with respect to the last two points on the stack, push it onto the stack. Otherwise, pop the last point from the stack and repeat until the new point turns left with respect to the last two points on the stack. Then push the new point onto the stack.
        for (int i = 1; i < n - 1; i++) {
            Point2D p = sortedPoints.get(i);
            while (hull.size() >= 2 && !isLeftTurn(hull.get(hull.size() - 2), hull.get(hull.size() - 1), p)) {
                hull.remove(hull.size() - 1);
            }
            hull.add(p);
        }

        return hull;
    }

    private boolean isLeftTurn(Point2D a, Point2D b, Point2D c) {
        double crossProduct = (b.getX() - a.getX()) * (c.getY() - a.getY()) - (b.getY() - a.getY()) * (c.getX() - a.getX());
        return crossProduct > 0;
    }

    private void drawPolygon(List<Point2D> points, Pane pane) {
        int n = points.size();

        for (int i = 0; i < n; i++) {
            Point2D p1 = points.get(i);
            Point2D p2 = points.get((i + 1) % n);
            Line line = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            line.setStroke(Color.BLUE);
            pane.getChildren().add(line);
        }
    }
}
