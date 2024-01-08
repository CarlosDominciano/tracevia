package beans;

import models.Product;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@ManagedBean
@ViewScoped
public class EditProductBean implements Serializable {
    Product product;

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

    @PostConstruct
    public void init() {
        String productId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        product = findProduct(Long.parseLong(productId));
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public boolean update() throws Exception {
        int result = 0;
        try {

            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tracevia", "root", "SENHA");
            PreparedStatement statement = connection.prepareStatement("UPDATE tb_products SET name=?, price=?, description=? WHERE id=?");
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice());
            statement.setString(3, product.getDescription());
            statement.setLong(4, product.getId());

            result = statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result == 1;
    }


    public String submit() throws Exception {
        return update() ? "products.xhtml?faces-redirect=true" : "editProduct.xhtml";
    }
}