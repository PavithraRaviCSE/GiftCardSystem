package com.xyz.giftcard.main;

import java.util.*;
import com.xyz.giftcard.services.*;

public class Main {

	private static Scanner input = new Scanner(System.in);
	private static AdminService admin = new AdminService();
	private static UserService user = new UserService();
	private static PurchaseService purchase = new PurchaseService();

	public static void main(String args[]) {

		boolean flag = true;

		while (flag) {

			System.out.println("............Options.........");
			System.out.println("1. Exit");
			System.out.println("2. Admin");
			System.out.println("3. User");
			System.out.println("4.purchase");

			System.out.println("Enter your option:");

			int option = input.nextInt();

			switch (option) {
			case 1:
				System.out.println("Exited....");
				flag = false;
				break;
			case 2:
				System.out.println("Admin login....");
				admin.adminFunctionalities();

				break;
			case 3:
				System.out.println("User....");
				user.userFunctionalities();

				break;
			case 4:
				purchase.purchase();
				break;
			default:
				System.out.println("Invalid option..");
			}

		}

	}
}