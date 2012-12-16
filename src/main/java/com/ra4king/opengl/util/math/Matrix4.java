package com.ra4king.opengl.util.math;

import java.nio.FloatBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;

public class Matrix4 {
	private float[] matrix;
	
	public Matrix4() {
		matrix = new float[16];
	}
	
	public Matrix4(float[] m) {
		this();
		put(m);
	}
	
	public Matrix4(Matrix4 m) {
		this();
		put(m);
	}
	
	public Matrix4 clear() {
		Arrays.fill(matrix, 0);
		return this;
	}
	
	public Matrix4 clearToIdentity() {
		return clear().put(0, 1).put(5, 1).put(10, 1).put(15, 1);
	}
	
	public Matrix4 clearToOrtho(float left, float right, float bottom, float top, float near, float far) {
		return clear().put(0, 2 / (right - left))
				.put(5, 2 / (top - bottom))
				.put(10, -2 / (far - near))
				.put(12, -(right + left) / (right - left))
				.put(13, -(top + bottom) / (top - bottom))
				.put(14, -(far + near) / (far - near))
				.put(15, 1);
	}
	
	public Matrix4 clearToPerspective(float fovRad, float width, float height, float near, float far) {
		float fov = 1 / (float)Math.tan(fovRad / 2);
		return clear().put(0, fov / (width / height))
				.put(5, fov).put(10, (far + near) / (near - far))
				.put(14, (2 * far * near) / (near - far))
				.put(11, -1);
	}
	
	public Matrix4 clearToPerspectiveDeg(float fov, float width, float height, float near, float far) {
		return clearToPerspective((float)Math.toRadians(fov), width, height, near, far);
	}
	
	public float get(int index) {
		return matrix[index];
	}
	
	public Matrix4 put(int index, float f) {
		matrix[index] = f;
		return this;
	}
	
	public Matrix4 put(int index, Vector4 v) {
		put(index * 4 + 0, v.x());
		put(index * 4 + 1, v.y());
		put(index * 4 + 2, v.z());
		put(index * 4 + 3, v.z());
		return this;
	}
	
	public Matrix4 put(int index, Vector3 v) {
		put(index * 4 + 0, v.x());
		put(index * 4 + 1, v.y());
		put(index * 4 + 2, v.z());
		return this;
	}
	
	public Matrix4 put(int index, Vector3 v, float w) {
		put(index * 4 + 0, v.x());
		put(index * 4 + 1, v.y());
		put(index * 4 + 2, v.z());
		put(index * 4 + 3, w);
		return this;
	}
	
	public Matrix4 put(float[] m) {
		if(m.length < matrix.length)
			throw new IllegalArgumentException("float array must have at least " + matrix.length + " values.");
		
		System.arraycopy(m, 0, matrix, 0, matrix.length);
		
		return this;
	}
	
	public Matrix4 put(Matrix4 m) {
		return put(m.matrix);
	}
	
	public Matrix4 mult(float f) {
		for(int a = 0; a < matrix.length; a++)
			put(a, get(a) * f);
		
		return this;
	}
	
	public Matrix4 mult(float[] m) {
		float[] newm = new float[matrix.length];
		
		for(int a = 0; a < matrix.length; a += 4) {
			newm[a + 0] = get(0) * m[a] + get(4) * m[a + 1] + get(8) * m[a + 2] + get(12) * m[a + 3];
			newm[a + 1] = get(1) * m[a] + get(5) * m[a + 1] + get(9) * m[a + 2] + get(13) * m[a + 3];
			newm[a + 2] = get(2) * m[a] + get(6) * m[a + 1] + get(10) * m[a + 2] + get(14) * m[a + 3];
			newm[a + 3] = get(3) * m[a] + get(7) * m[a + 1] + get(11) * m[a + 2] + get(15) * m[a + 3];
		}
		
		put(newm);
		
		return this;
	}
	
	public Matrix4 mult(Matrix4 m) {
		return mult(m.matrix);
	}
	
	public Vector4 mult(Vector4 vec) {
		Vector4 v = new Vector4();
		
		v.x(get(0) * vec.x() + get(4) * vec.y() + get(8) * vec.z() + get(12) * vec.w());
		v.y(get(1) * vec.x() + get(5) * vec.y() + get(9) * vec.z() + get(13) * vec.w());
		v.z(get(2) * vec.x() + get(6) * vec.y() + get(10) * vec.z() + get(14) * vec.w());
		v.w(get(3) * vec.x() + get(7) * vec.y() + get(11) * vec.z() + get(15) * vec.w());
		
		return v;
	}
	
	public Matrix4 transpose() {
		float old = get(1);
		put(1, get(4));
		put(4, old);
		
		old = get(2);
		put(2, get(8));
		put(8, old);
		
		old = get(3);
		put(3, get(12));
		put(12, old);
		
		old = get(7);
		put(7, get(13));
		put(13, old);
		
		old = get(11);
		put(11, get(14));
		put(14, old);
		
		old = get(6);
		put(6, get(9));
		put(9, old);
		
		return this;
	}
	
