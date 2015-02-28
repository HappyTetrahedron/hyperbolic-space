package assignment2;

public class Vector {

	public static final Vector VEC_ZERO = new Vector(0, 0, 0);
	private final double x, y, z;
	
	public Vector() {
		this(0, 0, 0);
	}
	
	public Vector(double d) {
		this(d,d,d);
	}

	public Vector(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}
    
	// TODO: implement these vector functions!
	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}
	
	//Vector operations
	public Vector cross(Vector o) {
		return new Vector(y*o.getZ() - z*o.getY(), z*o.getX() - x*o.getZ(), x*o.getY() - y*o.getX());
	}

	public double dot(Vector o) {
		return x * o.getX() + y * o.getY() + z * o.getZ();
	}

	public Vector minus(Vector o) {
		return new Vector(x - o.getX(), y - o.getY(), z - o.getZ());
	}

	public Vector mul(Vector o) {
		return new Vector(x*o.getX(), y*o.getY(), z*o.getZ());
		//What is this?
		
	}

	public double norm() {
		return Math.sqrt(x*x + y*y + z*z);
	}

	public Vector normalize() {
		double norm = norm();
		if (norm == 0) return VEC_ZERO.copy();
		return new Vector(x/norm, y/norm, z/norm);
	}

	public Vector plus(Vector o) {
		return new Vector(x + o.getX(), y + o.getY(), z + o.getZ());
	}

	public Vector smult(double f) {
		return new Vector(x*f, y*f, z*f);
	}
	
	public Vector copy() {
		return new Vector(x, y, z);
	}
	
	public boolean isNotZero() {
		return (x != 0 || y != 0 || z != 0);
	}
	

	// Some helper function to produce a color from a Vector
	// You don't have to change those!
	public Vector clamp(double min, double max) {
		return new Vector(clamp(x, min, max), clamp(y, min, max), clamp(z, min,
				max));
	}
	
	private static double clamp(double value, double min, double max) {
		double d = value;
		if (d < min) {
			d = min;
		} else if (d > max) {
			d = max;
		}
		return d;
	}
	
	public int toRGB() {
		return ((int) (Math.pow(clamp(x, 0, 1), 1 / 1) * 255. + .5) << 16)
				| ((int) (Math.pow(clamp(y, 0, 1), 1 / 1) * 255. + .5) << 8)
				| ((int) (Math.pow(clamp(z, 0, 1), 1 / 1) * 255. + .5));
	}

	@Override
	public String toString() {
		return "Vector [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

}
