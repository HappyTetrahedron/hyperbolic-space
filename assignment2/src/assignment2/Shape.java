package assignment2;

public abstract class Shape {
	// Implement class Sphere according to the exercise sheet!

	
	//Don't make these public.
	protected Vector ambient, diffuse, specular, reflection, refraction;
	protected double exponent, refDelta;
	
	protected boolean isLight = false;

	protected Vector color;

	public Shape(Vector color) {
		this(color, new Vector(color.norm()), new Vector(0.7), new Vector(0.3), 100d, new Vector());
	}
	
	public Shape(Vector color, Vector ambient, Vector diffuse, Vector specular, double exponent, Vector reflection) {
		this(color, ambient, diffuse, specular, exponent, reflection, new Vector(), 0);
	}
	
	public Shape(Vector color, Vector ambient, Vector diffuse, Vector specular, double exponent, Vector reflection, Vector refraction, double refDelta) {
		this.color = color;

		this.ambient = ambient.clamp(0, 10);
		this.diffuse = diffuse.clamp(0, 10);
		this.specular = specular.clamp(0, 10);
		this.exponent = exponent;
		this.reflection = reflection.clamp(0, 10);
		this.refraction = refraction.clamp(0, 10);
		this.refDelta = refDelta;
	}

	public Vector getColor() {
		return color.copy();
	}
	
	public Vector getAmbient() {
		return ambient;
	}
	
	public Vector getDiffuse() {
		return diffuse;
	}
	
	public Vector getSpecular() {
		return specular;
	}
	
	public Double getExponent() {
		return exponent;
	}
	
	public Vector getReflection() {
		return reflection;
	}
	
	public Vector getRefraction() {
		return refraction;
	}
	
	public Double getRefDelta() {
		return refDelta;
	}
	
	
	public void setReflection(Vector r) {
		reflection = r.clamp(0, 10);
	}
	
	public abstract Vector getNormal(Vector p);

	public abstract double intersect(Ray r);



}
