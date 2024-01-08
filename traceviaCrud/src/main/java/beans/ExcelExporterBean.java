package beans;
import models.Product;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.swing.filechooser.FileSystemView;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@RequestScoped
public class ExcelExporterBean {

    public void exportToExcel(String tableName) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tracevia", "root", "SENHA")) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);

            List<Product> productList = convertResultSetToProductList(resultSet);

            HSSFWorkbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet(tableName);

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Description");
            headerRow.createCell(3).setCellValue("Price");

            int rowNumber = 1;
            for (Product product : productList) {
                Row row = sheet.createRow(rowNumber++);
                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(product.getDescription());
                row.createCell(3).setCellValue(product.getPrice());
            }

            FileSystemView fileSystemView = FileSystemView.getFileSystemView();
            String downloadDirectory = fileSystemView.getDefaultDirectory().getPath();

            try (FileOutputStream fileOut = new FileOutputStream(downloadDirectory +"\\" + tableName + ".xls")) {
                workbook.write(fileOut);
            }

            System.out.println("Diretório de Downloads: " + downloadDirectory);

            workbook.close();

            System.out.println("Excel criado com sucesso!");
            alert();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void alert() {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        String downloadDirectory = fileSystemView.getDefaultDirectory().getPath();
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Alerta!", "Excel downloaded at " + downloadDirectory));
    }

    public String sourceDirectory() {
        return FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
    }

    private List<Product> convertResultSetToProductList(ResultSet resultSet) throws SQLException {
        List<Product> productList = new ArrayList<>();
        while (resultSet.next()) {
            Product product = new Product();
            product.setId(resultSet.getLong("id"));
            product.setName(resultSet.getString("name"));
            product.setDescription(resultSet.getString("description"));
            product.setPrice(resultSet.getDouble("price"));
            productList.add(product);
        }
        return productList;
    }
}