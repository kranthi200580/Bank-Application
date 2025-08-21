package com.borntogeek.sms_sender_twilio;
import com.twilio.Twilio ;
import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Message ;
import com.twilio.type.PhoneNumber;
import java.sql.* ;
import java.util.Scanner ;


public class BankApplication {

    // JDBC Connector
    static private final String url = "enterYourURL";
    static private final String userName = "root";
    static private final String password = "enterYourPassword" ;

    public static void main(String[] argh) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Scanner scanner = new Scanner(System.in);
        int personChoices;

        do {

            System.out.println("****** BANK APPLICATION ******");
            System.out.println("1. ADD CUSTOMER ");
            System.out.println("2. ALL CUSTOMER DETAILS ");
            System.out.println("3. CREDIT AMOUNT ");
            System.out.println("4. WITHDRAWAL AMOUNT ");
            System.out.println("5. DELETE ACCOUNT ");
            System.out.println("6. EXIT");

            System.out.print("Choose an option from (1-6) : ");
            personChoices = scanner.nextInt();
            scanner.nextLine();

            switch (personChoices) {

                case 1:
                    try (Connection connection = DriverManager.getConnection(url, userName, password)) {
                        String addCustomer = "INSERT INTO bank (accountNumber, firstName, lastName, customerMail, mobileNumber, creditAmount, debitAmount, currentBalance, accountSid, authToken, twilioNumber, todaysDate)VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement preparedStatement = connection.prepareStatement(addCustomer);

                        while (true) {

                            System.out.print("Enter Account Number : ");
                            int accountNumber = scanner.nextInt () ;
                            scanner.nextLine () ;

                            System.out.print("Enter First Name : ");
                            String firstName = scanner.nextLine () ;

                            System.out.print ("Enter Last Name : ");
                            String lastName = scanner.nextLine () ;

                            System.out.print("Enter Customer Mail Address : ");
                            String customerMail = scanner.nextLine () ;

                            System.out.print("Enter Mobile Number (+91) : ");
                            String mobileNumber = scanner.nextLine () ;

                            System.out.print("Enter Credit Amount : ");
                            int creditAmount = scanner.nextInt();

                            System.out.print("Enter Debit Amount : ");
                            int debitAmount = scanner.nextInt();

                            System.out.print("Enter Current Balance :");
                            int currentBalance = scanner.nextInt();
                            scanner.nextLine () ;

                            System.out.print("Enter Account SID : ");
                            String accountSid = scanner.nextLine () ;

                            System.out.print("Enter Authentication Token : ");
                            String authToken = scanner.nextLine () ;

                            System.out.print("Enter your Twilio Number (+91) : ");
                            String twilioNumber = scanner.nextLine () ;

                            System.out.print("Enter Todays Date : ") ;
                            String todaysDate = scanner.nextLine () ;

                            System.out.print("Add Another One (Yes/No) :");
                            String choice = scanner.nextLine () ;

                            preparedStatement.setInt(1, accountNumber);
                            preparedStatement.setString(2, firstName);
                            preparedStatement.setString(3, lastName);
                            preparedStatement.setString(4, customerMail);
                            preparedStatement.setString(5, mobileNumber);
                            preparedStatement.setInt(6, creditAmount);
                            preparedStatement.setInt(7, debitAmount);
                            preparedStatement.setInt(8, currentBalance);
                            preparedStatement.setString(9, accountSid);
                            preparedStatement.setString(10, authToken);
                            preparedStatement.setString(11, twilioNumber);
                            preparedStatement.setString(12, todaysDate) ;

                            preparedStatement.addBatch();
                            System.out.println("The Account is Created!!");
                            System.out.println();

                            if (choice.equals("No")) {
                                break;
                            }
                        }

                        int[] array = preparedStatement.executeBatch();

                        for (int j : array) {
                            if (j == 0) {
                                System.out.println("The customer is not inserted");
                            }
                        }

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 2:
                    try (Connection connection = DriverManager.getConnection(url, userName, password)) {
                        String viewCustomers = "select * from bank";
                        PreparedStatement preparedStatement = connection.prepareStatement(viewCustomers);
                        ResultSet resultSet = preparedStatement.executeQuery();

                        while (resultSet.next()) {

                            System.out.println();
                            System.out.println("Account Number  : " + resultSet.getInt("accountNumber"));
                            System.out.println("First Name : " + resultSet.getString("firstName"));
                            System.out.println("Last Name : " + resultSet.getString("lastName"));
                            System.out.println("Customer Mail : " + resultSet.getString("customerMail"));
                            System.out.println("Last Credit Amount : " + resultSet.getString("creditAmount"));
                            System.out.println("Last Debit Amount : " + resultSet.getString("debitAmount"));
                            System.out.println("Current Balance : " + resultSet.getString("currentBalance"));
                            System.out.println("--------------------------------");
                            System.out.println();

                        }

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 3:
                    try (Connection connection = DriverManager.getConnection(url, userName, password)) {
                        // First query
                        String insertedQuery = "update bank set creditAmount = ? where accountNumber = ? ";
                        PreparedStatement preparedStatement2 = connection.prepareStatement(insertedQuery);

                        System.out.print("Enter Amount : ");
                        int creditAmount = scanner.nextInt();

                        System.out.print("Enter Account Id to transfer the amount : ");
                        int accountNumber = scanner.nextInt();

                        preparedStatement2.setInt(1, creditAmount);
                        preparedStatement2.setInt(2, accountNumber);

                        preparedStatement2.addBatch();
                        preparedStatement2.executeUpdate();

                        // Second Query
                        String creditsQuery = "update bank set currentBalance = coalesce(currentBalance) + (creditAmount) where accountNumber = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(creditsQuery);

                        System.out.print("Confirmation Account Number : ");
                        int confirmationAccountNumber = scanner.nextInt();

                        preparedStatement1.setInt(1, confirmationAccountNumber);
                        preparedStatement1.addBatch();

                        //Third Query
                        String mobileQuery = "select firstName, mobileNumber, creditAmount, accountSid, authToken, twilioNumber, todaysDate from bank where accountNumber = ?";
                        PreparedStatement preparedStatement5 = connection.prepareStatement(mobileQuery);

                        System.out.print("Please again enter your account number for your mobile number : ");
                        int mobileAccountNumber = scanner.nextInt();
                        preparedStatement5.setInt(1, mobileAccountNumber);
                        preparedStatement5.addBatch();
                        ResultSet resultSet = preparedStatement5.executeQuery();

                        int[] array = preparedStatement1.executeBatch();
                        for (int j : array) {
                            if (j != 0) {
                                System.out.println("--------Successfully Credited--------");
                                System.out.println();
                            } else {
                                System.out.println("Not Credited");
                                System.out.println();
                            }
                        }

                        while (resultSet.next()) {
                            String customerName = resultSet.getString ("firstName");
                            String mobileNumber = resultSet.getString ("mobileNumber");
                            int creditAmount1 = resultSet.getInt ("creditAmount");
                            String accountSid = resultSet.getString ("accountSid");
                            String authToken = resultSet.getString ("authToken");
                            String twilioNumber = resultSet.getString ("twilioNumber");
                            String todaysDate = resultSet.getString ("todaysDate") ;
                            if (accountNumber == confirmationAccountNumber) {
                                try {
                                    creditSmsAlert(customerName, mobileNumber, creditAmount1, accountSid, authToken, twilioNumber, todaysDate);
                                } catch (TwilioException twilioException) {
                                    System.err.println("The Daily limit is 9 SMS only");
                                }
                            }
                        }

                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 4:
                    try (Connection connection1 = DriverManager.getConnection(url, userName, password)) {

                        String queryOtp = "select accountSid, authToken, mobileNumber, twilioNumber from bank where accountNumber = ?";
                        PreparedStatement preparedStatement8 = connection1.prepareStatement(queryOtp);

                        System.out.print("Enter your Account Number for Generating OTP : ");
                        int accountNumber1 = scanner.nextInt();
                        preparedStatement8.setInt(1, accountNumber1);
                        preparedStatement8.addBatch();
                        ResultSet resultSet1 = preparedStatement8.executeQuery();

                        int randomPin = 0;

                        while (resultSet1.next()) {
                            String accountSid = resultSet1.getString ("accountSid") ;
                            String authToken = resultSet1.getString ("authToken") ;
                            String mobileNumber = resultSet1.getString ("mobileNumber") ;
                            String twilioNumber = resultSet1.getString ("twilioNumber") ;
                            try {
                                randomPin = generateOTP(accountSid, authToken, mobileNumber, twilioNumber) ;
                            } catch (TwilioException twilioException) {
                                System.err.println("The Daily limit is only 9 Messages"); // Exception Error
                            }
                        }
                        System.out.print("Enter OTP : ");
                        int myOtpChoice = scanner.nextInt();

                        if (randomPin == myOtpChoice) {

                            // First Query
                            String amountQuery = "update bank set debitAmount = ? where accountNumber = ?";
                            PreparedStatement preparedStatement3 = connection1.prepareStatement(amountQuery);

                            System.out.print("Enter Amount : ");
                            int debitAmount = scanner.nextInt();

                            System.out.print("Enter account number to remove amount : ");
                            int accountNumber = scanner.nextInt();

                            preparedStatement3.setInt(1, debitAmount);
                            preparedStatement3.setInt(2, accountNumber);

                            preparedStatement3.addBatch();
                            preparedStatement3.executeUpdate();

                            // Second Query
                            String debitsQuery = "update bank set currentBalance = coalesce(currentBalance) - (debitAmount) where accountNumber = ?";
                            PreparedStatement preparedStatement4 = connection1.prepareStatement(debitsQuery) ;

                            System.out.print("Confirmation Account Number : ") ;
                            int confirmationAccountNumber = scanner.nextInt() ;

                            preparedStatement4.setInt(1, confirmationAccountNumber) ;
                            preparedStatement4.addBatch() ;

                            //Third Query
                            String mobileQuery = "select firstName, mobileNumber, debitAmount, accountSid, authToken, twilioNumber, todaysDate from bank where accountNumber = ?";
                            PreparedStatement preparedStatement6 = connection1.prepareStatement(mobileQuery) ;

                            System.out.print("Please again enter your account number for your mobile number : ") ;
                            int mobileAccountNumber = scanner.nextInt();
                            preparedStatement6.setInt(1, mobileAccountNumber) ;
                            preparedStatement6.addBatch();
                            ResultSet resultSet = preparedStatement6.executeQuery() ;

                            int[] array1 = preparedStatement4.executeBatch();
                            for (int j : array1) {
                                if (j != 0) {
                                    System.out.println("-----------Debited Successfully---------") ;
                                    System.out.println();
                                } else {
                                    System.out.println("Not Debited") ;
                                    System.out.println() ;
                                }
                            }

                            while (resultSet.next()) {
                                String customerName = resultSet.getString ("firstName") ;
                                String mobileNumber = resultSet.getString ("mobileNumber") ;
                                int debitAmount1 = resultSet.getInt ("debitAmount") ;
                                String accountSid = resultSet.getString ("accountSid") ;
                                String authToken = resultSet.getString ("authToken") ;
                                String twilioNumber = resultSet.getString ("twilioNumber") ;
                                String todaysDate = resultSet.getString ("todaysDate") ;
                                if (accountNumber == confirmationAccountNumber) {
                                    try {
                                        debitSmsAlert(customerName, mobileNumber, debitAmount1, accountSid, authToken, twilioNumber, todaysDate);
                                    } catch (TwilioException twilioException) {
                                        System.err.println("The Daily limit is 9 SMS only");
                                    }
                                }
                            }
                        } else {
                            System.out.println ("Incorrect OTP !!")  ;
                            System.out.println() ;
                        }

                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 5:
                    try (Connection connection = DriverManager.getConnection(url, userName, password)) {
                        String deleteAccountQuery = "delete from bank where accountNumber = ?";
                        PreparedStatement preparedStatement7 = connection.prepareStatement(deleteAccountQuery);

                        System.out.print("Enter the Account Number to Delete the Bank Account permanently : ");
                        int accountNumber = scanner.nextInt();

                        preparedStatement7.setInt(1, accountNumber);
                        preparedStatement7.executeUpdate();
                        System.out.println("The Account is Successfully Deleted!!");
                        System.out.println();

                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 6:
                    System.out.println() ;
                    System.out.println("-------------------") ;
                    System.out.println("ThankYou for Visiting!!") ;
                    System.out.println("-------------------") ;
                    System.out.println() ;
                    System.exit(0);
                    break;

                default:
                    System.out.println("Please enter a valid option");
                    break;
            }
        } while (personChoices != 6);
    }

    //Credit Sms alert...
    public static void creditSmsAlert(String customerName,
                                      String mobileNumber,
                                      int creditAmount1,
                                      String accountSid,
                                      String authToken,
                                      String twilioNumber,
                                      String todaysDate) {

        Twilio.init(accountSid, authToken);
        Message message = Message.creator(
                new PhoneNumber(mobileNumber), // To Number
                new PhoneNumber(twilioNumber), // From twilio number
                "Congratulations !! " + customerName + " Your a/c no.XXXXXXXX4562 is credited by Rs." + creditAmount1 + ".00 on " +"("+todaysDate+")"+ " and debited from a/c no.XXXXXXXX2466").create();
        message.getSid();
        System.out.println("Message sent Successfully!!");
        System.out.println();
    }
    
    // OTP Generator
    public static int generateOTP(String accountSid, String authToken, String mobileNumber, String twilioNumber) {

        int randomPin = (int) (Math.random() * 9000) + 1000;
        Twilio.init(accountSid, authToken);
        Message message = Message.creator(
                new PhoneNumber(mobileNumber),
                new PhoneNumber(twilioNumber),
                randomPin + "  OTP is Generated, don't share it to anyone!!"
        ).create() ;
        message.getSid();
        System.out.println("OTP sent Successfully!!");
        System.out.println();

        return randomPin;
    }

    // Withdrawal Sms Alert...
    public static void debitSmsAlert(String customerName,
                                     String mobileNumber,
                                     int debitAmount1,
                                     String accountSid,
                                     String authToken,
                                     String twilioNumber,
                                     String todaysDate) {

        Twilio.init(accountSid, authToken);
        Message message = Message.creator(
                new PhoneNumber(mobileNumber),
                new PhoneNumber(twilioNumber),
                "Dear " + customerName + "!! " + " Your a/c no.XXXXXXXX5378 is debited for Rs." + debitAmount1 + ".00 on " +"("+todaysDate+")"+ " and credited to a/c no.XXXXXXXX4537").create();
        message.getSid();
        System.out.println("Message sent Successfully!!");
        System.out.println();
    }

}

