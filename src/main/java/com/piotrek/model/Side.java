package com.piotrek.model;

public enum Side {

	DARK("Ciemna"), LIGHT("Jasna");

	private String side;

	private Side(String side) {
		this.side = side;
	}

	@Override
	public String toString() {
		return side;
	}

}
