package assignment2;

public interface Light {
	
	public boolean isShadowCasting();
	
	public Vector getColor();
	
	public Vector getPosition();
	
	public Vector getRandPosition();
	
	public double getDistance(Vector from);
}
