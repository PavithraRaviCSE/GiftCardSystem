# Gift Card System

## Prerequisites
- **Latest Eclipse version(2024)**.(You can use any other IDE) 
- **Redis Server**: Install Redis server. (Used as cache)
- **MySQL Database**: Required for storing user and gift card data.
  import the sql provided in this file and then create a user with user type admin manually.
  Note: password is encripted, so wile login you need to give the password correctly. the encrytion details are given below.
- **Maven**: Used for project dependencies and build management.

## Features
- **JWT Token Authentication**
- **Rate Limiting Filter**
- **Jetty Server (as a plugin)**
- **Redis Caching**
- **Password Encryption** (e.g., '1' → '2', 'A' → 'B', 'a' → 'b')

## Installation and Setup
1. Install Redis server using Ubuntu terminal.
2. Configure MySQL and Redis.
3. Manually create an admin user in the database.
4. Clone the repository and navigate to the project directory.
5. Use the following command to run the application:
   ```sh
   mvn jetty:run
   ```
6. After starting of the server of to any browser and give the url "localhost](http://localhost:8080/GiftCardSystem/Login"
   this will open up the login page and then u can proceed.

## Authentication and Password Encryption
- Passwords are stored in an encrypted format.
- Example:
  - Store: `2345`
  - Login: Use `1234` as the password.

## Functionalities
### Admin Features
- Create user
- view user by name or account number
- topup user

### User Features
- Create and top up gift cards.
- view card transactions 
- Block gift cards if needed.
- change password.
  
### Purchase Features
- anyone can use any cardnumber and pin to purchase the item.
  
## Running the Application
1. Ensure Redis and MySQL services are running.
2. Run the application with:
   ```sh
   mvn jetty:run
   ```
3. Access the application via browser or API endpoints.

## Notes
- This is a **Maven-based** application.
- Jetty server is used as an embedded server.
- ChatGPT provided the admin and user functionalities as mentioned in previous prompts.