	public Matrix4 translate(float x, float y, float z) {
		float[] m = new float[matrix.length];
		
		m[0] = 1;
		m[5] = 1;
		m[10] = 1;
		m[15] = 1;
		
		m[12] = x;
		m[13] = y;
		m[14] = z;
		
		return mult(m);
	}
	
	public Matrix4 translate(Vector3 vec) {
		return translate(vec.x(), vec.y(), vec.z());
	}
	
	public Matrix4 scale(float x, float y, float z) {
		float[] m = new float[matrix.length];
		
		m[0] = x;
		m[5] = y;
		m[10] = z;
		m[15] = 1;
		
		return mult(m);
	}
	
	public Matrix4 scale(Vector3 vec) {
		return scale(vec.x(), vec.y(), vec.z());
	}
	
	public Matrix4 rotate(float angle, float x, float y, float z) {
		float cos = (float)Math.cos(angle);
		float sin = (float)Math.sin(angle);
		float invCos = 1 - cos;
		
		Vector3 v = new Vector3(x, y, z).normalize();
		
		float[] m = new float[matrix.length];
		m[0] = v.x() * v.x() + (1 - v.x() * v.x()) * cos;
		m[4] = v.x() * v.y() * invCos - v.z() * sin;
		m[8] = v.x() * v.z() * invCos + v.y() * sin;
		
		m[1] = v.y() * v.x() * invCos + v.z() * sin;
		m[5] = v.y() * v.y() + (1 - v.y() * v.y()) * cos;
		m[9] = v.y() * v.z() * invCos - v.x() * sin;
		
		m[2] = v.z() * v.x() * invCos - v.y() * sin;
		m[6] = v.z() * v.y() * invCos + v.x() * sin;
		m[10] = v.z() * v.z() + (1 - v.z() * v.z()) * cos;
		
		m[15] = 1;
		
		return mult(m);
	}
	
	public Matrix4 rotate(float angle, Vector3 vec) {
		return rotate(angle, vec.x(), vec.y(), vec.z());
	}
	
	public Matrix4 rotateDeg(float angle, float x, float y, float z) {
		return rotate((float)Math.toRadians(angle), x, y, z);
	}
	
	public Matrix4 rotateDeg(float angle, Vector3 vec) {
		return rotate((float)Math.toRadians(angle), vec);
	}
	
	public float determinant() {
		float a = get(5) * get(10) * get(15) + get(9) * get(14) * get(7) + get(13) * get(6) * get(11) - get(7) * get(10) * get(13) - get(11) * get(14) * get(5) - get(15) * get(6) * get(9);
		float b = get(1) * get(10) * get(15) + get(9) * get(14) * get(3) + get(13) * get(2) * get(11) - get(3) * get(10) * get(13) - get(11) * get(14) * get(1) - get(15) * get(2) * get(9);
		float c = get(1) * get(6) * get(15) + get(5) * get(14) * get(3) + get(13) * get(2) * get(7) - get(3) * get(6) * get(13) - get(7) * get(14) * get(1) - get(15) * get(2) * get(5);
		float d = get(1) * get(6) * get(11) + get(5) * get(10) * get(3) + get(9) * get(2) * get(7) - get(3) * get(6) * get(9) - get(7) * get(10) * get(1) - get(11) * get(2) * get(5);
		
		return get(0) * a - get(4) * b + get(8) * c - get(12) * d;
	}
	
