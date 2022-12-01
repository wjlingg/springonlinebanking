# Springboot Online Banking Application


![alt text](https://github.com/wjlingg/springonlinebanking/blob/main/src/main/resources/static/images/image.png)

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

## Build and Deploy the Project
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

## Project Features, Functionality and Usage:

** For Customers/ Basic Users : **

1) **Use UOB bank services** :
- This platform is for UOB to market their services and get customers to save/ invest their money.
<br>

2) **Register new user** : 
- New customers can create a new user account

- On the UOB homepage (localhost://8080/), there is a "Click here to register" link.	

- Which directs to the new user signup page (http://localhost:8080/register). Customers can fill up the form with the necessary user details and account.

- On clicking the sign up button, the new user account is created and customer will be redirected to the homepage which leads to the login existing user (step 2).

- Once a new user is created, an Account Id will be generated and saved in the database along with Account Details.

3) **Login existing user** :
- Existing customers can login with their existing user account

- Existing users will key in their Username and Password on the Home page/ Index page (localhost://8080/)

4) **List of Bank Services** :
<br>

4.1) View Account	
- A drop down menu has been implemented for users to view the various accounts they have
	
- When Account Id is selected, it gets the User's Account and Transaction Details from the database and displays it in a table

- The page has been rendered to display the information only when the Account Id is selected.

- User can see more information about Account with the "Account Action" Button where the accrued interest is calculated for the Account Type. User is able to choose to Renew Deposit or Withdraw balance and Delete Account.

- The current maturity date has been set to 1 year and interest rates are set at 5%, 10% and 15% for Savings, Fixed Deposit and Recurring Deposit respectively.

4.1.1) Renew Deposit
- On maturity, user can choose to renew their deposit by depositing more more money. Their balance will be updated together with the accrued interest.

4.1.2) Withdraw balance and Delete Account
- Before or after maturity, users can also choose to withdraw the total balance and close the account.
- On completion of this action, the Deleted Account balance will be set to zero and set as dormant in the database. The Deleted Account will not be able to be edited anymore but can still be read for compliance.
<br>

4.2) Create Account
- Users can create a new account with the Create Account button. In the page (http://localhost:8080/createaccount), they are able to see a list of the existing accounts they have and create a new account (Savings/ Fixed Deposit/ Recurring Deposit).

- 1 user can have many accounts, there is currently no limit to the number of accounts each user can have.

- Users need a minimum of $500 to create a Savings account.
<br>

4.3) Make a transaction
- Withdraw or Deposit money into the Accounts
- The withdraw will not go through if the account's balance after withdraw is lower than $500.

** For Admin Users : **

1) Admin Users are able to login with the Username & Password: admin.

2) Admin can view the list of users and are able to edit and delete users.

## Future Improvements for Current Limitations

1) Able to withdraw to savings account
2) 