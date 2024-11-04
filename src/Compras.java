import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Compras extends JFrame {
    private JButton agregarProductoButton;
    private JButton agregarCompraButton;
    private JButton agregarButton;
    private JButton modificarButton;
    private JButton eliminarButton;
    private JButton mostrarButton;
    private JTextField Nombre_Proveedor;
    private JTextField Direccion;
    private JTextField Telefono;
    private JTextField Cod_Compra;
    private JTextField Cod_Administrador;
    private JTextField Fecha_Compra;
    private JTextField Producto_Comprado;
    private JTextField Cantidad_Producto_Kg;
    private JTextField Valor_Unitario;
    private JTextField Total_Producto;
    private JTextField Total_Compra;
    private JLabel COD_SUPPLIER;
    private JLabel SUPPLIER_NAME;
    private JLabel ADDRESS;
    private JLabel PHONE_NUMBER;
    private JTable table1P;
    private JScrollPane scrollPane1P;
    private JLabel COD_PURCHASE;
    private JLabel COD_ADMINISTRATOR;
    private JLabel COD_SUPPLIERC;
    private JLabel DATE_PURCHASE;
    private JLabel PURCHASED_PRODUCT;
    private JLabel QUANTITY_Kg;
    private JLabel UNIT_VALUE;
    private JLabel TOTAL_PRODUCT;
    private JLabel TOTAL_PURCHASE_VALUE;
    private JTable table1;
    private JTextField Cod_Proveedor;
    private JTextField Cod_ProveedorC;
    private JPanel panelCompras;
    private JButton mostrarComprasButton;
    private JButton volverButton;
    private JScrollPane scrollPane;
    Connection conexion;
    PreparedStatement ps;
    ResultSet rs;

    List<Compras.CompraDetalle> productosCompra = new ArrayList<>();

    public Compras() {
        Cod_ProveedorC.setEditable(false);
        Cod_Proveedor.setEditable(false);
        Cod_Compra.setEditable(false);
        Cod_ProveedorC.setEditable(false);
        Total_Producto.setEditable(false);
        Total_Compra.setEditable(false);

        mostrarDatos();
        mostrarDatosCompra();

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarP();
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

        agregarProductoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarArticulo();
                actualizarTotalCompra();
            }
        });

        agregarCompraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarCompra();
            }
        });

        Cantidad_Producto_Kg.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calcularTotalPorProducto();
            }
        });

        Valor_Unitario.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calcularTotalPorProducto();
            }
        });

        mostrarComprasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDatosCompra();
            }
        });

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                regresar();
            }
        });


        table1P.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table1P.getSelectedRow() != -1) {
                    Cod_ProveedorC.setText(table1P.getValueAt(table1P.getSelectedRow(), 0).toString());

                }
            }
        });
    }

    class CompraDetalle {
        String COD_PURCHASE;
        String PURCHASED_PRODUCT;
        String UNIT_VALUE;
        String QUANTITY_Kg;
        String TOTAL_PRODUCT;

        public CompraDetalle(String COD_PURCHASE, String PURCHASED_PRODUCT, String UNIT_VALUE, String QUANTITY_Kg, String TOTAL_PRODUCT) {
            this.COD_PURCHASE = COD_PURCHASE;
            this.PURCHASED_PRODUCT = PURCHASED_PRODUCT;
            this.UNIT_VALUE = UNIT_VALUE;
            this.QUANTITY_Kg = QUANTITY_Kg;
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

    void agregarP(){
        conectar();
        int codProveedorGenerado = -1;

        String sql = "INSERT INTO SUPPLIERS (SUPPLIER_NAME, ADDRESS, PHONE_NUMBER) VALUES (?, ?, ?)";

        try {
            ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            String SUPPLIER_NAME = Nombre_Proveedor.getText();
            String ADDRESS = Direccion.getText();
            String PHONE_NUMBER = Telefono.getText();


            ps.setString(1, SUPPLIER_NAME);
            ps.setString(2, ADDRESS);
            ps.setString(3, PHONE_NUMBER);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();


            if (rs.next()) {
                codProveedorGenerado = rs.getInt(1);
                Cod_Proveedor.setText(String.valueOf(codProveedorGenerado));
            } else {
                throw new SQLException("Error al obtener el COD_SUPPLIER generado.");
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
        String sql = "update PURCHASES set COD_SUPPLIER = ?, SUPPLIER_NAME = ?, ADDRESS = ?, PHONE_NUMBER = ?";

        String COD_SUPPLIER = Cod_Proveedor.getText();
        String SUPPLIER_NAME = Nombre_Proveedor.getText();
        String ADDRESS = Direccion.getText();
        String PHONE_NUMBER = Telefono.getText();

        try {
            ps = conexion.prepareStatement(sql);

            ps.setString(1, COD_SUPPLIER);
            ps.setString(2, SUPPLIER_NAME);
            ps.setString(3, ADDRESS);
            ps.setString(4, PHONE_NUMBER);


            int filasModificadas = ps.executeUpdate();
            if (filasModificadas > 0) {
                JOptionPane.showMessageDialog(null, "Los datos se actualizaron correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encuentra el proveedor con el código que especificastes.");
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
        String sql = "delete from SUPPLIERS where COD_SUPPLIER = ?";

        String  COD_SUPPLIER = Cod_Proveedor.getText();

        try {
            ps = conexion.prepareStatement(sql);
            ps.setString(1, COD_SUPPLIER);

            int filasEliminadas = ps.executeUpdate();
            if (filasEliminadas > 0) {
                JOptionPane.showMessageDialog(null, "El proveedor se eliminó correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el proveedor con el código que especificastes.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Hay un error al eliminar el proveedor: " + e.getMessage());
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
        String sql = "SELECT * FROM SUPPLIERS";

        try {
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();


            DefaultTableModel model = (DefaultTableModel) table1P.getModel();
            model.setRowCount(0);

            if (model.getColumnCount() == 0) {
                model.addColumn("Cod_Proveedor");
                model.addColumn("Nombre_Proveedor");
                model.addColumn("Direccion");
                model.addColumn("Telefono");
            }

            while (rs.next()) {
                Object[] fila = {
                        rs.getString("COD_SUPPLIER"),
                        rs.getString("SUPPLIER_NAME"),
                        rs.getString("ADDRESS"),
                        rs.getString("PHONE_NUMBER")
                };
                model.addRow(fila);
            }

            table1P.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar los datos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarConexion();
        }
    }
    void agregarArticulo() {
        conectar();
        String COD_PURCHASE = Cod_Compra.getText().trim();
        String PURCHASED_PRODUCT = Producto_Comprado.getText();
        String UNIT_VALUE = Valor_Unitario.getText().trim();
        String QUANTITY_Kg = Cantidad_Producto_Kg.getText().trim();
        String TOTAL_PRODUCT = Total_Producto.getText().trim().replace(",",".");

        CompraDetalle producto = new CompraDetalle(COD_PURCHASE, PURCHASED_PRODUCT, UNIT_VALUE, QUANTITY_Kg, TOTAL_PRODUCT);
        productosCompra.add(producto);

        JOptionPane.showMessageDialog(null, "Producto agregado a la compra.");

    }
    void agregarCompra() {
        conectar();
        int codCompraGenerado = -1;

        try {
            conexion.setAutoCommit(false);
            String sqlPurchases = "INSERT INTO PURCHASES (COD_ADMINISTRATOR, COD_SUPPLIER, DATE_PURCHASE, TOTAL_PURCHASE_VALUE) VALUES (?, ?, ?, ?)";
            ps = conexion.prepareStatement(sqlPurchases, Statement.RETURN_GENERATED_KEYS);

            String COD_ADMINISTRATOR = Cod_Administrador.getText().trim();
            String COD_SUPPLIERC = Cod_ProveedorC.getText().trim();
            String DATE_PURCHASE = Fecha_Compra.getText().trim();
            String TOTAL_PURCHASE_VALUE = Total_Compra.getText().trim().replace(",",".");

            ps.setString(1, COD_ADMINISTRATOR);
            ps.setString(2, COD_SUPPLIERC);
            ps.setString(3, DATE_PURCHASE);
            ps.setString(4, TOTAL_PURCHASE_VALUE);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();


            if (rs.next()) {
                codCompraGenerado = rs.getInt(1);
                Cod_Compra.setText(String.valueOf(codCompraGenerado));
            } else {
                throw new SQLException("Error al obtener el COD_PURCHASE generado.");
            }


            String sqlPurchases_Details = "INSERT INTO PURCHASES_DETAILS (COD_PURCHASE, PURCHASED_PRODUCT, UNIT_VALUE, QUANTITY_Kg, TOTAL_PRODUCT) VALUES (?, ?, ?, ?, ?)";
            ps = conexion.prepareStatement(sqlPurchases_Details);

            for (CompraDetalle producto : productosCompra) {
                ps.setInt(1, codCompraGenerado);
                ps.setString(2, producto.PURCHASED_PRODUCT);
                ps.setString(3, producto.UNIT_VALUE);
                ps.setString(4, producto.QUANTITY_Kg);
                ps.setString(5, producto.TOTAL_PRODUCT);
                ps.addBatch();
            }

            int[] filasInsertadas = ps.executeBatch();

            if (filasInsertadas.length > 0) {
                conexion.commit();
                JOptionPane.showMessageDialog(null, "La compra y los productos se insertaron correctamente.");
                productosCompra.clear();
            } else {
                throw new SQLException("No se pudieron insertar los productos en la compra.");
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
        conectar();
        try {
            double cantidad = Double.parseDouble(Cantidad_Producto_Kg.getText().trim());
            double precio = Double.parseDouble(Valor_Unitario.getText().trim());
            double totalProducto = cantidad * precio;
            Total_Producto.setText(String.format("%.2f", totalProducto));
        } catch (NumberFormatException ex) {
        }
    }

    void actualizarTotalCompra() {
        conectar();
        double totalCompra = 0;
        for (CompraDetalle producto : productosCompra) {
            try {
                double totalProducto = Double.parseDouble(producto.TOTAL_PRODUCT.replace(",","."));
                totalCompra += totalProducto;
            } catch (NumberFormatException e) {
                System.out.println("Error al parsear el total del producto: " + producto.TOTAL_PRODUCT);
            }
        }
        Total_Compra.setText(String.format("%.2f", totalCompra));
        System.out.println("Total de compra actualizado: " + totalCompra);
    }


    void mostrarDatosCompra() {
        conectar();
        String sqlPURCHASE = "SELECT p.COD_PURCHASE, p.COD_ADMINISTRATOR, p.COD_SUPPLIER, p.DATE_PURCHASE, p.TOTAL_PURCHASE_VALUE, " +
                "pd.PURCHASED_PRODUCT, pd.UNIT_VALUE, pd.QUANTITY_Kg, pd.TOTAL_PRODUCT " +
                "FROM PURCHASES p JOIN PURCHASES_DETAILS pd ON p.COD_PURCHASE = pd.COD_PURCHASE";

        try {
            ps = conexion.prepareStatement(sqlPURCHASE);
            rs = ps.executeQuery();


            DefaultTableModel model = (DefaultTableModel) table1.getModel();
            model.setRowCount(0);

            if (model.getColumnCount() == 0) {
                model.addColumn("Cod_Compra");
                model.addColumn("Cod_Administrador");
                model.addColumn("Cod_Proveedor");
                model.addColumn("Fecha_Compra");
                model.addColumn("Producto_Comprado");
                model.addColumn("Valor_Unitario");
                model.addColumn("Cantidad_Producto_Kg");
                model.addColumn("Total_Producto");
                model.addColumn("Total_Compra");
            }

            while (rs.next()) {
                Object[] fila = {
                        rs.getString("COD_PURCHASE"),
                        rs.getString("COD_ADMINISTRATOR"),
                        rs.getString("COD_SUPPLIER"),
                        rs.getString("DATE_PURCHASE"),
                        rs.getString("PURCHASED_PRODUCT"),
                        rs.getString("UNIT_VALUE"),
                        rs.getString("QUANTITY_Kg"),
                        rs.getString("TOTAL_PRODUCT"),
                        rs.getString("TOTAL_PURCHASE_VALUE")
                };
                model.addRow(fila);
            }

            table1.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar los datos de compra: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarConexion();
        }
    }

    private void cerrarConexion() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conexion != null) conexion.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void mostrarVentanaCompra() {
        Compras compras1 = new Compras();
        compras1.setContentPane(new Compras().panelCompras);
        compras1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        compras1.setVisible(true);
        compras1.pack();
    }
}