	public Matrix4 inverse() {
		Matrix4 inv = new Matrix4();
		
		inv.put(0, +(get(5) * get(10) * get(15) + get(9) * get(14) * get(7) + get(13) * get(6) * get(11) - get(7) * get(10) * get(13) - get(11) * get(14) * get(5) - get(15) * get(6) * get(9)));
		inv.put(1, -(get(4) * get(10) * get(15) + get(8) * get(14) * get(7) + get(12) * get(6) * get(11) - get(7) * get(10) * get(12) - get(11) * get(14) * get(4) - get(15) * get(6) * get(8)));
		inv.put(2, +(get(4) * get(9) * get(15) + get(8) * get(13) * get(7) + get(12) * get(5) * get(11) - get(7) * get(9) * get(12) - get(11) * get(13) * get(4) - get(15) * get(5) * get(8)));
		inv.put(3, -(get(4) * get(9) * get(14) + get(8) * get(13) * get(6) + get(12) * get(5) * get(10) - get(6) * get(9) * get(12) - get(10) * get(13) * get(4) - get(14) * get(5) * get(8)));
		
		inv.put(4, -(get(1) * get(10) * get(15) + get(9) * get(14) * get(3) + get(13) * get(2) * get(11) - get(3) * get(10) * get(13) - get(11) * get(14) * get(1) - get(15) * get(2) * get(9)));
		inv.put(5, +(get(0) * get(10) * get(15) + get(8) * get(14) * get(3) + get(12) * get(2) * get(11) - get(3) * get(10) * get(12) - get(11) * get(14) * get(0) - get(15) * get(2) * get(8)));
		inv.put(6, -(get(0) * get(9) * get(15) + get(8) * get(13) * get(3) + get(12) * get(1) * get(11) - get(3) * get(9) * get(12) - get(11) * get(13) * get(0) - get(15) * get(1) * get(8)));
		inv.put(7, +(get(0) * get(9) * get(14) + get(8) * get(13) * get(2) + get(12) * get(1) * get(10) - get(2) * get(9) * get(12) - get(10) * get(13) * get(0) - get(14) * get(1) * get(8)));
		
		inv.put(8, +(get(1) * get(6) * get(15) + get(5) * get(14) * get(3) + get(13) * get(2) * get(7) - get(3) * get(6) * get(13) - get(7) * get(14) * get(1) - get(15) * get(2) * get(5)));
		inv.put(9, -(get(0) * get(6) * get(15) + get(4) * get(14) * get(3) + get(12) * get(2) * get(7) - get(3) * get(6) * get(12) - get(7) * get(14) * get(0) - get(15) * get(2) * get(4)));
		inv.put(10, +(get(0) * get(5) * get(15) + get(4) * get(13) * get(3) + get(12) * get(1) * get(7) - get(3) * get(5) * get(12) - get(7) * get(13) * get(0) - get(15) * get(1) * get(4)));
		inv.put(11, -(get(0) * get(5) * get(14) + get(4) * get(13) * get(2) + get(12) * get(1) * get(6) - get(2) * get(5) * get(12) - get(6) * get(13) * get(0) - get(14) * get(1) * get(4)));
		
		inv.put(12, -(get(1) * get(6) * get(11) + get(5) * get(10) * get(3) + get(9) * get(2) * get(7) - get(3) * get(6) * get(9) - get(7) * get(10) * get(1) - get(11) * get(2) * get(5)));
		inv.put(13, +(get(0) * get(6) * get(11) + get(4) * get(10) * get(3) + get(8) * get(2) * get(7) - get(3) * get(6) * get(8) - get(7) * get(10) * get(0) - get(11) * get(2) * get(4)));
		inv.put(14, -(get(0) * get(5) * get(11) + get(4) * get(9) * get(3) + get(8) * get(1) * get(7) - get(3) * get(5) * get(8) - get(7) * get(9) * get(0) - get(11) * get(1) * get(4)));
		inv.put(15, +(get(0) * get(5) * get(10) + get(4) * get(9) * get(2) + get(8) * get(1) * get(6) - get(2) * get(5) * get(8) - get(6) * get(9) * get(0) - get(10) * get(1) * get(4)));
		
		return put(inv.transpose().mult(1 / determinant()));
	}
	
	public Quaternion toQuaternion() {
		float x = get(0) - get(5) - get(10);
		float y = get(5) - get(0) - get(10);
		float z = get(10) - get(0) - get(5);
		float w = get(0) + get(5) + get(10);
		
		int biggestIndex = 0;
		float biggest = w;
		
		if(x > biggest) {
			biggest = x;
			biggestIndex = 1;
		}
		
		if(y > biggest) {
			biggest = y;
			biggestIndex = 2;
		}
		
		if(z > biggest) {
			biggest = z;
			biggestIndex = 3;
		}
		
		float biggestVal = (float)(Math.sqrt(biggest + 1) * 0.5);
		float mult = 0.25f / biggestVal;
		
		Quaternion res = new Quaternion();
		
		switch(biggestIndex) {
			case 0:
				res.w(biggestVal);
				res.x((get(6) - get(9)) * mult);
				res.y((get(8) - get(2)) * mult);
				res.z((get(1) - get(4)) * mult);
				break;
			case 1:
				res.w((get(6) - get(9)) * mult);
				res.x(biggestVal);
				res.y((get(1) - get(4)) * mult);
				res.z((get(8) - get(2)) * mult);
				break;
			case 2:
				res.w((get(8) - get(2)) * mult);
				res.x((get(1) - get(4)) * mult);
				res.y(biggestVal);
				res.z((get(6) - get(9)) * mult);
				break;
			case 3:
				res.w((get(1) - get(4)) * mult);
				res.x((get(8) - get(2)) * mult);
				res.y((get(6) - get(9)) * mult);
				res.z(biggestVal);
				break;
		}
		
		return res;
	}
	
	private final static FloatBuffer direct = BufferUtils.createFloatBuffer(16);
	
	public FloatBuffer toBuffer() {
		direct.clear();
		direct.put(matrix);
		direct.flip();
		return direct;
	}
}
