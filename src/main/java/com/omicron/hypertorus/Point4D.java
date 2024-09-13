package com.omicron.hypertorus;

public class Point4D {
    double x, y, z, w;

    public Point4D(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    // Subtract two 4D points
    public Point4D subtract(Point4D other) {
        return new Point4D(this.x - other.x, this.y - other.y, this.z - other.z, this.w - other.w);
    }

    public Point4D subtract(double scale, Point4D normal) {
        return new Point4D(
                this.x - scale * normal.x,
                this.y - scale * normal.y,
                this.z - scale * normal.z,
                this.w - scale * normal.w
        );
    }

    // Add two 4D points
    public Point4D add(Point4D other) {
        return new Point4D(this.x + other.x, this.y + other.y, this.z + other.z, this.w + other.w);
    }

    // Multiply by a scalar
    public Point4D multiply(double scalar) {
        return new Point4D(this.x * scalar, this.y * scalar, this.z * scalar, this.w * scalar);
    }

    // Dot product of two 4D points
    public static double dot(Point4D p1, Point4D p2) {
        return p1.x * p2.x + p1.y * p2.y + p1.z * p2.z + p1.w * p2.w;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }

    public static Point4D closestPointOnPlane(Point4D q, double a, double b, double c, double d, double e) {
        Point4D normal = new Point4D(a, b, c, d);
        double dotProduct = a * q.x + b * q.y + c * q.z + d * q.w;
        double distance = (dotProduct - e) / (a * a + b * b + c * c + d * d);
        return q.subtract(distance, normal);
    }


    public static double distance(double x0, double y0, double z0, double w0, double A, double B, double C, double D, double E)
    {
        return Math.abs(A * x0 + B * y0 + C * z0 + D * w0 + E) / Math.sqrt(A * A + B * B + C * C + D * D);
    }
}
