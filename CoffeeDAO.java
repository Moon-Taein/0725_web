package kr.co.greenart.coffee;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// DAO 사용한 자원들 해제해줘야 함
public class CoffeeDAO {
	private Coffee mapRow(ResultSet rs) throws SQLException {
		int no = rs.getInt("no");
		String name = rs.getString("name");
		int price = rs.getInt("price");

		return new Coffee(no, name, price);
	}

	public List<Coffee> getAll() throws SQLException {
//		drvier 로드를 해줘야한다?
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Coffee> list = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/my_db", "root", "root");
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("select * from coffee")) {
			while (rs.next()) {
				Coffee c = mapRow(rs);
				list.add(c);
			}
		}
		return list;
	}

	public Coffee getRowByNumber(int number) throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Coffee coffee;
		String sql = "select * from coffee where no = ?";
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/my_db", "root", "root");
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, number);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				coffee = mapRow(rs);
				return coffee;
			}
		}
		return null;

	}

	public boolean isExist(String reqParam) {
		int no = Integer.parseInt(reqParam);
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String sql = "select * from coffee where no = ?";
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/my_db", "root", "root");
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, no);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	public boolean insertCoffee(Coffee coffee) {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		String sql = "INSERT INTO coffee (no, name, price) VALUES (?, ?, ?)";
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/my_db", "root", "root");
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, coffee.getNo());
			stmt.setString(2, coffee.getName());
			stmt.setInt(3, coffee.getPrice());
			return stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

}
