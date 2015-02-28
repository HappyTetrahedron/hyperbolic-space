package assignment2;

import java.util.Random;

public class SphereLight extends Sphere implements Light {
	
	private boolean isShadowCasting;
	
	private Random rand = new Random();
	
	public SphereLight(double radius, Vector center, Vector color) {
		this(radius, center, color, true);
	}
	
	public SphereLight(double radius, Vector center, Vector color, boolean isShadowCasting) {
		super(radius, center, color, new Vector(1), new Vector(), new Vector(), 0, new Vector());
		
		this.isShadowCasting = isShadowCasting;
		isLight = true;
		
	}

	@Override
	public Vector getPosition() {
		return center.copy();
	}
	
	@Override
	public Vector getRandPosition() {
		return center.plus(new Vector(rand.nextDouble(), rand.nextDouble(), rand.nextDouble()).normalize().smult(radius));
	}

	@Override
	public double getDistance(Vector from) {
		return Math.abs(center.minus(from).norm() - radius);
	}
	
	@Override
	public boolean isShadowCasting() {
		return isShadowCasting;
	}

}
