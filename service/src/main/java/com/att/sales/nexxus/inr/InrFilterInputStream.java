package com.att.sales.nexxus.inr;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This customized InputStream is used to skip the special character &#26; which
 * will cause xml parsing exception
 * 
 * @author xy3208
 *
 */
public class InrFilterInputStream extends FilterInputStream {
	private int[] buffer = {-1, -1, -1, -1, -1};
	
	public InrFilterInputStream(InputStream in) {
		super(in);
	}
	
	@Override
	public int read() throws IOException {
		if (buffer[0] != -1) {
			int res = buffer[0];
			shiftBuffer();
			return res;
		}
		int b = in.read();
		if (b == '&') {
			buffer[0] = b;
			buffer[1] = in.read();
			buffer[2] = in.read();
			buffer[3] = in.read();
			buffer[4] = in.read();
			if (buffer[0] == '&' && buffer[1] == '#' && buffer[2] == '2' && buffer[3] == '6' && buffer[4] == ';') {
				clearBuffer();
			}
			return read();
		}
		return b;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		for (int i= 0; i < len; i++) {
			int r = read();
			if (r == -1) {
				if (i == 0) {
					return -1;
				} else {
					return i;
				}
			} else {
				b[off + i] = (byte) r;
			}
		}
		return len;
	}
	
	private void clearBuffer() {
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = -1;
		}
		
	}

	private void shiftBuffer() {
		for (int i = 1; i < buffer.length; i++) {
			buffer[i - 1] = buffer[i];
		}
		buffer[buffer.length - 1] = -1;
	}
	
	

}
