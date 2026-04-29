package com.ukdw.rplbo.soal_ug_javafx;

import com.ukdw.rplbo.soal_ug_javafx.data.Mahasiswa_table;
import com.ukdw.rplbo.soal_ug_javafx.data.Matakuliah_table;
import com.ukdw.rplbo.soal_ug_javafx.data.Nilai_table;
import com.ukdw.rplbo.soal_ug_javafx.entity.Mahasiswa;
import com.ukdw.rplbo.soal_ug_javafx.entity.Matakuliah;
import com.ukdw.rplbo.soal_ug_javafx.entity.Nilai;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppController {
    @FXML
    private ComboBox<String> option;
    @FXML
    private TableView<Object> table;
    @FXML
    private TableColumn<Object,String> column1;
    @FXML
    private TableColumn<Object,String> column2;
    @FXML
    private TableColumn<Object,String> column3;

    @FXML
    private BarChart<String, Number> barchart;
    @FXML
    private LineChart<String, Number> linechart;
    @FXML
    private PieChart piechart;


    Mahasiswa_table mhs_table = new Mahasiswa_table();
    Matakuliah_table mtkl_table = new Matakuliah_table();
    Nilai_table nilai_table = new Nilai_table();


    public AppController() throws SQLException {
    }

    @FXML
    public void initialize() throws SQLException {
        ObservableList<String> options = FXCollections.observableArrayList("Mahasiswa", "Matakuliah");
        option.setItems(options);
        option.setValue("Mahasiswa");

        option.valueProperty().addListener((observable, oldValue, newValue) -> {
            table.getItems().clear();

            if ("Matakuliah".equals(newValue)) {
                linechart.setVisible(true);
                column1.setText("kode_mk");
                column1.setCellValueFactory(new PropertyValueFactory<>("kode_mk"));
                column2.setText("nama");
                column2.setCellValueFactory(new PropertyValueFactory<>("nama"));


                column3.setText("sks");
                column3.setCellValueFactory(new PropertyValueFactory<>("sks"));

                table.setItems(FXCollections.observableArrayList(mtkl_table.fetch_all_matkul()));
            } else {
                linechart.setVisible(false);
                column1.setText("NIM");
                column1.setCellValueFactory(new PropertyValueFactory<>("NIM"));
                column2.setText("nama");
                column2.setCellValueFactory(new PropertyValueFactory<>("nama"));

                column3.setText(" ");
                column3.setCellValueFactory(null);

                table.setItems(FXCollections.observableArrayList(mhs_table.fetch_all_mahasiswa()));
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {

                if (newSelection instanceof Mahasiswa) {
                    Mahasiswa m = (Mahasiswa) newSelection;
                    System.out.println("Clicked Mahasiswa: " + m.getNama() + " (" + m.getNIM() + ")");

                    update_barchart("nim",m.getNIM());
                    update_piechart("nim",m.getNIM());


                } else if (newSelection instanceof Matakuliah) {
                    Matakuliah m = (Matakuliah) newSelection;
                    System.out.println("Clicked Mahasiswa: " + m.getNama() + " (" + m.getKode_mk() + ")");

                    update_barchart("kode_mk",m.getKode_mk());
                    update_piechart("kode_mk",m.getKode_mk());
                    update_linechart(m.getKode_mk());
                }
            }
        });

        linechart.setVisible(false);
        column1.setText("NIM");
        column1.setCellValueFactory(new PropertyValueFactory<>("NIM"));
        column2.setText("nama");
        column2.setCellValueFactory(new PropertyValueFactory<>("nama"));
        column3.setText(" ");

        ObservableList<Object> data = FXCollections.observableArrayList(mhs_table.fetch_all_mahasiswa());
        table.setItems(data);

    }

    public void update_barchart(String target_col,String val) {
        // TODO: buat barchart menampilkan seberapa banyak nilai A,A-,B+,...
        // method ini dapat di gunakan di 2 situasi yaitu nilai berdasarkan nim mahasiswa dan berdasarkan kode matakuliah
        // ambil data dari attribute nilai_table
        // tips: target_col merujuk pada nama kolom di datbase sedangkan val adalah value yang di cari dari kolom tersebut misal:
        // target_col -> nim, val -> 71200001, maka kita mencari 71200001 di kolom nim
        barchart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Distribusi Nilai");

        int a = 0;
        int aMin = 0;
        int bPlus = 0;
        int b = 0;
        int bMin = 0;
        int cPlus = 0;
        int c = 0;
        int d = 0;
        int e = 0;

        for (Nilai n : nilai_table.fetch_all_nilai()) {
            boolean isMatch = false;
            if (target_col.equalsIgnoreCase("nim") && n.getNIM().equals(val)) {
                isMatch = true;
            } else if (target_col.equalsIgnoreCase("kode_mk") && n.getKode_mk().equals(val)) {
                isMatch = true;
            }

            if (isMatch) {
                switch (n.getNilai()) {
                    case "A": a++;
                    break;
                    case "A-": aMin++;
                    break;
                    case "B+": bPlus++;
                    break;
                    case "B": b++;
                    break;
                    case "B-": bMin++;
                    break;
                    case "C+": cPlus++;
                    break;
                    case "C": c++;
                    break;
                    case "D": d++;
                    break;
                    case "E": e++;
                    break;
                }
            }
        }

        series.getData().add(new XYChart.Data<>("A", a));
        series.getData().add(new XYChart.Data<>("A-", aMin));
        series.getData().add(new XYChart.Data<>("B+", bPlus));
        series.getData().add(new XYChart.Data<>("B", b));
        series.getData().add(new XYChart.Data<>("B-", bMin));
        series.getData().add(new XYChart.Data<>("C+", cPlus));
        series.getData().add(new XYChart.Data<>("C", c));
        series.getData().add(new XYChart.Data<>("D", d));
        series.getData().add(new XYChart.Data<>("E", e));

        barchart.getData().add(series);
    }

    public void update_linechart(String kode_mk) {
        // TODO: buatlah linechart yang menggambarkan nilai mean dari setiap angkatan
        // angkatan dapat di ambil dengan cara getAngkatan() pada entity Mahasiswa
        // tips: fetch dulu entity mahasiswa menggunakan fetch_mahasiswa_by_nim() di mhs_tabel menggunakan nim pada nilai_table
        linechart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Rata-rata Nilai per Angkatan");

        java.util.Map<String, Double> totalBobotPerAngkatan = new java.util.TreeMap<>();
        java.util.Map<String, Integer> jumlahMhsPerAngkatan = new java.util.TreeMap<>();

        for (Nilai n : nilai_table.fetch_all_nilai()) {
            if (n.getKode_mk().equals(kode_mk)) {
                Mahasiswa mhs = mhs_table.fetch_mahasiswa_by_nim(n.getNIM());
                if (mhs != null) {
                    String angkatan = String.valueOf(mhs.getAngkatan());
                    double bobot = 0.0;

                    switch (n.getNilai()) {
                        case "A": bobot = 4.0;
                        break;
                        case "A-": bobot = 3.7;
                        break;
                        case "B+": bobot = 3.3;
                        break;
                        case "B": bobot = 3.0;
                        break;
                        case "B-": bobot = 2.7;
                        break;
                        case "C+": bobot = 2.3;
                        break;
                        case "C": bobot = 2.0;
                        break;
                        case "D": bobot = 1.0;
                        break;
                        case "E": bobot = 0.0;
                        break;
                    }

                    totalBobotPerAngkatan.put(angkatan, totalBobotPerAngkatan.getOrDefault(angkatan, 0.0) + bobot);
                    jumlahMhsPerAngkatan.put(angkatan, jumlahMhsPerAngkatan.getOrDefault(angkatan, 0) + 1);
                }
            }
        }

        for (String angkatan : totalBobotPerAngkatan.keySet()) {
            double mean = totalBobotPerAngkatan.get(angkatan) / jumlahMhsPerAngkatan.get(angkatan);
            series.getData().add(new XYChart.Data<>(angkatan, mean));
        }

        linechart.getData().add(series);
    }



    public void update_piechart(String target_col, String val) {

        piechart.getData().clear();

        int a = 0;
        int aMin = 0;
        int bPlus = 0;
        int b = 0;
        int bMin = 0;
        int cPlus = 0;
        int c = 0;
        int d = 0;
        int e = 0;

        for (Nilai n : nilai_table.fetch_all_nilai()) {
            boolean isMatch = false;

            if (target_col.equalsIgnoreCase("nim") && n.getNIM().equals(val)) {
                isMatch = true;
            } else if (target_col.equalsIgnoreCase("kode_mk") && n.getKode_mk().equals(val)) {
                isMatch = true;
            }

            if (isMatch) {
                switch (n.getNilai()) {
                    case "A": a++;
                    break;
                    case "A-": aMin++;
                    break;
                    case "B+": bPlus++;
                    break;
                    case "B": b++;
                    break;
                    case "B-": bMin++;
                    break;
                    case "C+": cPlus++;
                    break;
                    case "C": c++;
                    break;
                    case "D": d++;
                    break;
                    case "E": e++; break;
                }
            }
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        if (a > 0) pieChartData.add(new PieChart.Data("A (" + a + ")", a));
        if (aMin > 0) pieChartData.add(new PieChart.Data("A- (" + aMin + ")", aMin));
        if (bPlus > 0) pieChartData.add(new PieChart.Data("B+ (" + bPlus + ")", bPlus));
        if (b > 0) pieChartData.add(new PieChart.Data("B (" + b + ")", b));
        if (bMin > 0) pieChartData.add(new PieChart.Data("B- (" + bMin + ")", bMin));
        if (cPlus > 0) pieChartData.add(new PieChart.Data("C+ (" + cPlus + ")", cPlus));
        if (c > 0) pieChartData.add(new PieChart.Data("C (" + c + ")", c));
        if (d > 0) pieChartData.add(new PieChart.Data("D (" + d + ")", d));
        if (e > 0) pieChartData.add(new PieChart.Data("E (" + e + ")", e));

        piechart.setData(pieChartData);
    }
}
