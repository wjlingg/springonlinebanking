# Springboot Online Banking Application

>Note: This project is for educational purposes only. It is a project done by UOB employees to learn Spring Boot.

![Application Image](https://github.com/wjlingg/springonlinebanking/blob/main/src/main/resources/static/images/application.png)

## Introduction
The Springboot Online Banking Application aims to:

1) Handle bank accounts in multiple account types (Savings, Fixed Deposit and Recurring Deposit)

2) User base and user account mappings (Non-Registered User, Registered User and Admin)

3) Handling transactions in fund transfers and utility payments

4) Store all the transaction data on local database storage (MySQL/ Oracle SQL Developer)

### Contents Page

1) Introduction

2) Build and Deploy the Project

3) Project Features, Functionality and Usage

4) Future Improvements for Current Limitations

### Build and Deploy the Project
```
mvn clean install
```

This is a Spring Boot project, so you can deploy it by simply using the main class: `SpringOnlineBankingApplication.java`

Once deployed, you can access the app at:

https://localhost:8080

### Set up MySQL

By default, the project is configured to use the embedded H2 database.
If you want to use the MySQL instead, you need to uncomment relevant section in the [application.properties](src/main/resources/application.properties) and create the db user as shown below:
```
mysql -u root -p 
> CREATE USER 'root'@'localhost' IDENTIFIED BY 'root';
> GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost';
> FLUSH PRIVILEGES;

Go to MySQL Workbench
> Create a connection with hostname: localhost, Port: 3306, Username: root, Password: root
> Open connection
> CREATE DATABASE IF NOT EXISTS online_banking;
> USE online_banking;
```

### Project Features, Functionality and Usage:

**For Customers / Basic Users :**

1) **Use UOB bank services** :
- This platform is for UOB to market their services and get customers to save / invest their money.
  <br>

2) **Register new user** :

![Application Image](https://github.com/wjlingg/springonlinebanking/blob/main/src/main/resources/static/images/register.png)

- New customers can create a new user account

- On the UOB homepage (localhost://8080/), there is a "Click here to register" link.

- Which directs to the new user signup page (http://localhost:8080/register). Customers can fill up the form with the necessary user details and account.

- On clicking the sign up button, the new user account is created and customer will be redirected to the homepage which leads to the login existing user (step 2).

3) **Login existing user** :

![Application Image](https://github.com/wjlingg/springonlinebanking/blob/main/src/main/resources/static/images/login.png)

- Existing customers can login with their existing user account

- Existing users will key in their Username and Password on the Home page/ Index page (localhost://8080/)

4) **List of Bank Services** :
   <br>

4.1) View Account

![Application Image](https://github.com/wjlingg/springonlinebanking/blob/main/src/main/resources/static/images/viewaccount.png)

![Application Image](https://github.com/wjlingg/springonlinebanking/blob/main/src/main/resources/static/images/processviewaccount.png)

- A drop down menu has been implemented for users to view the various accounts they have

- When Account Id is selected, it gets the User's Account and Transaction Details from the database and displays it in a table

- The page has been rendered to display the information only when the Account Id is selected.

- User can see more information about Account with the "Account Action" Button where the accrued interest is calculated for the Account Type. User is able to choose to Renew Deposit or Withdraw balance and Delete Account.

- The current maturity date has been set to 1 year and interest rates are set at 5%, 10% and 15% for Savings, Fixed Deposit and Recurring Deposit respectively.

4.1.1) Renew Deposit

![Application Image](https://github.com/wjlingg/springonlinebanking/blob/main/src/main/resources/static/images/renewdeposit.png)

![Application Image](https://github.com/wjlingg/springonlinebanking/blob/main/src/main/resources/static/images/processrenewdeposit.png)

- On maturity, user can choose to renew their deposit by depositing more than, less than or equal to the total balance after accrued interest.
- If chosen deposited amount is more than total balance, remaining amount required will be credited from user savings account.
- If chosen deposited amount is less than total balance, excess amount will be deposited to user savings account.

4.1.2) Withdraw balance and Delete Account

![Application Image](https://github.com/wjlingg/springonlinebanking/blob/main/src/main/resources/static/images/deleteaccount.png)

- Before or after maturity, users can also choose to withdraw the total balance and close the account.
- For fixed deposit and recurring deposit, total balance will be withdrawn to user savings account.
- On completion of this action, the Deleted Account balance will be set to zero and set as dormant in the database. The Deleted Account can never be edited but can still be read for compliance.
  <br>

4.2) Create Account

![Application Image](https://github.com/wjlingg/springonlinebanking/blob/main/src/main/resources/static/images/createaccount.png)

- Users can create a new account with the Create Account button. In the page (http://localhost:8080/createaccount), they are able to see a list of the existing accounts they have and create a new account (Savings/ Fixed Deposit/ Recurring Deposit).
- 1 user can have many accounts, there is currently no limit to the number of accounts each user can have.
- Users need a minimum of $500 to create any account.
- In addition, users need to input the monthly contribution amount for recurring deposit account.
  <br>

4.3) Make a transaction

![Application Image](https://github.com/wjlingg/springonlinebanking/blob/main/src/main/resources/static/images/transaction.png)

- Withdraw or Deposit money into Savings Account only.
- The withdrawal will not be successful if the account's balance after withdrawal is lower than $500.

**For Admin Users :**

![Application Image](https://github.com/wjlingg/springonlinebanking/blob/main/src/main/resources/static/images/admin.png)

![Application Image](https://github.com/wjlingg/springonlinebanking/blob/main/src/main/resources/static/images/processadmin.png)

1) Admin Users will login with the Username & Password: admin.

2) Admin can view the list of users and are able to edit and delete users.

3) Admin can filter the list of user by username.

### Future Improvements for Current Limitations

1) This program has the potential to further develop the search query criteria of users. Users can be filtered or sorted by other values such as balance/ date/ transaction type.

2) There is only 1 Admin User created upon runtime of program. There is possibility for a differentiation of Admin, basic user and other user roles. Then Admin rights can be allocated based on each username instead of just 1 Admin username.
