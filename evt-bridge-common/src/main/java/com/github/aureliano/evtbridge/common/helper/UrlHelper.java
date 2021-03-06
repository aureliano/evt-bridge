package com.github.aureliano.evtbridge.common.helper;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.Map;

public final class UrlHelper {

	public UrlHelper() {
		super();
	}
	
	private static final int RADIX = 16;
		
	private static final BitSet RESERVED = new BitSet(256);
	private static final BitSet URLENCODER = new BitSet(256);
	private static final BitSet UNRESERVED = new BitSet(256);
	private static final BitSet PUNCT = new BitSet(256);
	private static final BitSet USERINFO = new BitSet(256);
	private static final BitSet PATHSAFE = new BitSet(256);
	private static final BitSet URIC = new BitSet(256);

	static {
		for (int i = 'a'; i <= 'z'; i++) {
			UNRESERVED.set(i);
		}
		
		for (int i = 'A'; i <= 'Z'; i++) {
			UNRESERVED.set(i);
		}
		
		for (int i = '0'; i <= '9'; i++) {
			UNRESERVED.set(i);
		}

		UNRESERVED.set('_');
		UNRESERVED.set('-');
		UNRESERVED.set('.');
		UNRESERVED.set('*');
		URLENCODER.or(UNRESERVED);
		UNRESERVED.set('!');
		UNRESERVED.set('~');
		UNRESERVED.set('\'');
		UNRESERVED.set('(');
		UNRESERVED.set(')');
		
		PUNCT.set(',');
		PUNCT.set(';');
		PUNCT.set(':');
		PUNCT.set('$');
		PUNCT.set('&');
		PUNCT.set('+');
		PUNCT.set('=');
		
		USERINFO.or(UNRESERVED);
		USERINFO.or(PUNCT);
		
		PATHSAFE.or(UNRESERVED);
		PATHSAFE.set('/');
		PATHSAFE.set(';');
		PATHSAFE.set(':');
		PATHSAFE.set('@');
		PATHSAFE.set('&');
		PATHSAFE.set('=');
		PATHSAFE.set('+');
		PATHSAFE.set('$');
		PATHSAFE.set(',');
		
		RESERVED.set(';');
		RESERVED.set('/');
		RESERVED.set('?');
		RESERVED.set(':');
		RESERVED.set('@');
		RESERVED.set('&');
		RESERVED.set('=');
		RESERVED.set('+');
		RESERVED.set('$');
		RESERVED.set(',');
		RESERVED.set('[');
		RESERVED.set(']');
		
		URIC.or(RESERVED);
		URIC.or(UNRESERVED);
	}
	
	public static String formatParameters(final Map<String, String> parameters) {
		final StringBuilder result = new StringBuilder();
		
		for (String key : parameters.keySet()) {
			String encodedName = encodeFormFields(key);
			String encodedValue = encodeFormFields(parameters.get(key));
			
			if (result.length() > 0) {
				result.append('&');
			}
			
			result.append(encodedName);
			if (encodedValue != null) {
				result.append('=');
				result.append(encodedValue);
			}
		}
		
		return result.toString();
	}
	
	private static String encodeFormFields (final String content) {
		if (content == null) {
			return null;
		}
		
		return urlEncode(content, URLENCODER, true);
	}
	
	private static String urlEncode(final String content, final BitSet safechars, final boolean blankAsPlus) {
		Charset charset = Charset.forName("UTF-8");
		if (content == null) {
			return null;
		}
		
		final StringBuilder builder = new StringBuilder();
		final ByteBuffer bb = charset.encode(content);
		
		while (bb.hasRemaining()) {
			final int b = bb.get() & 0xff;
			
			if (safechars.get(b)) {
				builder.append((char) b);
			} else if (blankAsPlus && b == ' ') {
				builder.append('+');
			} else {
				builder.append("%");
				final char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, RADIX));
				final char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, RADIX));
				
				builder.append(hex1);
				builder.append(hex2);
			}
		}
		
		return builder.toString();
	}
}