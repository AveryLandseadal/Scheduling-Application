    The purpose of the application is to provide a scheduling solution for a company utilizing different timezones.

    Author - Avery Landseadal

    Full JDK of version used (Java 17.0.1),
    JavaFX version compatible with JDK version (javafx-sdk-17.0.1)
    The MySQL Connector driver version number (mysql-connector-java-8.0.25)

    Directions on how to run - The paths will need to be set for JavaFX, Java JDK and the database connection.
    Navigate to File -> Project Structure
    Then under libraries  add the JavaFX lib folder and the mysql connector file if its not already setup.

    Under Run / Debug configurations the VM options may need to be setup inorder to get JavaFX working.
    
    Set the path to where JavaFx is located.
    
    Example : --module-path="C:\PATH_TO_FX" --add-modules=javafx.fxml,javafx.controls,javafx.graphics
    
    My current path is : --module-path="C:\Users\Av\Desktop\javafx-sdk-17.0.1\lib" --add-modules=javafx.fxml,javafx.controls,javafx.graphics

    In the DAO folder navigate to the JDBC file and update the following if needed :
    location
    databaseName
    userName
    password
