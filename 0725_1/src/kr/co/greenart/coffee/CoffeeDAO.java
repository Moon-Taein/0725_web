package kr.co.greenart.coffee;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CoffeeDAO {
	public CoffeeDAO() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private Coffee mapRow(ResultSet rs) throws SQLException {
		int no = rs.getInt("no");
		String name = rs.getString("name");
		int price = rs.getInt("price");

		return new Coffee(no, name, price);
	}

	public Coffee getByNo(int no) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/my_db", "root", "root");
			stmt = conn.prepareStatement("SELECT * FROM coffee WHERE no = ?");
			stmt.setInt(1, no);
			rs = stmt.executeQuery();

			if (rs.next()) {
				return mapRow(rs);
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		// return 대신에 exception throw 해주기
		throw new NotFoundException(no + "번의 커피가 존재하지 않음.");
	}

	public List<Coffee> getAll() throws SQLException {
		List<Coffee> list = new ArrayList<Coffee>();
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/my_db", "root", "root");
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM coffee")) {
			while (rs.next()) {
				Coffee c = mapRow(rs);
				list.add(c);
			}
		}
		return list;
	}

	// auto-increment 숫자 가져와서 확인까지 가능
	public int insert(Coffee coffee) throws SQLException {
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/my_db", "root", "root");
				PreparedStatement stmt = conn.prepareStatement("insert into coffee (name, price) values (? ,?)",
						Statement.RETURN_GENERATED_KEYS);) {
			stmt.setString(1, coffee.getName());
			stmt.setInt(2, coffee.getPrice());

			int result = stmt.executeUpdate();
			if (result == 1) {
				try (ResultSet rs = stmt.getGeneratedKeys()) {
					rs.next();
					int pk = rs.getInt(1);

					return pk;
				}
			}
		}
		throw new RuntimeException("생성 실패");
	}

}
