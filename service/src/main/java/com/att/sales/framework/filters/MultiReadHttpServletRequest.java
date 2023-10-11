package com.att.sales.framework.filters;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

public class MultiReadHttpServletRequest extends HttpServletRequestWrapper {
	private final byte[] body;

	public MultiReadHttpServletRequest(HttpServletRequest request) throws IOException {
		super(request);
		InputStream inputStream = request.getInputStream();
		if (inputStream != null) {
			body = IOUtils.toByteArray(inputStream);
			return;
		}
		body = null;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (body == null) {
			return super.getInputStream();
		}
		final ByteArrayInputStream stream = new ByteArrayInputStream(body);
		return new ServletInputStream() {
			
			public int read() throws IOException {
				return stream.read();
			}

			
			public boolean isFinished() {
				return false;
			}

			
			public boolean isReady() {
				return false;
			}

			
			public void setReadListener(ReadListener listener) {
				// empty implementation
			}
		};
	}

	@Override
	public BufferedReader getReader() throws IOException {
		if (body == null) {
			return super.getReader();
		}
		BufferedReader br=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(body), StandardCharsets.UTF_8));
		return br;
	}

	public byte[] getBody() {
		return body;
	}
}