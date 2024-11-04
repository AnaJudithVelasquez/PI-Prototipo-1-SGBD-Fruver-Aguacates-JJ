import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class PantallaInicioFruver extends JFrame {
    private JPanel panelFruver;
    private JLabel PASS;
    private JTextField USER1;
    private JLabel USER;
    private JPasswordField PASS1;
    private JButton INGRESAR;
    Connection conexion;
    Statement st;
    ResultSet rs;

public PantallaInicioFruver(){

    setSize(600, 600);
    setLocationRelativeTo(null);

    INGRESAR.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            validarUSER();
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

    void validarUSER() {
        conectar();
        String NAME_U = USER1.getText();
        String PASS_W = String.valueOf(PASS1.getText());

        try {
            st = conexion.createStatement();
            rs = st.executeQuery("SELECT * FROM USERS WHERE NAME_U = '" + NAME_U + "' AND PASS_W = '" + PASS_W + "'");

            if (rs.next()) {
                int POSITION = rs.getInt("POSITION");
                JOptionPane.showMessageDialog(null, "Las credenciales del usuario son correctas");

                if (POSITION == 1) {
                    Fruver_Aguacates_JJ enlace = new Fruver_Aguacates_JJ();
                    enlace.mostrarVentanaFruver_Aguacates_JJ();
                } else if (POSITION == 2) {
                    Ventas enlaceVentas = new Ventas();
                    enlaceVentas.mostrarVentanaVentas();
                } else {
                    JOptionPane.showMessageDialog(null, "El rol del usuario no está definido correctamente.");
                }

            } else {
                JOptionPane.showMessageDialog(null, "Las credenciales no son correctas");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }



    public static void main(String[] args){
        PantallaInicioFruver pantallaInicio1 = new PantallaInicioFruver();
        pantallaInicio1.setContentPane(new PantallaInicioFruver().panelFruver);
        pantallaInicio1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pantallaInicio1.setVisible(true);
        pantallaInicio1.pack();

    }
}



