package controller.ui;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import service.StatisticsService;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DashboardStatsController {

    private final StatisticsService statisticsService;

    @FXML
    private Label totalBooksLabel;
    @FXML
    private Label availableBooksLabel;
    @FXML
    private Label totalMembersLabel;
    @FXML
    private Label activeMembersLabel;
    @FXML
    private Label currentBorrowsLabel;
    @FXML
    private Label lateBorrowsLabel;
    @FXML
    private Label totalFinesLabel;

    @FXML
    private BarChart<String, Number> borrowsChart;
    @FXML
    private PieChart categoryChart;

    @FXML
    public void initialize() {
        refreshStats();
    }

    @FXML
    private void handleRefresh() {
        refreshStats();
    }

    public void refreshStats() {
        Map<String, Object> globalStats = statisticsService.getGlobalStats();

        totalBooksLabel.setText(String.valueOf(globalStats.get("totalBooks")));
        availableBooksLabel.setText(globalStats.get("availableBooks") + " disponibles");
        totalMembersLabel.setText(String.valueOf(globalStats.get("totalMembers")));
        activeMembersLabel.setText(globalStats.get("activeMembers") + " actifs");
        currentBorrowsLabel.setText(String.valueOf(globalStats.get("currentBorrows")));
        lateBorrowsLabel.setText(String.valueOf(globalStats.get("lateBorrows")));
        totalFinesLabel.setText(String.format("%.2f € d'amendes", globalStats.get("totalFines")));

        // Load Bar Chart Data
        borrowsChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Emprunts");
        Map<String, Long> borrowsByMonth = statisticsService.getBorrowsByMonth();
        borrowsByMonth.forEach((month, count) -> {
            XYChart.Data<String, Number> data = new XYChart.Data<>(month, count);
            series.getData().add(data);
        });
        borrowsChart.getData().add(series);

        // Add tooltips to BarChart
        javafx.application.Platform.runLater(() -> {
            for (XYChart.Series<String, Number> s : borrowsChart.getData()) {
                for (XYChart.Data<String, Number> d : s.getData()) {
                    if (d.getNode() != null) {
                        javafx.scene.control.Tooltip.install(d.getNode(),
                                new javafx.scene.control.Tooltip(d.getXValue() + ": " + d.getYValue() + " emprunts"));
                    }
                }
            }
        });

        // Load Pie Chart Data
        categoryChart.getData().clear();
        Map<String, Long> categoryDist = statisticsService.getBookDistributionByCategory();

        if (categoryDist.isEmpty()) {
            categoryChart.getData().add(new PieChart.Data("Aucune donnée", 1));
        } else {
            categoryDist.forEach((cat, count) -> {
                PieChart.Data data = new PieChart.Data(cat, (double) count);
                categoryChart.getData().add(data);
            });

            // Add tooltips to PieChart
            javafx.application.Platform.runLater(() -> {
                for (PieChart.Data data : categoryChart.getData()) {
                    if (data.getNode() != null) {
                        javafx.scene.control.Tooltip.install(data.getNode(),
                                new javafx.scene.control.Tooltip(
                                        data.getName() + ": " + (int) data.getPieValue() + " livres"));
                    }
                }
            });
        }

        categoryChart.setLabelsVisible(true);
        categoryChart.setLegendVisible(true);
        categoryChart.setLegendSide(javafx.geometry.Side.BOTTOM);
        borrowsChart.setLegendVisible(true);
    }
}
