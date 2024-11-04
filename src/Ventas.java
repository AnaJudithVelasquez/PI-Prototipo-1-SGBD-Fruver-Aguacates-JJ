import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Ventas extends JFrame {
    private JPanel panelVentas;
    private JTable table1;
    private JButton agregarProductoButton;
    private JTable table2;
    private JTextField Cod_Venta;
    private JTextField Cod_Empleado;
    private JTextField Cod_Producto;
    private JTextField Cantidad;
    private JTextField Precio_Producto;
    private JTextField Total_Por_Producto;
    private JTextField Nombre_Producto;
    private JTextField Total_Venta;
    private JTextField Identificacion_Cliente;
    private JTextField Fecha_Venta;
    private JLabel COD_SALE;
    private JLabel COD_EMPLOYEE;
    private JLabel DATE_SALE;
    private JLabel COSTUMER_IDENTIFICATION;
    private JLabel COD_PRODUCT;
    private JLabel PRODUCT_NAME;
    private JLabel PRODUCT_QUANTITY_Kg;
    private JLabel PRODUCT_PRICE;
    private JLabel TOTAL_PRODUCT;
    private JLabel TOTAL_SALE_VALUE;
    private JButton agregarVentaButton;
    private JButton mostrarButton;
    private JButton listaProductosButton;
    private JButton volverButton;
    private JScrollPane scrollPane;
    private JScrollPane scrollPane2;
    Connection conexion;
    PreparedStatement ps;
    ResultSet rs;

    List<ProductoDetalle> productosVenta = new ArrayList<>();

    public Ventas() {
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Cod_Producto.setEditable(false);
        Nombre_Producto.setEditable(false);
        Precio_Producto.setEditable(false);
        Total_Por_Producto.setEditable(false);
        Total_Venta.setEditable(false);
        Cod_Venta.setEditable(false);

        mostrarDatos();
        mostrarProductos();

        agregarProductoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarProducto();
                actualizarTotalVenta();
            }
        });

        agregarVentaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarVenta();
            }
        });

        Cantidad.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calcularTotalPorProducto();
            }
        });

        Precio_Producto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calcularTotalPorProducto();
            }
        });

        mostrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDatos();
            }
        });



        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                regresar();
            }
        });


        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) {
                    Cod_Producto.setText(table1.getValueAt(selectedRow, 0).toString());
                    Nombre_Producto.setText(table1.getValueAt(selectedRow, 2).toString());
                    Precio_Producto.setText(table1.getValueAt(selectedRow, 5).toString());

                }
            }
        });


    }

    class ProductoDetalle {
        String COD_PRODUCT;
        String PRODUCT_NAME;
        String QUANTITY_Kg;
        String PRODUCT_PRICE;
        String TOTAL_PRODUCT;

        public ProductoDetalle(String COD_PRODUCT, String PRODUCT_NAME, String QUANTITY_Kg, String PRODUCT_PRICE, String TOTAL_PRODUCT) {
            this.COD_PRODUCT = COD_PRODUCT;
            this.PRODUCT_NAME = PRODUCT_NAME;
            this.QUANTITY_Kg = QUANTITY_Kg;
            this.PRODUCT_PRICE = PRODUCT_PRICE;
            this.TOTAL_PRODUCT = TOTAL_PRODUCT;
        }
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

    void agregarProducto() {
        conectar();
        String COD_PRODUCT = Cod_Producto.getText().trim();
        String PRODUCT_NAME = Nombre_Producto.getText();
        String PRODUCT_QUANTITY_Kg = Cantidad.getText().trim();
        String PRODUCT_PRICE = Precio_Producto.getText().trim();
        String TOTAL_PRODUCT = Total_Por_Producto.getText().trim().replace(",", ".");

        ProductoDetalle producto = new ProductoDetalle(COD_PRODUCT, PRODUCT_NAME, PRODUCT_QUANTITY_Kg, PRODUCT_PRICE, TOTAL_PRODUCT);
        productosVenta.add(producto);

        JOptionPane.showMessageDialog(null, "Producto agregado a la venta.");
    }

    void agregarVenta() {
        conectar();
        int codVentaGenerado = -1;
        try {
            conexion.setAutoCommit(false);

            String sqlSales = "INSERT INTO SALES (COD_EMPLOYEE, DATE_SALE, COSTUMER_IDENTIFICATION, TOTAL_SALE_VALUE) VALUES (?, ?, ?, ?)";
            ps = conexion.prepareStatement(sqlSales, Statement.RETURN_GENERATED_KEYS);

            String COD_EMPLOYEE = Cod_Empleado.getText().trim();
            String DATE_SALE = Fecha_Venta.getText();
            String COSTUMER_IDENTIFICATION = Identificacion_Cliente.getText().trim();
            String TOTAL_SALE_VALUE = Total_Venta.getText().trim().replace(",", ".");

            ps.setString(1, COD_EMPLOYEE);
            ps.setString(2, DATE_SALE);
            ps.setString(3, COSTUMER_IDENTIFICATION);
            ps.setString(4, TOTAL_SALE_VALUE);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                codVentaGenerado = rs.getInt(1);
                Cod_Venta.setText(String.valueOf(codVentaGenerado));
            } else {
                throw new SQLException("Error al obtener el COD_SALE generado.");
            }

            String sqlSales_Details = "INSERT INTO SALES_DETAILS (COD_SALE, COD_PRODUCT, PRODUCT_NAME, PRODUCT_QUANTITY_Kg, PRODUCT_PRICE, TOTAL_PRODUCT) VALUES (?, ?, ?, ?, ?, ?)";
            ps = conexion.prepareStatement(sqlSales_Details);

            for (ProductoDetalle producto : productosVenta) {
                ps.setInt(1, codVentaGenerado);
                ps.setString(2, producto.COD_PRODUCT);
                ps.setString(3, producto.PRODUCT_NAME);
                ps.setString(4, producto.QUANTITY_Kg);
                ps.setString(5, producto.PRODUCT_PRICE);
                ps.setString(6, producto.TOTAL_PRODUCT);
                ps.addBatch();
            }

            int[] filasInsertadas = ps.executeBatch();

            if (filasInsertadas.length > 0) {
                conexion.commit();
                JOptionPane.showMessageDialog(null, "La venta y los productos se insertaron correctamente.");
                productosVenta.clear();
            } else {
                throw new SQLException("No se pudieron insertar los productos en la venta.");
            }

        } catch (SQLException e) {
            try {
                if (conexion != null) {
                    conexion.rollback();
                }
                JOptionPane.showMessageDialog(null, "Hay un error al insertar datos: " + e.getMessage());
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                if (ps != null) ps.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    void calcularTotalPorProducto() {
        try {
            double cantidad = Double.parseDouble(Cantidad.getText().trim());
            double precio = Double.parseDouble(Precio_Producto.getText().trim());
            double totalProducto = cantidad * precio;
            Total_Por_Producto.setText(String.format("%.2f", totalProducto));
        } catch (NumberFormatException ignored) {
        }
    }

    void actualizarTotalVenta() {
        double totalVenta = 0;
        for (ProductoDetalle producto : productosVenta) {
            try {
                double totalProducto = Double.parseDouble(producto.TOTAL_PRODUCT.replace(",", "."));
                totalVenta += totalProducto;
            } catch (NumberFormatException e) {
                System.out.println("Error al parsear el total del producto: " + producto.TOTAL_PRODUCT);
            }
        }
        Total_Venta.setText(String.format("%.2f", totalVenta));
        System.out.println("Total de venta actualizado: " + totalVenta);
    }

    void mostrarDatos() {
        conectar();
        String sqlSALES = "SELECT s.COD_SALE, s.COD_EMPLOYEE, s.DATE_SALE, s.COSTUMER_IDENTIFICATION, s.TOTAL_SALE_VALUE, sd.COD_PRODUCT, sd.PRODUCT_NAME, sd.PRODUCT_QUANTITY_Kg, sd.PRODUCT_PRICE, sd.TOTAL_PRODUCT " +
                "FROM SALES s JOIN SALES_DETAILS sd ON s.COD_SALE = sd.COD_SALE";

        try {
            ps = conexion.prepareStatement(sqlSALES);
            rs = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) table2.getModel();
            model.setRowCount(0);

            if (model.getColumnCount() == 0) {
                model.addColumn("Cod_Venta");
                model.addColumn("Cod_Empleado");
                model.addColumn("Fecha_Venta");
                model.addColumn("Identificacion_Cliente");
                model.addColumn("Cod_Producto");
                model.addColumn("Nombre_Producto ");
                model.addColumn("Cantidad_Producto_Kg");
                model.addColumn("Precio_Producto");
                model.addColumn("Total_Producto");
                model.addColumn("Total_Venta");
            }

            while (rs.next()) {
                Object[] fila = new Object[10];
                fila[0] = rs.getString("COD_SALE");
                fila[1] = rs.getString("COD_EMPLOYEE");
                fila[2] = rs.getString("DATE_SALE");
                fila[3] = rs.getString("COSTUMER_IDENTIFICATION");
                fila[4] = rs.getString("COD_PRODUCT");
                fila[5] = rs.getString("PRODUCT_NAME");
                fila[6] = rs.getString("PRODUCT_QUANTITY_Kg");
                fila[7] = rs.getString("PRODUCT_PRICE");
                fila[8] = rs.getString("TOTAL_PRODUCT");
                fila[9] = rs.getString("TOTAL_SALE_VALUE");

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

    void mostrarProductos() {
        conectar();
        String sqlProducts = "SELECT * FROM PRODUCTS";

        try {
            ps = conexion.prepareStatement(sqlProducts);
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
            JOptionPane.showMessageDialog(null, "Hay un error al mostrar los datos de productos: " + e.getMessage());
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

    public static void mostrarVentanaVentas() {
        Ventas ventasFrame = new Ventas();
        ventasFrame.setContentPane(new Ventas().panelVentas);
        ventasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventasFrame.setVisible(true);
        ventasFrame.pack();
    }

}
