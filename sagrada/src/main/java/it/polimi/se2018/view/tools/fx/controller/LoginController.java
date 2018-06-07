package it.polimi.se2018.view.tools.fx.controller;

import it.polimi.se2018.view.app.JavaFXStageProducer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * Manages actions in login page
 *
 * @author Mathyas Giudici
 */

public class LoginController extends FXController {

    @FXML
    TextField name;
    @FXML
    private PasswordField password;
    @FXML
    CheckBox newUser;
    @FXML
    TextField server;
    @FXML
    RadioButton socketRadio;
    @FXML
    RadioButton rmiRadio;
    @FXML
    TextField requestPort;
    @FXML
    TextField objectPort;
    @FXML
    Label error;
    @FXML
    HBox errorContent;


    /**
     * Validates form data before call a login request
     */
    @FXML
    public void validation() {
        boolean isRMI = false;
        int intRequestPort = -1;
        int intObjectPort = -1;

        StringBuilder stringBuilder = new StringBuilder();

        checkName(stringBuilder);
        checkPassword(stringBuilder);
        checkServer(stringBuilder);

        if (!rmiRadio.isSelected() && !socketRadio.isSelected()) {
            stringBuilder.append("Deve essere selezionato il tipo di connessione\n");
        } else {
            isRMI = true;
            intRequestPort = checkRequestPort(stringBuilder);
            if (socketRadio.isSelected()) {
                isRMI = false;
                intObjectPort = checkObjectPort(stringBuilder);
            }
        }

        if (stringBuilder.toString().equals(""))
            this.loginCall(isRMI, intRequestPort, intObjectPort);
        else {
            String returnString = "ERRORI:\n" +
                    stringBuilder;
            Platform.runLater(() -> {
                error.setText(returnString);
                errorContent.setBackground(new Background(new BackgroundFill(Color.valueOf("#D11E22"), null, null)));
                errorContent.setMinHeight(error.getHeight());
            });
        }
    }

    /**
     * Tries a login request in the JavaFXApp
     *
     * @param isRMI       contains connection's type
     * @param requestPort contains request port's number
     * @param objectPort  contians object port's number
     */
    private void loginCall(boolean isRMI, int requestPort, int objectPort) {
        //Save information in JavaFXApp
        JavaFXStageProducer.getApp().tryLogin(name.getText(), rmiRadio.isSelected());

        //Call View Actions
        JavaFXStageProducer.getApp().getViewActions().login(
                name.getText(), password.getText(), this.newUser.isSelected(),
                server.getText(), isRMI, objectPort, requestPort);
    }

    /**
     * Checks name field isn't empty
     *
     * @param stringBuilder contains the errors' string builder
     */
    private void checkName(StringBuilder stringBuilder) {
        if (name.getText() == null || name.getText().equals("")) {
            stringBuilder.append("Nome non può essere vuoto\n");
        }
    }

    /**
     * Checks password field isn't empty
     *
     * @param stringBuilder contains the errors' string builder
     */
    private void checkPassword(StringBuilder stringBuilder) {
        if (password.getText() == null || password.getText().equals("")) {
            stringBuilder.append("Password non può essere vuota\n");
        }
    }

    /**
     * Checks server field isn't empty
     *
     * @param stringBuilder contains the errors' string builder
     */
    private void checkServer(StringBuilder stringBuilder) {
        if (server.getText() == null || server.getText().equals("")) {
            stringBuilder.append("Server non può essere vuoto\n");
        }
    }

    /**
     * Checks request port field isn't empty and contains a number
     *
     * @param stringBuilder contains the errors' string builder
     */
    private int checkRequestPort(StringBuilder stringBuilder) {
        int intRequestPort = -1;

        if (requestPort.getText() == null || requestPort.getText().equals("")) {
            stringBuilder.append("Porta Richieste non può essere vuota\n");
        } else {
            try {
                intRequestPort = Integer.parseInt(this.requestPort.getText());
                if (intRequestPort < 0) {
                    stringBuilder.append("Porta Richieste deve contenere un numero positivo\n");
                }
            } catch (Exception e) {
                stringBuilder.append("Porta Richieste non contiene un numero\n");
            }
        }

        return intRequestPort;
    }

    /**
     * Checks object port field isn't empty and contains a number
     *
     * @param stringBuilder contains the errors' string builder
     */
    private int checkObjectPort(StringBuilder stringBuilder) {
        int intObjectPort = -1;

        if (objectPort.getText() == null || objectPort.getText().equals("")) {
            stringBuilder.append("Porta Oggetti non può essere vuota\n");
        } else {
            try {
                intObjectPort = Integer.parseInt(this.objectPort.getText());
                if (intObjectPort < 0) {
                    stringBuilder.append("Porta Oggetti deve contenere un numero positivo\n");
                }
            } catch (Exception e) {
                stringBuilder.append("Porta Oggetti non contiene un numero\n");
            }
        }

        return intObjectPort;
    }

    /**
     * Manages correct radio button selections
     */
    public void selectedRadioRMI() {
        rmiRadio.setSelected(true);
        socketRadio.setSelected(false);
        objectPort.setDisable(true);
    }

    /**
     * Manages correct radio button selections
     */
    public void selectedRadioSocket() {
        socketRadio.setSelected(true);
        rmiRadio.setSelected(false);
        objectPort.setDisable(false);
    }
}