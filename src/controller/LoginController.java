package controller;

import DAO.appointmentDAO;
import DAO.userDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import static java.util.ResourceBundle.getBundle;
import static model.User.userID;

public class LoginController implements Initializable {
    public Label zoneText;
    public Label errorText;
    public Label zone;
    public Label loginText;
    Stage stage;
    Parent scene;

    @FXML
    private Button loginButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label usernameText;
    @FXML
    private Label passwordText;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        checkLocale();
    }

    public void checkLocale() {
        Locale locale = Locale.getDefault();
        zoneText.setText(ZoneId.systemDefault().toString());
        ResourceBundle resourceBundle = getBundle("languages/lang", Locale.getDefault());

        if (Locale.getDefault().getLanguage().equals("fr") || (Locale.getDefault().getLanguage().equals("en"))) {
            usernameText.setText(resourceBundle.getString("Username"));
            passwordText.setText(resourceBundle.getString("Password"));
            loginButton.setText(resourceBundle.getString("Login"));
            cancelButton.setText(resourceBundle.getString("Cancel"));
            zone.setText(resourceBundle.getString("Zone"));
            loginText.setText(resourceBundle.getString("LoginText"));

        }
    }

    public void cancelButtonOnAction(javafx.event.ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * @return returns the user ID on login attempt
     * @throws IOException throws an error if the IO operation fails
     */
    public int loginButtonAction() throws IOException, SQLException {
        ResourceBundle resourceBundle = getBundle("languages/lang", Locale.getDefault());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        String currentTime = localDateTime.format(dateTimeFormatter);

        try {
            String usernameInput = usernameField.getText();
            String passwordInput = passwordField.getText();
            int userID = userDAO.checkLogin(usernameInput, passwordInput);

            if (userID >= 0) {
                appointmentWithin15();
                PrintStream out = new PrintStream(new FileOutputStream("login_activity.txt", true));
                System.setOut(out);
                System.out.println("Login attempt : " + "Username = " + usernameInput + " " + "Password = " + passwordInput + " " + currentTime + " Login attempt was successful!");
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../view/SchedulingScreen.fxml")));
                stage = (Stage) loginButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            }
            if (userID == -1) {
                PrintStream out = new PrintStream(new FileOutputStream("login_activity.txt", true));
                System.setOut(out);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(resourceBundle.getString("Error"));
                alert.showAndWait();
                System.out.println("Login attempt : " + "Username = " + usernameInput + " " + "Password = " + passwordInput + " " + currentTime + " Login attempt was unsuccessful!");
            }


            return userID;
        } finally {

        }
    }

    public void appointmentWithin15() throws SQLException {
        ObservableList<Appointment> appointmentIn15 = FXCollections.observableArrayList();
        ResourceBundle resourceBundle = getBundle("languages/lang", Locale.getDefault());
        LocalDateTime currentTime = LocalDateTime.now();

        // Gets current time and adds 15 minutes for the appointment check.
        LocalDateTime currentTimePlus15 = LocalDateTime.now().plusMinutes(15);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ObservableList<Appointment> appointmentObservableList = appointmentDAO.getAppointment();


        for (Appointment appointment : appointmentObservableList) {
            String getStart = appointment.getStart();
            String getID = String.valueOf(appointment.getAppointmentID());
            LocalDateTime localDateStart = LocalDateTime.parse(getStart, dateTimeFormatter);

            // Convert LDT to ZonedDateTime to show appointments in local time based on the users machine.
            final ZoneId utc = ZoneId.of("UTC");
            final ZoneId local = ZoneId.systemDefault();
            ZonedDateTime zonedDateTimeStart = localDateStart.atZone(utc).withZoneSameInstant(local);
            String stringStart = zonedDateTimeStart.format(dateTimeFormatter);
            LocalDateTime convertStringStart = LocalDateTime.parse(stringStart, dateTimeFormatter);

            if (convertStringStart.isAfter(currentTime) && convertStringStart.isBefore(currentTimePlus15)) {
                appointmentIn15.add(appointment);
            }
            if (appointmentIn15.size() == 1){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(resourceBundle.getString("Appointment15") + "\n" +
                        resourceBundle.getString("Appointment153") + getID + " " + "\n" +
                        resourceBundle.getString("Appointment152") + convertStringStart.format(dateTimeFormatter));
                alert.showAndWait();
                return;}
            }
                if (appointmentIn15.size() == 0){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(resourceBundle.getString("NoAPT"));
                alert.showAndWait();
            }
        }
    }







