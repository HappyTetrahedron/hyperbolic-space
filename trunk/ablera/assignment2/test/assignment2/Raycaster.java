package assignment2;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

public class Raycaster {
	
	private final static int MAX_RECURSION_DEPTH = 100;
	private final static int DIFFUSE_DEPTH = 20;
	private final static double EPS = 1e-5;
	
	private Shape[] shapes = new Shape[4];
	private Light[] lights = new Light[2];
	
	private Vector ambientCol = new Vector(1,1,1);
	
	public static void main(String[] args) {
		new Raycaster().testRayCaster();
	}
	
	

	@Test
	public void testRayCaster() {
		final int width = 512;
		final int height = 512;
		

		shapes[0] = new Sphere(20, new Vector(0, 18, -200), new Vector(0.75, 0.75, 0.75), new Vector(), new Vector(), new Vector(0.7), 100, new Vector(), new Vector(0.8,1,0.9), 0.2);
//		shapes[1] = new Wall(new Vector(0, 0, -250), new Vector(-1.25,0.5,1), new Vector(0.25, 0.25, 0.25),0,0,0,0,1);
//		shapes[2] = new Wall(new Vector(0, 0, -250), new Vector(1,0.5,1), new Vector(0.25, 0.25, 0.25),0,0,0,0,1);
		shapes[1] = new Sphere(7, new Vector(20, 37, -190), new Vector(0.15, 0.15, 0.15), new Vector(0.1), new Vector(0.2), new Vector(0.5), 100, new Vector(0.8,0.5,0.5));
		shapes[2] = new Wall(new Vector(0, 40, -200), new Vector(0, -2.25, 1.125), new Vector(0.6,0.6,0.6), new Vector(0.6), new Vector(0.5), new Vector(0.2), 50, new Vector(0.4));

		
		lights[0] = new SphereLight(20, new Vector(100, -30, -100), new Vector(0.7,0.7,0.7));
		lights[1] = new SphereLight(10, new Vector(100, -70, -270), new Vector(1,1,1), true);
//		lights[2] = new Light(new Vector(100, 2, 0), new Vector(1,1,1));
//		lights[3] = new Light(new Vector(100, 2, 0), new Vector(1,1,1));

		shapes[3] = (Shape)lights[1];

		final BufferedImage image = new BufferedImage(width, height,
				ColorSpace.TYPE_RGB);

		long nanos = System.nanoTime();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				 
				final Ray direction = new Ray(new Vector(0,0,0), new Vector(i-width/2, j-height/2, -500));

				image.setRGB(i, j, traceRay(direction, 1).toRGB());
			}
		}
		System.out.println("Duration [ms]: " + (System.nanoTime() - nanos) / 1000);

		try {
			ImageIO.write(image, "PNG", new File("raytrace.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	double[] intersect(Ray direction) {
		double[] dists = new double[shapes.length];
		
			for(int i=0; i < shapes.length; i++) {
				dists[i] = shapes[i].intersect(direction);
			}
		
		return dists;
	}
	
	boolean canSeeLight(Ray direction, double lightDist) {
		double dist = Double.NaN;
		
		for(int i=0; i < shapes.length; i++){
			if(!shapes[i].isLight){
				dist = shapes[i].intersect(direction);
				if(!Double.isNaN(dist)){
					if(dist < lightDist - EPS) return false;
				}
			}
		}
		return true;
	}
	

	
	// calculates the color a given ray sees. 
	Vector traceRay(Ray direction, int depth){
		
		if (depth > MAX_RECURSION_DEPTH) return new Vector(1,0,1);
		
		double[] dists = intersect(direction);
		
		double min = getmin(dists);

		if (Double.isNaN(min)) {
			// Missed ALL the Shapes
			return new Vector(0.5,0.5,0.5);
		}
		
		for(int i = 0; i < shapes.length; i++) {
			if(min == dists[i]) {
				Vector point = direction.getOrigin().plus(direction.getDirection().smult(dists[i]));
				return getSurfaceColor(point, direction.getDirection().smult(-1), shapes[i], direction.getRefraction(), depth);
			}
		}
		 
		//should not actually be reached.
		return new Vector(1, 0, 0);
	}
	
	
	// Calculates the color of a surface at a given point and from a given viewing direction.
	private Vector getSurfaceColor(Vector point, Vector viewer, Shape s, double refInd, int depth) {
		if(s.isLight) return s.getColor();
		
		Vector normal = s.getNormal(point).normalize();
		viewer = viewer.normalize();
		
		// AMBIENT
		Vector col = s.getColor().mul(ambientCol).mul(s.getAmbient());
		
		// REFLECTION
		if(s.getReflection().isNotZero()) {
			Vector dir = reflectVector(viewer, normal);
			col = col.plus(traceRay(new Ray(point.plus(dir.smult(EPS)), dir, refInd), depth+1).mul(s.getReflection()));
		}
		
		// REFRACTION
		if(s.getRefraction().isNotZero()) { 
			double newRefInd;
			
			if(normal.dot(viewer) >= 0) { // we're entering the medium
				newRefInd = refInd + s.getRefDelta();
			}
			else { //leaving the medium
				newRefInd = refInd - s.getRefDelta();
			}
			
			Vector dir = refractVector(viewer.smult(-1), normal, refInd, newRefInd);
			if ((dir.dot(normal) >= 0) == (viewer.dot(normal) >= 0)) { //if viewer and dir vectors are on the same side...
				newRefInd = refInd; //total reflection occurred, we do not change medium.
//				return new Vector(1,0,0);
			}
			
			col = col.plus(traceRay(new Ray(point.plus(dir.smult(0.03)), dir, newRefInd), depth+1).mul(s.getRefraction()));
			if (dir.getY() == 1) return dir;
		}
		
		//LIGHTS 
		for(int i=0; i < lights.length; i++){
			// non-shadow-casting lights always apply.
			if(!lights[i].isShadowCasting()) col = col.plus(getLitCol(s, lights[i], point, normal, viewer));
			
			else {
				Vector lightCol = new Vector();
				for(int j=0; j < DIFFUSE_DEPTH; j++){
					
					Vector lightSource = lights[i].getRandPosition().minus(point).normalize();
					
					Vector half = viewer.plus(lightSource);
					half = half.normalize();
					
					if (canSeeLight(new Ray(point.plus(lightSource.smult(EPS)), lightSource), lights[i].getDistance(point))) {
						
						lightCol = lightCol.plus(getLitCol(s, lights[i], point, normal, viewer));
					}
				
				}
				
				lightCol = lightCol.smult(1/(double)DIFFUSE_DEPTH);
				col = col.plus(lightCol);
			}
		}
		return col;
	}
	
	private Vector getLitCol(Shape s, Light l, Vector point, Vector normal, Vector viewer) {
		Vector col = new Vector();
		
		Vector lightSource = l.getPosition().minus(point).normalize();
		
		Vector half = viewer.plus(lightSource);
		half = half.normalize();
		
		// DIFFUSE (lambertian)
		col = col.plus((l.getColor().mul(s.getColor()).smult(Math.max(0,lightSource.dot(normal)))).mul(s.diffuse));
		
		// SPECULAR (blinn-phong)
		col = col.plus((l.getColor().mul(new Vector(1,1,1)).smult(Math.pow(Math.max(normal.dot(half),0), s.exponent))).mul(s.specular));
	
		return col;
	}
	
	private double getmin(double[] d) {
		double min = Double.NaN;
		for(int i=0; i < d.length; i++) {
			if (!Double.isNaN(d[i]) && (Double.isNaN(min) || d[i] <= min)) min = d[i];
		}
		return min;
		
	}
	
	//Reflects a vector by turning it 180 degrees around the "normal" axis
	private Vector reflectVector(Vector v, Vector normal){
		
		Vector vParallel = normal.smult(v.dot(normal));
		Vector vOrthogonal = v.minus(vParallel);
		
		return vParallel.plus(vOrthogonal.smult(-1));
		
	}
	
	
	//Returns the refracted vector when light travels from an n1 medium to an n2 medium
	private Vector refractVector(Vector v, Vector normal, double n1, double n2) {
		v = v.normalize();
		normal = normal.normalize();
		
		Vector vOrthogonal = v.minus(normal.smult(v.dot(normal)));
		
		Vector rOrthogonal = vOrthogonal.smult(n1/n2);
		
		// total reflection
		if (rOrthogonal.norm() > 1) return reflectVector(v.smult(-1), normal);

		Vector rParallel = normal.smult(Math.signum(normal.dot(v)) * Math.sqrt(1 - rOrthogonal.norm()*rOrthogonal.norm()));
		
		Vector r = rParallel.plus(rOrthogonal);
		
		
		return r;
		
	}

}
