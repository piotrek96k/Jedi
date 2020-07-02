package com.piotrek.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.piotrek.exception.NameException;
import com.piotrek.exception.ValueException;
import com.piotrek.util.SqlUtil;

public class Jedi implements Serializable {

	private static final long serialVersionUID;

	public static final String JEDI_FILE_EXTENSION;

	public static final int MIN_POWER;

	public static final int MAX_POWER;

	private static List<Jedi> jedisList;

	private int id;

	private String name;

	private transient Order order;

	private int orderId;

	private LightsaberColor lightsaberColor;

	private int power;

	private Side side;

	static {
		serialVersionUID = 3240819541559872577L;
		JEDI_FILE_EXTENSION = ".jedi";
		MIN_POWER = 0;
		MAX_POWER = 1000;
		jedisList = new ArrayList<Jedi>();
	}

	private Jedi(String name, LightsaberColor lightsaberColor, int power, Side side)
			throws NameException, ValueException {
		setName(name);
		setPower(power);
		setLightsaberColor(lightsaberColor);
		setSide(side);
	}

	private void setSide(Side side) throws ValueException {
		if (side == null)
			throw new ValueException("Wrong value");
		this.side = side;
	}

	private void setLightsaberColor(LightsaberColor lightsaberColor) throws ValueException {
		if (lightsaberColor == null)
			throw new ValueException("Wrong value");
		this.lightsaberColor = lightsaberColor;
	}

	private void setPower(int power) throws ValueException {
		if (power < 0 || power > 1000)
			throw new ValueException("Wrong value");
		this.power = power;
	}

	private void setName(String name) throws NameException {
		if (name == null || name.isEmpty())
			throw new NameException("Wrong name");
		this.name = name;

	}

	public static void loadJedisFromDatabase() throws SQLException, NameException, ValueException {
		Order.loadOrdersFromDatabse();
		SqlUtil sqlUtil = new SqlUtil();
		ResultSet data = sqlUtil.executeQuery("SELECT * FROM JEDI ORDER BY ID ASC");
		while (data.next()) {
			String name = data.getString("NAME");
			String color = data.getString("LIGHTSABER_COLOR");
			LightsaberColor lightsaberColor = getLightsaberColor(color);
			int power = data.getInt("POWER");
			Side side = getSide(data.getString("SIDE"));
			Jedi jedi = new Jedi(name, lightsaberColor, power, side);
			jedi.setId(data.getInt("ID"));
			jedi.setOrderId(data.getInt("ORDER_ID"));
			jedisList.add(jedi);
		}
		sqlUtil.close();
		Order.bindOrdersWithJedi();
	}

	private static Side getSide(String side) {
		Side[] sides = Side.values();
		for (int i = 0; i < sides.length; i++)
			if (sides[i].toString().equals(side))
				return sides[i];
		return null;
	}

	private static LightsaberColor getLightsaberColor(String color) {
		LightsaberColor[] colors = LightsaberColor.values();
		for (int i = 0; i < colors.length; i++)
			if (colors[i].toString().equals(color))
				return colors[i];
		return null;
	}

	public static Jedi addJedi(String name, LightsaberColor lightsaberColor, int power, Side side)
			throws SQLException, NameException, ValueException {
		Jedi jedi = new Jedi(name, lightsaberColor, power, side);
		addJedi(jedi);
		jedisList.add(jedi);
		return jedi;
	}

	private static String getInsertJediQuery(Jedi jedi) {
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO JEDI (NAME, LIGHTSABER_COLOR, POWER, SIDE) VALUES ('");
		builder.append(jedi.name);
		builder.append("','");
		builder.append(jedi.lightsaberColor);
		builder.append("','");
		builder.append(jedi.power);
		builder.append("','");
		builder.append(jedi.side);
		builder.append("');");
		return builder.toString();
	}

	public static List<Jedi> getJedisList() {
		return Collections.unmodifiableList(jedisList);
	}

	public static Jedi getJedi(int id) {
		for (Jedi jedi : jedisList)
			if (jedi.id == id)
				return jedi;
		return null;
	}

	public static void saveInFile(File file) {
		if (file == null)
			return;
		if (!file.exists())
			file = new File(file.toString());
		try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file))) {
			stream.writeObject(jedisList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void addJedi(Jedi jedi) throws SQLException {
		SqlUtil sqlUtil = new SqlUtil();
		sqlUtil.execute(getInsertJediQuery(jedi));
		ResultSet data = sqlUtil.executeQuery("SELECT MAX(ID) AS MAX FROM JEDI");
		data.next();
		jedi.setId(data.getInt("MAX"));
		sqlUtil.close();
	}

	private static void addJediIfNotExists(Jedi jedi) throws SQLException {
		if (getJedi(jedi.id) != null)
			return;
		addJedi(jedi);
		jedisList.add(jedi);
		Order order = Order.getOrder(jedi.orderId);
		if (order != null)
			jedi.addOrder(order);
	}

	public static void readFromFile(File file) throws SQLException {
		if (file == null || !file.exists())
			return;
		if (!file.toString().endsWith(JEDI_FILE_EXTENSION))
			return;
		try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file))) {
			Object object = stream.readObject();
			if (object instanceof List<?>) {
				List<?> list = (List<?>) object;
				for (Object obj : list)
					if (obj instanceof Jedi) {
						Jedi jedi = (Jedi) obj;
						addJediIfNotExists(jedi);
					}
			}
			Order.bindOrdersWithJedi();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void addOrder(Order order) throws SQLException {
		SqlUtil sqlUtil = new SqlUtil();
		sqlUtil.execute(String.format("UPDATE JEDI SET ORDER_ID=%d WHERE ID=%d;", order.getId(), id));
		sqlUtil.close();
		setOrderId(order.getId());
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.writeObject(Encryptor.encrypt(Integer.toString(id)));
		stream.writeObject(Encryptor.encrypt(name));
		stream.writeObject(Encryptor.encrypt(lightsaberColor.name()));
		stream.writeObject(Encryptor.encrypt(Integer.toString(power)));
		stream.writeObject(Encryptor.encrypt(side.name()));
		stream.writeObject(Encryptor.encrypt(Integer.toString(orderId)));
	}

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		id = Integer.parseInt(Encryptor.decrypt((String) stream.readObject()));
		name = Encryptor.decrypt((String) stream.readObject());
		lightsaberColor = LightsaberColor.valueOf(Encryptor.decrypt((String) stream.readObject()));
		power = Integer.parseInt(Encryptor.decrypt((String) stream.readObject()));
		side = Side.valueOf(Encryptor.decrypt((String) stream.readObject()));
		orderId = Integer.parseInt(Encryptor.decrypt((String) stream.readObject()));
	}

	private void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getOrderId() {
		return orderId;
	}

	public Order getOrder() {
		return order;
	}

	private void setOrderId(int orderId) {
		Order order = Order.getOrder(orderId);
		if (order != null)
			this.order = order;
		this.orderId = orderId;
	}

	public String getName() {
		return name;
	}

	public LightsaberColor getLightsaberColor() {
		return lightsaberColor;
	}

	public int getPower() {
		return power;
	}

	public Side getSide() {
		return side;
	}

	@Override
	public String toString() {
		return id + " Nazwa " + name + "Moc " + power + " Strona Mocy " + side;
	}

}
