package sample;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.soulwing.snmp.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Controller {
    private static Controller instance;
    @FXML
    private TableView<Client> table01;
    @FXML
    public TableColumn<Client, String> Test;
    @FXML
    private TextField ipField;
    @FXML
    private TextField CommField;
    @FXML
    private Button scanNetworkBtn;
    @FXML
    public TableView<Varbinds> table02;
    @FXML
    public TableColumn<Varbinds, String> Name;
    @FXML
    public TableColumn<Varbinds, String> OID;
    @FXML
    private TableColumn<Varbinds, String> Value;
    private ArrayList<Client> clients = new ArrayList<>();

    public static Controller getInstance() {
        return instance;
    }

    public void load(String ip, String community) throws InterruptedException {
        VarbindCollection v = null;
        try {
            v = Main.read(ip, community);
        } catch (IOException | ExecutionException | InterruptedException e) {
            System.out.print("");
        }
        if (v == null) {
            for (int i = 0; i < clients.size(); i++) {
                if(clients.get(i).getTest().getText().equals(ip)){
                    clients.get(i).getTest().setStyle("-fx-background-color: yellow");
                }
            }

            System.out.println(ip + "not reachable with SNMP");
        }else{
            for (int i = 0; i < v.size(); i++) {
                table02.getItems().add(new Varbinds(v.get(i)));
            }
        }

        /*if (!frstScan){
            table02.getItems().clear();
            for (int i = 0; i < v.size(); i++) {
                table02.getItems().add(new Varbinds(v.get(i)));
            }
        }*/


        /*if (frstScan) {
            synchronized (this){
                table01.getItems().add(new Client(ip, "public"));
            }

        } else {
            table02.getItems().clear();
            for (int i = 0; i < v.size(); i++) {
                table02.getItems().add(new Varbinds(v.get(i)));
            }
        }*/


    }

    @FXML
    public void initialize() {
        instance = this;
        ipField = new TextField();
        CommField = new TextField();
        ipField.setText("10.10.30.0");
        CommField.setText("public");
        Name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        OID.setCellValueFactory(new PropertyValueFactory<>("OID"));
        Value.setCellValueFactory(new PropertyValueFactory<>("Value"));
        Test.setCellValueFactory(new PropertyValueFactory<>("Test"));
        table02.getColumns().clear();
        table02.getColumns().add(Name);
        table02.getColumns().add(OID);
        table02.getColumns().add(Value);
    }

    public void getIpBtn(ActionEvent actionEvent) {
        //read();
    }

    public void scanNetwork(ActionEvent actionEvent){
        int timeout = 1000;
        String host = ipField.getText();
        String[] temp = host.split("\\.");
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 1; i < 255; i++) {
            final int j = i;
            executor.execute(() -> {
                try {
                    System.out.println(j);
                    temp[3] = String.valueOf(j);
                    String x = String.join(".", temp);
                    InetAddress address = InetAddress.getByName(x);
                    if (address.isReachable(timeout)) {
                        System.out.println(address.toString());
                        //System.out.println("in" + CommField.getText());
                        clients.add(new Client(x, "public"));
                        table01.getItems().add(clients.get(clients.size() - 1));
                        load(x, "public");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdown();
        try {
            if(!executor.awaitTermination(500, TimeUnit.MILLISECONDS)){
                table02.getItems().clear();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void click(MouseEvent mouseEvent) {
        System.out.println("ssd");
    }
}
