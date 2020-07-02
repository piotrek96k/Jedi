package com.piotrek.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Encryptor {

	private static final int DISPLACEMENT = 20;

	private Encryptor() {
	}

	public static String decrypt(String input) {
		if (input == null || input.isEmpty())
			return input;
		String characters = getCharacters();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			for (int j = 0; j < characters.length(); j++)
				if (input.charAt(i) == characters.charAt(j)) {
					if (j - DISPLACEMENT < 0)
						j += characters.length();
					j -= DISPLACEMENT;
					builder.append(characters.charAt(j));
					j = characters.length();
				}
		}
		return builder.toString();
	}

	public static String encrypt(String input) {
		if (input == null || input.isEmpty())
			return input;
		String characters = getCharacters();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			for (int j = 0; j < characters.length(); j++)
				if (input.charAt(i) == characters.charAt(j)) {
					if (j + DISPLACEMENT >= characters.length())
						j -= characters.length();
					j += DISPLACEMENT;
					builder.append(characters.charAt(j));
					j = characters.length();
				}
		}
		return builder.toString();
	}

	private static String getCharacters() {
		String path = System.getProperty("java.class.path") + "/characters/Characters.txt";
		File file = new File(path);
		StringBuilder builder;
		if (file.exists())
			builder = readCharacters(file);
		else {
			file = new File(System.getProperty("user.dir") + "/src/main/resources/characters/Characters.txt");
			builder = createCharacters(file);
		}
		return builder.toString();
	}

	private static StringBuilder createCharacters(File file) {
		char character = 32;
		try {
			PrintWriter writer = new PrintWriter(file);
			while (character < 800)
				writer.print(character++);
			writer.close();
			Scanner scanner = new Scanner(file);
			String characters = scanner.nextLine();
			scanner.close();
			StringBuilder builder = new StringBuilder("?\n\t");
			for (int i = 0; i < characters.length(); i++) {
				if (characters.charAt(i) != '?')
					builder.append(characters.charAt(i));
			}
			writer = new PrintWriter(file);
			writer.print(builder);
			writer.close();
			return builder;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static StringBuilder readCharacters(File file) {
		try (Scanner scanner = new Scanner(file)) {
			StringBuilder builder = new StringBuilder();
			while (scanner.hasNextLine()) {
				builder.append(scanner.nextLine());
				builder.append('\n');
			}
			builder.deleteCharAt(builder.length() - 1);
			return builder;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

}
