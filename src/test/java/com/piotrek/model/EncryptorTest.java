package com.piotrek.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class EncryptorTest {

	private String field;
	
	public EncryptorTest(String field ) {
		this.field = field;
	}

	@Test
	public void encryptTest() {
		Assert.assertNotEquals(field, Encryptor.encrypt(field));
	}
	
	@Test
	public void decryptTest() {
		Assert.assertEquals(field, Encryptor.decrypt(Encryptor.encrypt(field)));
	}

	@Parameterized.Parameters
	public static Collection<Object> getData() {
		Object[] data = new Object[1000];
		String characters = getCharacters();
		Random random = new Random();
		int length = random.nextInt(characters.length() / 2);
		for (int i = 0; i < data.length; i++) {
			StringBuilder builder = new StringBuilder();
			for (int j = 0; j < length; j++) {
				int index = random.nextInt(characters.length());
				builder.append(characters.charAt(index));
			}
			data[i] = builder.toString();
		}
		return Arrays.asList(data);
	}

	private static String getCharacters() {
		StringBuilder builder = new StringBuilder();
		char character = 'a';
		while (character != 'z' + 1)
			builder.append(character++);
		character = 'A';
		while (character != 'Z' + 1) 
			builder.append(character++);
		character = '0';
		while (character != '9' + 1)
			builder.append(character++);
		builder.append("!@#$%^&*()_+=-/*\n\\<>,.?|");
		return builder.toString();
	}

}
