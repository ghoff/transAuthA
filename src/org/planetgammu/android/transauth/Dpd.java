package org.planetgammu.android.transauth;

public class Dpd {
	public static int[] unpack(byte[] message) throws Exception {
		/* this is not general purpose, it only works with bit length is off by 4 */
		int extralen, i, length;
		int[] tmp, output;
		length = message.length * 8;
		extralen = length % 10;
		if (extralen != 4)
			throw new Exception("can only process special length DPD");
		tmp = dpd2Bcd((message[0] >>> 4));
		// only keep output[2]
		output = new int[1]; output[0] = tmp[2];
		i = extralen;
		length = message.length * 8;
		for(;i<length;i+=10) {
			int index, in, maskh, maskl, top, bottom, keeph, keepl;
			index = i/8;
			keeph = 8-(i%8);
			keepl = 10 - keeph;
			maskh = (int)Math.pow(2, keeph)-1;
			maskl = (int)Math.pow(2, keepl)-1;
			top = message[index] & maskh;
			bottom = message[index+1] >> (8-keepl);
			in = top << (10-keeph) | bottom & maskl;
			output=intArrayConcat(output,dpd2Bcd(in));
		}
		return(output);
	}
	private static int[] intArrayConcat(int[] input1, int[] input2) {
		int[] output = new int[input1.length + input2.length];
		System.arraycopy(input1, 0, output, 0, input1.length);
		System.arraycopy(input2, 0, output, input1.length, input2.length);
		return(output);
	}
	private static int[] dpd2Bcd(int input) {
		int a,b,c,d,e,f,g,h,i,j,k,m;
		int p,q,r,s,t,u,v,w,x,y;
		int[] output = new int[3];

		p = (input>>9)&1;
		q = (input>>8)&1;
		r = (input>>7)&1;
		s = (input>>6)&1;
		t = (input>>5)&1;
		u = (input>>4)&1;
		v = (input>>3)&1;
		w = (input>>2)&1;
		x = (input>>1)&1;
		y = (input>>0)&1;

		a = (v & w) & ((s^1) | t | (x^1));
		b = p & ((v^1) | (w^1) | (s & (t^1) & x));
		c = q & ((v^1) | (w^1) | (s & (t^1) & x));
		d = r;
		e = v & (((w^1) & x) | ((t^1) & x) | (s & x));
		f = (s & ((v^1) | (x^1))) | (p & (s^1) & t & v & w & x);
		g = (t & ((v^1) | (x^1))) | (q & (s^1) & t & w);
		h = u;
		i = v & (((w^1) & (x^1)) | (w & x & (s | t)));
		j = ((v^1) & w) | (s & v & (w^1) & x) | (p & w & ((x^1) | ((s^1) & (t^1))));
		k = ((v^1) & x) | (t & (w^1) & x) | (q & v & w & ((x^1) | ((s^1) & (t^1))));
		m = y;

		output[0] = a<<3|b<<2|c<<1|d;
		output[1] = e<<3|f<<2|g<<1|h;
		output[2] = i<<3|j<<2|k<<1|m;

		return(output);
	}
}
