package com.piotrek.model;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import com.piotrek.exception.NameException;
import com.piotrek.exception.ValueException;

public class JediTest {

	@Test
	public void addJediTest1() {
		try {
			Jedi.addJedi(null, LightsaberColor.BLUE, 500, Side.DARK);
		} catch (NameException e) {
		} catch (SQLException | ValueException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void addJediTest2() {
		try {
			Jedi.addJedi("", LightsaberColor.BLUE, 500, Side.DARK);
		} catch (NameException e) {
		} catch (SQLException | ValueException e) {
			Assert.fail();
		}
	}
	

	@Test
	public void addJediTest3() {
		try {
			Jedi.addJedi("Name", null, 500, Side.DARK);
		} catch (NameException | SQLException e) {
			Assert.fail();
		} catch (ValueException e) {
		}
	}
	
	@Test
	public void addJediTest4() {
		try {
			Jedi.addJedi("Name", LightsaberColor.GREEN, -7, Side.DARK);
		} catch (NameException | SQLException e) {
			Assert.fail();
		} catch (ValueException e) {
		}
	}
	
	@Test
	public void addJediTest5() {
		try {
			Jedi.addJedi("Name", LightsaberColor.GREEN, 0, null);
		} catch (NameException | SQLException e) {
			Assert.fail();
		} catch (ValueException e) {
		}
	}

}
