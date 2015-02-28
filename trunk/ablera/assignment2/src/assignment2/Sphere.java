package assignment2;

public class Sphere extends Shape{
	// Implement class Sphere according to the exercise sheet!

	protected Vector center;
	protected double radius;

	
	public Sphere(double radius, Vector center, Vector color) {
		super(color);
		
		this.radius = Math.abs(radius);
		this.center = center;
		
	}
	
	public Sphere(double radius, Vector center, Vector color, Vector ambient, Vector diffuse, Vector specular, double exponent, Vector reflection){
		super(color, ambient, diffuse, specular, exponent, reflection);
		
		this.radius = Math.abs(radius);
		this.center = center;
	}
	
	public Sphere(double radius, Vector center, Vector color, Vector ambient, Vector diffuse, Vector specular, double exponent, Vector reflection, Vector refraction, double refDelta){
		super(color, ambient, diffuse, specular, exponent, reflection, refraction, refDelta);
		
		this.radius = Math.abs(radius);
		this.center = center;
	}
	
	public Vector getCenter() {
		return center.copy();
	}
	
	public Vector getNormal(Vector p) {
		return p.minus(center).normalize();
	}

	public double intersect(Ray r) {
		// returns the parameter t for the ray equation X = S + D*t 
		// that gives the intersection point of ray r and this sphere.
		// If the ray does not intersect this, Double.NaN is returned.
		// formula: t1,2 = -V*D +- sqrt( (V*D)^2 - V*V + r^2 )  with V = S - C
		
		Vector v = r.getOrigin().minus(center);
		Vector d = r.getDirection();
		
		double t1, t2;
		
		double determinant = (v.dot(d) * v.dot(d)) - v.dot(v) + radius*radius;
		if (determinant < 0) return Double.NaN;
		
		
		t1 = Vector.VEC_ZERO.minus(v).dot(d) + Math.sqrt(determinant);
		t2 = Vector.VEC_ZERO.minus(v).dot(d) - Math.sqrt(determinant);
		
		
		// We are not interested in solutions below zero.
		if(Math.min(t1, t2) <= 0){
			if (Math.max(t1, t2) <= 0) return Double.NaN;
			return Math.max(t1,  t2);
		}
		return Math.min(t1, t2);
		
	}
	
	
}
