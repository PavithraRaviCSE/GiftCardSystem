package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.service.UserException;

public class HelperDAO {

	public static long fetchValue(Connection conn, String name) throws UserException {
		String selectQuery = "SELECT value FROM idgenerator WHERE name = ?";
		String updateQuery = "UPDATE idgenerator SET value = value + 1 WHERE name = ?";
		String insertQuery = "INSERT INTO idgenerator (name, value) VALUES (?, ?)";

		try (PreparedStatement pst = conn.prepareStatement(selectQuery)) {
			pst.setString(1, name);

			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {

					long id = rs.getLong("value");

					System.out.println("getId: " + id);

					try (PreparedStatement updatePst = conn.prepareStatement(updateQuery)) {
						updatePst.setString(1, name);
						int rowsUpdated = updatePst.executeUpdate();
						if (rowsUpdated > 0) {
							return id;
						} else {
							throw new UserException("Failed to update value for: " + name);
						}
					}

				}

				else {
					try (PreparedStatement insertPst = conn.prepareStatement(insertQuery)) {

						System.out.println("No " + name + " is in db");

						long id = 0;
						insertPst.setString(1, name);
						if (name.equals("account_number"))
							id = 1002;
						else if (name.equals("card_number"))
							id = 10002;
						else
							id = 2;
						insertPst.setLong(2, id);
						int rowsInserted = insertPst.executeUpdate();
						if (rowsInserted > 0) {
							System.out.println("1 row inserted");
							return id - 1;
						} else {
							System.out.println("failed to indert the row in idgenerator");
							throw new UserException("Failed to insert value for: " + name);
						}
					}
				}
			}
		} catch (SQLException e) {
			throw new UserException("Database error: " + e.getMessage(), e);
		}
	}

}
