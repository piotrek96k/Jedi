package com.piotrek.model;

public enum LightsaberColor {

	RED("Czerwony"), GREEN("Zielony"), BLUE("Niebieski"), PURPLE("Fioletowy");

	private String color;

	private LightsaberColor(String color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return color;
	}

}
