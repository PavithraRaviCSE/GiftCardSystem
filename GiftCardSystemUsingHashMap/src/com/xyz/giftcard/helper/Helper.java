package com.xyz.giftcard.helper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class Helper {

	private static int userCount = 0;
	private static int accountCount = 0;

	public static int generateUserId() {
		return ++userCount;
	}

	public static String generateAccountNumber() {

		return "ACC" + ++accountCount;
	}

	public static String generateCardNumber() {
		return String.format("%05d", new Random().nextInt(100000));
	}

	public static String generatePassword(LocalDate dob) {
		if (dob == null) {
			throw new IllegalArgumentException("Date of birth cannot be null");
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMMdd");
		return dob.format(formatter); // Format the LocalDate
	}

	public static String generatePin() {
		return String.format("%03d", new Random().nextInt(10000));
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

	public static <T> void printList(List<T> list) {
		if (list == null || list.isEmpty()) {
			System.out.println("The list is empty or null.");
			return;
		}

		for (T item : list) {
			System.out.println(item);
		}
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

}
