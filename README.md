# Springboot Online Banking

![alt text](https://https://github.com/wjlingg/springonlinebanking/blob/main/image.png)

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

### Features:

1) ***Registration*** : Add description here
2) ***Login*** : Add description here
3) 

### Sample flow of operations:

1) Register for an account through the `Click here to register` link
2) 

### Future work:

1) Able to withdraw to savings account
2) 