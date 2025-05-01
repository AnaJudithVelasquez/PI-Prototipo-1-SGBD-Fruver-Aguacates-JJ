import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Fruver_Aguacates_JJ extends JFrame {
    private JPanel panelOpciones;
    private JButton productosButton;
    private JButton ventasButton;
    private JButton comprasButton;
    Connection conexion;

    public Fruver_Aguacates_JJ(){

        setSize(600, 600);
        setLocationRelativeTo(null);

        productosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
             llamarBotonProducto();
            }
        });


        ventasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                llamarBotonVenta();
            }
        });

        comprasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                llamarBotonCompra();
            }
        });
    }

    void conectar() {
        try {
            conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/FruverAguacates", "root", "10j56yeyo");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void llamarBotonCompra(){
        conectar();
        Compras enlace = new Compras();
        enlace.mostrarVentanaCompras();
        dispose();
    }

     void llamarBotonProducto(){
        conectar();
         Productos enlace = new Productos();
         enlace.mostrarVentanaProductos();
         dispose();
     }

    void llamarBotonVenta(){
        conectar();
        Ventas enlace = new Ventas();
        enlace.mostrarVentanaVentas();
        dispose();
    }



    public static void mostrarVentanaFruver_Aguacates_JJ(){
        Fruver_Aguacates_JJ fruver_aguacates_JJ1 = new Fruver_Aguacates_JJ();
        fruver_aguacates_JJ1.setContentPane(fruver_aguacates_JJ1.panelOpciones);
        fruver_aguacates_JJ1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fruver_aguacates_JJ1.setVisible(true);
        fruver_aguacates_JJ1.pack();
    }


}




