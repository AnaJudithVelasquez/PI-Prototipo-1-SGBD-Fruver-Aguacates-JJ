import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Productos extends JFrame {
    private JPanel panelProductos;
    private JTextField Producto1;
    private JTextField NombreProducto1;
    private JTextField Compra1;
    private JTextField Descripcion1;
    private JTextField PrecioProducto1;
    private JTextField PrecioCompra1;
    private JTextField Cantidad1;
    private JButton agregarButton;
    private JButton eliminarButton;
    private JButton modificarButton;
    private JButton mostrarButton;
    private JTable table1;
    private JButton volverButton;
    private JLabel COD_PRODUCT;
    private JLabel COD_PURCHASE;
    private JLabel PRODUCT_NAME;
    private JLabel QUANTITY_Kg;
    private JLabel PURCHASE_VALUE;
    private JLabel PRODUCT_PRICE;
    private JLabel DESCRIPTION_PRODUCT_STATUS;
    private JScrollPane scrollPane;
    private JTable table2;
    private JScrollPane scrollPaneCP;
    private JButton listaComprasButton;
    Connection conexion;
    PreparedStatement ps;
    ResultSet rs;

    public Productos() {
        setSize(600, 600);
        setLocationRelativeTo(null);

        Compra1.setEditable(false);
        NombreProducto1.setEditable(false);
        PrecioCompra1.setEditable(false);
        Producto1.setEditable(false);
        Compra1.setEditable(false);

        mostrarDatos();
        mostrarDatosCompra();

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                regresar();
            }
        });

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregar();
            }
        });

        modificarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificar();
            }
        });

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminar();
            }
        });

        mostrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDatos();
            }
        });


        table2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table2.getSelectedRow();
                if (row != -1) {
                    Compra1.setText(table2.getValueAt(row, 0).toString());
                    NombreProducto1.setText(table2.getValueAt(row, 4).toString());
                    PrecioCompra1.setText(table2.getValueAt(row, 6).toString());

                }
            }
        });
    }

    void conectar() {
        try {
            conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/FruverAguacates", "root", "10j56yeyo");
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar a la base de datos", e);
        }
    }

    void regresar() {
        Fruver_Aguacates_JJ enlace = new Fruver_Aguacates_JJ();
        enlace.mostrarVentanaFruver_Aguacates_JJ();
    }

    void agregar() {
        int codProductoGenerado = -1;
        conectar();
        String sql = "INSERT INTO PRODUCTS (COD_PURCHASE, PRODUCT_NAME, QUANTITY_Kg, PURCHASE_VALUE, PRODUCT_PRICE, DESCRIPTION_PRODUCT_STATUS) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            String COD_PURCHASE = Compra1.getText();
            String PRODUCT_NAME = NombreProducto1.getText();
            String QUANTITY_Kg = Cantidad1.getText();
            String PURCHASE_VALUE = PrecioCompra1.getText();
            String PRODUCT_PRICE = PrecioProducto1.getText();
            String DESCRIPTION_PRODUCT_STATUS = Descripcion1.getText();

            ps.setString(1, COD_PURCHASE);
            ps.setString(2, PRODUCT_NAME);
            ps.setString(3, QUANTITY_Kg);
            ps.setString(4, PURCHASE_VALUE);
            ps.setString(5, PRODUCT_PRICE);
            ps.setString(6, DESCRIPTION_PRODUCT_STATUS);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                codProductoGenerado = rs.getInt(1);
                Producto1.setText(String.valueOf(codProductoGenerado));
            } else {
                throw new SQLException("Error al obtener el COD_PRODUCT generado.");
            }

            JOptionPane.showMessageDialog(null, "Los datos se insertaron correctamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Hay un error al insertar datos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    void modificar() {
        conectar();
        String sql = "update PRODUCTS set COD_PURCHASE = ?, PRODUCT_NAME = ?, QUANTITY_Kg = ?, PURCHASE_VALUE = ?, PRODUCT_PRICE = ?, DESCRIPTION_PRODUCT_STATUS = ? WHERE COD_PRODUCT = ?";

        String COD_PRODUCT = Producto1.getText();
        String COD_PURCHASE = Compra1.getText();
        String PRODUCT_NAME = NombreProducto1.getText();
        String QUANTITY_Kg = Cantidad1.getText();
        String PURCHASE_VALUE = PrecioCompra1.getText();
        String PRODUCT_PRICE = PrecioProducto1.getText();
        String DESCRIPTION_PRODUCT_STATUS = Descripcion1.getText();

        try {
            ps = conexion.prepareStatement(sql);

            ps.setString(1, COD_PURCHASE);
            ps.setString(2, PRODUCT_NAME);
            ps.setString(3, QUANTITY_Kg);
            ps.setString(4, PURCHASE_VALUE);
            ps.setString(5, PRODUCT_PRICE);
            ps.setString(6, DESCRIPTION_PRODUCT_STATUS);
            ps.setString(7, COD_PRODUCT);

            int filasModificadas = ps.executeUpdate();
            if (filasModificadas > 0) {
                JOptionPane.showMessageDialog(null, "Los datos se actualizaron correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encuentra el producto con el código que especificastes.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Hay un error al actualizar los datos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    void eliminar() {
        conectar();
        String sql = "delete from PRODUCTS where COD_PRODUCT = ?";

        String COD_PRODUCT = Producto1.getText();

        try {
            ps = conexion.prepareStatement(sql);
            ps.setString(1, COD_PRODUCT);

            int filasEliminadas = ps.executeUpdate();
            if (filasEliminadas > 0) {
                JOptionPane.showMessageDialog(null, "El producto se eliminó correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el producto con el código que especificastes.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Hay un error al eliminar el producto: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    void mostrarDatos() {
        conectar();
        String sql = "select * from PRODUCTS";

        try {
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();
            DefaultTableModel model = (DefaultTableModel) table1.getModel();
            model.setRowCount(0);


            if (model.getColumnCount() == 0) {
                model.addColumn("Cod_Producto");
                model.addColumn("Cod_Compra");
                model.addColumn("Nombre_Producto");
                model.addColumn("Cantidad_Kg");
                model.addColumn("Valor_Compra");
                model.addColumn("Precio_Producto");
                model.addColumn("Descripcion");
            }

            while (rs.next()) {
                Object[] fila = new Object[7];
                fila[0] = rs.getString("COD_PRODUCT");
                fila[1] = rs.getString("COD_PURCHASE");
                fila[2] = rs.getString("PRODUCT_NAME");
                fila[3] = rs.getString("QUANTITY_Kg");
                fila[4] = rs.getString("PURCHASE_VALUE");
                fila[5] = rs.getString("PRODUCT_PRICE");
                fila[6] = rs.getString("DESCRIPTION_PRODUCT_STATUS");

                model.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Hay un error al mostrar los datos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    void mostrarDatosCompra() {
        conectar();
        String sqlPURCHASE = "select p.COD_PURCHASE, p.COD_ADMINISTRATOR, p.COD_SUPPLIER, p.DATE_PURCHASE, p.TOTAL_PURCHASE_VALUE, pd.COD_PURCHASE, pd.PURCHASED_PRODUCT, pd.UNIT_VALUE, pd.QUANTITY_Kg, pd.TOTAL_PRODUCT from PURCHASES p join PURCHASES_DETAILS pd on p.COD_PURCHASE = pd.COD_PURCHASE";

        try {
            ps = conexion.prepareStatement(sqlPURCHASE);
            rs = ps.executeQuery();
            DefaultTableModel model = (DefaultTableModel) table2.getModel();
            model.setRowCount(0);

            if (model.getColumnCount() == 0) {
                model.addColumn("Cod_Compra");
                model.addColumn("Cod_Administrador");
                model.addColumn("Cod_Proveedor");
                model.addColumn("Fecha_Compra");
                model.addColumn("Producto_Comprado");
                model.addColumn("Cantidad_Producto_Kg");
                model.addColumn("Valor_Unitario");
                model.addColumn("Total_Producto");
                model.addColumn("Total_Compra");
            }

            while (rs.next()) {
                Object[] fila = new Object[9];
                fila[0] = rs.getString("COD_PURCHASE");
                fila[1] = rs.getString("COD_ADMINISTRATOR");
                fila[2] = rs.getString("COD_SUPPLIER");
                fila[3] = rs.getString("DATE_PURCHASE");
                fila[4] = rs.getString("PURCHASED_PRODUCT");
                fila[5] = rs.getString("QUANTITY_Kg");
                fila[6] = rs.getString("UNIT_VALUE");
                fila[7] = rs.getString("TOTAL_PRODUCT");
                fila[8] = rs.getString("TOTAL_PURCHASE_VALUE");

                model.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Hay un error al mostrar los datos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null)ps.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void mostrarVentanaProductos() {
        Productos Productos1 = new Productos();
        Productos1.setContentPane(new Productos().panelProductos);
        Productos1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Productos1.setVisible(true);
        Productos1.pack();

    }
}
