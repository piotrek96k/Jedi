package com.piotrek.model;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import com.piotrek.exception.NameException;

public class OrderTest {

	@Test
	public void addOrderTest1() {
		try {
			Order.addOrder(null);
		} catch (NameException e) {
		} catch (SQLException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void addOrderTest2() {
		try {
			Order.addOrder("");
		} catch (NameException e) {
		} catch (SQLException e) {
			Assert.fail();
		}
	}

}
