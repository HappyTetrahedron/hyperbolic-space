package assignment2;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class VectorTest {
	private final Random r = new Random();
	private final static double EPS = 1e-4;

	@Test
	public void testNew() {
		Vector v = new Vector(1, 3, 4);
		Assert.assertEquals(1, v.getX(), 0);
		Assert.assertEquals(3, v.getY(), 0);
		Assert.assertEquals(4, v.getZ(), 0);
	}

	private double[] randomDoubles(int length) {
		double[] d = new double[length];
		for (int i = 0; i < length; i++) {
			d[i] = r.nextDouble();
		}
		return d;
	}

	private Vector vectorInit(double[] v, int offset) {
		return new Vector(v[0 + offset], v[1 + offset], v[2 + offset]);
	}

	private void assertUnchanged(Vector v, double vs[], int offset) {
		Assert.assertEquals(vs[0 + offset], v.getX(), EPS);
		Assert.assertEquals(vs[1 + offset], v.getY(), EPS);
		Assert.assertEquals(vs[2 + offset], v.getZ(), EPS);
	}

	@Test
	public void testPlus() {
		double[] vs = randomDoubles(6);
		Vector a = vectorInit(vs, 0);
		Vector b = vectorInit(vs, 3);
		Vector p = a.plus(b);
		assertUnchanged(a, vs, 0);
		assertUnchanged(b, vs, 3);
		Assert.assertEquals(vs[0] + vs[3], p.getX(), EPS);
		Assert.assertEquals(vs[1] + vs[4], p.getY(), EPS);
		Assert.assertEquals(vs[2] + vs[5], p.getZ(), EPS);
	}

	@Test
	public void testMinus() {
		double[] vs = randomDoubles(6);
		Vector a = vectorInit(vs, 0);
		Vector b = vectorInit(vs, 3);
		Vector p = a.minus(b);
		assertUnchanged(a, vs, 0);
		assertUnchanged(b, vs, 3);
		Assert.assertEquals(vs[0] - vs[3], p.getX(), EPS);
		Assert.assertEquals(vs[1] - vs[4], p.getY(), EPS);
		Assert.assertEquals(vs[2] - vs[5], p.getZ(), EPS);
	}

	@Test
	public void testMul() {
		double[] vs = randomDoubles(6);
		Vector a = vectorInit(vs, 0);
		Vector b = vectorInit(vs, 3);
		Vector p = a.mul(b);
		assertUnchanged(a, vs, 0);
		assertUnchanged(b, vs, 3);
		Assert.assertEquals(vs[0] * vs[3], p.getX(), EPS);
		Assert.assertEquals(vs[1] * vs[4], p.getY(), EPS);
		Assert.assertEquals(vs[2] * vs[5], p.getZ(), EPS);
	}

	@Test
	public void testSmult() {
		double[] vs = randomDoubles(4);
		Vector a = vectorInit(vs, 0);
		Vector p = a.smult(vs[3]);
		assertUnchanged(a, vs, 0);
		Assert.assertEquals(vs[0] * vs[3], p.getX(), EPS);
		Assert.assertEquals(vs[1] * vs[3], p.getY(), EPS);
		Assert.assertEquals(vs[2] * vs[3], p.getZ(), EPS);
	}

	@Test
	public void testNorm() {
		double[] v = randomDoubles(3);
		Vector a = vectorInit(v, 0);
		double p = a.norm();
		assertUnchanged(a, v, 0);
		final double norm = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
		Assert.assertEquals(norm, p, EPS);
	}

	@Test
	public void testNormalize() {
		double[] v = randomDoubles(3);
		Vector a = vectorInit(v, 0);
		Vector p = a.normalize();
		assertUnchanged(a, v, 0);
		final double norm = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
		Assert.assertEquals(v[0] / norm, p.getX(), EPS);
		Assert.assertEquals(v[1] / norm, p.getY(), EPS);
		Assert.assertEquals(v[2] / norm, p.getZ(), EPS);
	}

	@Test
	public void testCross() {
		double[] v = randomDoubles(6);
		Vector a = vectorInit(v, 0);
		Vector b = vectorInit(v, 3);
		Vector p = a.cross(b);
		assertUnchanged(a, v, 0);
		assertUnchanged(b, v, 3);
		Assert.assertEquals(v[1] * v[5] - v[2] * v[4], p.getX(), EPS);
		Assert.assertEquals(v[2] * v[3] - v[0] * v[5], p.getY(), EPS);
		Assert.assertEquals(v[0] * v[4] - v[1] * v[3], p.getZ(), EPS);
	}

	@Test
	public void testDot() {
		double[] v = randomDoubles(6);
		Vector a = vectorInit(v, 0);
		Vector b = vectorInit(v, 3);
		double p = a.dot(b);
		assertUnchanged(a, v, 0);
		assertUnchanged(b, v, 3);
		Assert.assertEquals(v[0] * v[3] + v[1] * v[4] + v[2] * v[5], p, EPS);
	}

}
