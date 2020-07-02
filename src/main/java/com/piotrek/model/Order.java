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
import com.piotrek.util.SqlUtil;

public class Order implements Serializable {

	public static final String ORDER_FILE_EXTENSION;

	private static final long serialVersionUID;

	private static List<Order> ordersList;

	private int id;

	private String name;

	private transient List<Jedi> jedisList;

	static {
		serialVersionUID = -4584135592384518564L;
		ORDER_FILE_EXTENSION = ".order";
		ordersList = new ArrayList<Order>();
	}

	{
		jedisList = new ArrayList<Jedi>();
	}

	private Order(String name) {
		this.name = name;
	}

	public static void loadOrdersFromDatabse() throws SQLException {
		SqlUtil sqlUtil = new SqlUtil();
		ResultSet data = sqlUtil.executeQuery("SELECT * FROM JEDI_ORDER ORDER BY ID ASC");
		while (data.next()) {
			Order order = new Order(data.getString("NAME"));
			order.setId(data.getInt("ID"));
			ordersList.add(order);
		}
		sqlUtil.close();
	}

	public static void bindOrdersWithJedi() throws SQLException {
		for (Order order : ordersList)
			for (Jedi jedi : Jedi.getJedisList())
				if (jedi.getOrderId() == order.id) {
					if (!order.jedisList.contains(jedi))
						order.jedisList.add(jedi);
					if (jedi.getOrder() == null)
						jedi.addOrder(order);
				}
	}

	public static Order addOrder(String name) throws SQLException, NameException {
		if (name == null || name.isEmpty())
			throw new NameException("Wrong Name");
		Order order = new Order(name);
		addOrder(order);
		ordersList.add(order);
		return order;
	}

	private static void addOrder(Order order) throws SQLException {
		SqlUtil sqlUtil = new SqlUtil();
		sqlUtil.execute(getInsertOrderQuery(order));
		ResultSet data = sqlUtil.executeQuery("SELECT MAX(ID) AS MAX FROM JEDI_ORDER");
		data.next();
		order.setId(data.getInt("MAX"));
		sqlUtil.close();
	}

	private static String getInsertOrderQuery(Order order) {
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO JEDI_ORDER (NAME) VALUES ('");
		builder.append(order.name);
		builder.append("');");
		return builder.toString();
	}

	private static void addOrderIfNotExists(Order order) throws SQLException {
		Order o = getOrder(order.id);
		if (o == null) {
			addOrder(order);
			order.jedisList = new ArrayList<Jedi>();
			ordersList.add(order);
		}
	}

	public static void saveInFile(File file) {
		if (file == null)
			return;
		if (!file.exists())
			file = new File(file.toString());
		try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file))) {
			stream.writeObject(ordersList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readFromFile(File file) throws SQLException {
		if (file == null || !file.exists())
			return;
		if (!file.toString().endsWith(ORDER_FILE_EXTENSION))
			return;
		try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file))) {
			Object object = stream.readObject();
			if (object instanceof List<?>) {
				List<?> list = (List<?>) object;
				for (Object obj : list)
					if (obj instanceof Order) {
						Order order = (Order) obj;
						addOrderIfNotExists(order);
					}
			}
			bindOrdersWithJedi();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.writeObject(Encryptor.encrypt(Integer.toString(id)));
		stream.writeObject(Encryptor.encrypt(name));
	}

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		id = Integer.parseInt(Encryptor.decrypt((String) stream.readObject()));
		name = Encryptor.decrypt((String) stream.readObject());
	}

	public static List<Order> getOrdersList() {
		return Collections.unmodifiableList(ordersList);
	}

	public static Order getOrder(int id) {
		for (Order order : ordersList)
			if (order.id == id)
				return order;
		return null;
	}

	public List<Jedi> getJedisList() {
		return Collections.unmodifiableList(jedisList);
	}

	public void addJedi(Jedi jedi) {
		jedisList.add(jedi);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

}