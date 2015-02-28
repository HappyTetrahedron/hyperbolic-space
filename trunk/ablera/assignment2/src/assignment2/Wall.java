package assignment2;

public class Wall extends Shape {
	
	private Vector origin;
	private Vector normal;
	
	public Wall(Vector origin, Vector normal, Vector color) {
		super(color);
		
		this.origin = origin.copy();
		this.normal = normal.normalize();
	}
	
	public Wall(Vector origin, Vector normal, Vector color, Vector ambient, Vector diffuse, Vector specular, double exponent, Vector reflection){
		super(color, ambient, diffuse, specular, exponent, reflection);
		this.origin = origin.copy();
		this.normal = normal.normalize();

	}
	
	public Vector getNormal(Vector p) {
		return normal.copy();
		// A plane's normal is always the same
	}
	
	public Vector getOrigin() {
		return origin.copy();
	}
	
	public double intersect(Ray r) {
		// (O*N - S*N) / D*N = t
		
		// >= 0 because our wall is invisible from behind.
//		if (r.getDirection().dot(normal) >= 0) return Double.NaN;
		
		double t = (origin.dot(normal) - r.getOrigin().dot(normal)) / r.getDirection().dot(normal);

		if (t <= 0) return Double.NaN;
		return t;
	}

}
