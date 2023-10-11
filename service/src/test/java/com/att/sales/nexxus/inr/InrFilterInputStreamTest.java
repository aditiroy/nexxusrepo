package com.att.sales.nexxus.inr;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InrFilterInputStreamTest {
	@Mock
	private InputStream in;
	
	@Test
	public void readTest() throws IOException {
		when(in.read()).thenReturn((int)'&', (int)'#', (int)'2', (int)'6', (int)'a', (int)'&', (int)'#', (int)'2', (int)'6', (int)';', (int)'a');
		InrFilterInputStream inrFilterInputStream = new InrFilterInputStream(in);
		byte[] b = new byte[11];
		inrFilterInputStream.read(b, 0, 11);
		
		when(in.read()).thenReturn(-1);
		inrFilterInputStream.read(b, 0, 11);
		
		when(in.read()).thenReturn((int)'&', -1);
		inrFilterInputStream.read(b, 0, 11);
		inrFilterInputStream.close();
	}
}
