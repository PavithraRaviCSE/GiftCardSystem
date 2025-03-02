package com.xyz.giftcard.helper;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Helper {

	public static String generatePassword(LocalDate dob) {
		if (dob == null) {
			throw new IllegalArgumentException("Date of birth cannot be null");
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMMdd");
		return dob.format(formatter);
	}

	public static String encryption(String password) {

		StringBuilder sb = new StringBuilder();

		for (char c : password.toCharArray()) {
			if (Character.isDigit(c))
				sb.append(getNumber(c - '0'));
			else
				sb.append(getChar(c));

		}

		return sb.toString();
	}

	private static char getChar(char c) {
		if (c == 'z')
			return 'a';
		else if (c == 'Z')
			return 'A';
		return (char) (c + 1);

	}

	private static int getNumber(int n) {

		if (n == 9)
			return 0;
		return n + 1;
	}

	public static <E> void printList(List<E> list, String column) {

		if (list.isEmpty()) {
			System.out.println("No data available.....");
			return;
		}
		System.out.println(column);
		for (E c : list) {
			System.out.println(c);
		}

	}

	public static <E> void displayList(List<E> list, String column, boolean includeSerialNumbers) {
		if (list.isEmpty()) {
			System.out.println("No data available.....");
			return;
		}

		if (includeSerialNumbers) {
			System.out.println("Sn:\t" + column);
			int count = 1;
			for (E item : list) {
				System.out.println(count++ + "\t" + item);
			}
		} else {
			System.out.println(column);
			for (E item : list) {
				System.out.println(item);
			}
		}
	}

	public static Timestamp getTimeStamp() {
		return new Timestamp(System.currentTimeMillis());
	}

	public static int getValidChoice(int min, int max, Scanner input) {
		int choice;
		while (true) {
			try {
				choice = input.nextInt();
				if (choice >= min && choice <= max) {
					return choice;
				}
				System.out.println("Please enter a number between " + min + " and " + max + ".");
			} catch (Exception e) {
				System.out.println("Invalid input. Please enter a valid number.");
				input.nextLine(); // Clear the invalid input
			}
		}
	}

//	public static boolean isValidName(String name) {
//
//		if (name.length() < 2 || name.length() > 50) {
//			System.out.println("Name must be between 2 and 50 characters long.");
//			return false;
//		}
//
//		String regex = "^[A-Za-z]+( [A-Za-z]+)*$";
//		if (!name.matches(regex)) {
//			System.out.println("Name can only contain letters and single spaces between words.");
//			return false;
//		}
//
//		return true;
//	}
//	
//	public String getValidName(Scanner input) {
//		while (true) {
//			String name = input.next();
//
//			if (isValidName(name)) {
//				return name;
//			} else {
//				System.out.println("Enter the name: ");
//			}
//		}
//	}

}
