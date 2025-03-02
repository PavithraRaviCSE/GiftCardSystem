# Gift Card System

## Database Setup
**Database:** MySQL  
**Schema File:** `giftcardsystem.sql`  

### Steps to Import Database
1. Ensure MySQL is installed and running.  
2. Open a terminal and run:  


3. This will create the necessary database and tables.

## Admin Setup
- Insert an **admin username and password** manually into the `admin` table.  
- Run the application and select **Admin Login**.  
- Admins can perform the following actions:  
- Add users  
- View users  
- Search users (by name or account number)  
- Credit user accounts  

## User Login & Features
- **Note:** A user can log in **only if the admin has added them to the `user` table**.  
- First-time users log in with their **Date of Birth (DOB) in `YYYYMMDD` format**.  
- **Password Encryption:**  
- Numbers are shifted forward by **1** (e.g., `1 → 2`).  
- Lowercase letters are replaced with the next letter (e.g., `a → b`).  
- Uppercase letters are also shifted (e.g., `A → B`).  

### User Features:
- Create a gift card  
- View gift cards  
- Top up a card  
- Block a card  
- View transactions  
- Change their password  
