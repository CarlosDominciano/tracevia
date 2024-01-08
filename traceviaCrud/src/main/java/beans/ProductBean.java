package beans;

import models.Product;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class ProductBean {

    private Product product = new Product();
    private Part image;
    private List<Product> productList;

    public Product findProduct(long productId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tracevia", "root", "SENHA");
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM tb_products WHERE id = ?");
            statement.setLong(1, productId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                String description = resultSet.getString("description");
                byte[] image = resultSet.getBytes("image");

                return new Product(id, name, description, price, image);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public byte[] processFileUpload() throws IOException {
        try (InputStream inputStream = image.getInputStream()) {
            byte[] contents = inputStream.readAllBytes();
            return contents;
        }
    }

    public void editProduct(long productId) {
        product = findProduct(productId);

        if (product != null) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().getFlash().get("product");
                FacesContext.getCurrentInstance().getExternalContext().redirect("editProduct.xhtml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean save() throws Exception {
        int result = 0;
        try {
            byte[] imageData = processFileUpload();

            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tracevia", "root", "SENHA");
            PreparedStatement statement = connection.prepareStatement("INSERT INTO tb_products(name, price, description, image) VALUES(?,?,?,?)");
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice());
            statement.setString(3, product.getDescription());
            statement.setBytes(4, imageData);
            result = statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result == 1;
    }

    public List<Product> getAllProducts() {
        productList = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tracevia", "root", "SENHA");
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM tb_products");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                String description = resultSet.getString("description");
                byte[] image = resultSet.getBytes("image");

                Product product = new Product(id, name, description, price, image);
                productList.add(product);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return productList;
    }

    public String deleteProduct(long productId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tracevia", "root", "SENHA");
            PreparedStatement statement = connection.prepareStatement("DELETE FROM tb_products WHERE id = ?");
            statement.setLong(1, productId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "products.xhtml?faces-redirect=true";
    }

    public String redirectToProducts() {
        return "products.xhtml";
    }


    public String redirectToCreatePage() {
        return "newProduct.xhtml";
    }

    public String submit() throws Exception {
        return save() ? "products.xhtml" : "newProduct.xhtml";
    }

    // Getters e setters

    public List<Product> getProductList() {
        return productList;
    }

    public Part getImage() {
        return image;
    }

    public void setImage(Part image) {
        this.image = image;
    }

    public Product getProduct() {
        return product;
    }


    public void setProduct(Product selectedProduct) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("product", selectedProduct);
    }
}