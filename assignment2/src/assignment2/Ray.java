package assignment2;

public class Ray {
	
	private Vector origin, direction;
	private double currentRefractionIndex;
	
	public Ray(Vector origin, Vector direction, double refraction) {
		if(direction.norm() == 0) throw new IllegalArgumentException("Ray direction vector must not be zero.");
		
		this.origin = origin.copy();
		this.direction = direction.normalize();
		
		currentRefractionIndex = refraction;
		
	}
	
	public Ray(Vector origin, Vector direction) {
		this(origin, direction, 1);
	}
	
	public double getRefraction(){
		return currentRefractionIndex;
	}
	
	public Vector getOrigin(){
		return origin.copy();
	}
	
	public Vector getDirection() {
		return direction.copy();
	}
}
