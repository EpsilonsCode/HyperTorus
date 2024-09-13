package com.omicron.hypertorus;

import java.util.*;

public class QuickHull3D {
    public static class Point3D {
        double x, y, z;

        public Point3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double distance(Point3D p) {
            return Math.sqrt(Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2) + Math.pow(z - p.z, 2));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point3D point3D = (Point3D) o;
            return Double.compare(point3D.x, x) == 0 &&
                    Double.compare(point3D.y, y) == 0 &&
                    Double.compare(point3D.z, z) == 0;
        }

        @Override
        public int hashCode() {
            return (int)(x * 31 + y * 31 + z * 31);
        }

        @Override
        public String toString() {
            return "Point3D{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }
    }

    public static class Face {
        Point3D[] points;

        public Face(Point3D p1, Point3D p2, Point3D p3) {
            this.points = new Point3D[] { p1, p2, p3 };
        }

        @Override
        public String toString() {
            return "Face{" +
                    "points=" + Arrays.toString(points) +
                    '}';
        }
    }

    public static Set<Face> quickHull(List<Point3D> points) {
        if (points.size() < 4) throw new IllegalArgumentException("At least 4 points required");

        // Find extreme points
        Point3D minX = points.get(0), maxX = points.get(0), minY = points.get(0), maxY = points.get(0);
        Point3D minZ = points.get(0), maxZ = points.get(0);

        for (Point3D p : points) {
            if (p.x < minX.x) minX = p;
            if (p.x > maxX.x) maxX = p;
            if (p.y < minY.y) minY = p;
            if (p.y > maxY.y) maxY = p;
            if (p.z < minZ.z) minZ = p;
            if (p.z > maxZ.z) maxZ = p;
        }

        Set<Face> hull = new HashSet<>();

        // Start with a tetrahedron
        hull.add(new Face(minX, maxX, minY));
        hull.add(new Face(minX, maxX, minZ));
        hull.add(new Face(minY, maxY, minZ));
        hull.add(new Face(minZ, maxZ, minY));

        // Recursively find the convex hull
        List<Point3D> outsidePoints = new ArrayList<>(points);
        outsidePoints.removeAll(List.of(minX, maxX, minY, maxY, minZ, maxZ));

        for (Point3D point : outsidePoints) {
            updateHull(hull, point);
        }

        return hull;
    }

    private static void updateHull(Set<Face> hull, Point3D point) {
        Set<Face> toRemove = new HashSet<>();
        Set<Face> toAdd = new HashSet<>();

        for (Face face : hull) {
            if (isPointOutsideFace(face, point)) {
                toRemove.add(face);
                for (int i = 0; i < 3; i++) {
                    toAdd.add(new Face(face.points[i], face.points[(i + 1) % 3], point));
                }
            }
        }

        hull.removeAll(toRemove);
        hull.addAll(toAdd);
    }

    private static boolean isPointOutsideFace(Face face, Point3D point) {
        // Vector calculations to check if a point is outside a triangle in 3D space
        Point3D v0 = new Point3D(face.points[1].x - face.points[0].x, face.points[1].y - face.points[0].y, face.points[1].z - face.points[0].z);
        Point3D v1 = new Point3D(face.points[2].x - face.points[0].x, face.points[2].y - face.points[0].y, face.points[2].z - face.points[0].z);
        Point3D normal = crossProduct(v0, v1);

        Point3D vectorToPoint = new Point3D(point.x - face.points[0].x, point.y - face.points[0].y, point.z - face.points[0].z);
        double dotProduct = dotProduct(normal, vectorToPoint);

        return dotProduct > 0;
    }

    private static Point3D crossProduct(Point3D v1, Point3D v2) {
        return new Point3D(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x);
    }

    private static double dotProduct(Point3D v1, Point3D v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public static void main(String[] args) {
        List<Point3D> points = new ArrayList<>();
        points.add(new Point3D(0, 0, 0));
        points.add(new Point3D(1, 0, 0));
        points.add(new Point3D(0, 1, 0));
        points.add(new Point3D(0, 0, 1));
        points.add(new Point3D(1, 1, 1));

        Set<Face> hull = quickHull(points);
        hull.forEach(System.out::println);
        System.out.println("Convex hull faces: " + hull.size());
    }
}
