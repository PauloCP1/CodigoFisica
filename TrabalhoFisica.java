package trabalhofisica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TrabalhoFisica extends JPanel implements ActionListener {
    private double F = 4.9;
    private double m1 = 3.0;
    private double m2 = 1.0;
    private final double g = 9.8;
    private double angulo = Math.toRadians(30);

    private Timer timer;
    private double[] resultado;
    private double deslocamento = 0;
    private double velocidade = 0;

    private JTextField inputF, inputM1, inputM2, inputAngulo;
    private JButton pauseButton;
    private boolean paused = false;

    public TrabalhoFisica() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 7));

        inputF = new JTextField(Double.toString(F));
        inputM1 = new JTextField(Double.toString(m1));
        inputM2 = new JTextField(Double.toString(m2));
        inputAngulo = new JTextField("30");

        JButton aplicar = new JButton("Aplicar");
        pauseButton = new JButton("Pausar");
        JButton reiniciarButton = new JButton("Reiniciar");

        inputPanel.add(new JLabel("Força F (N):"));
        inputPanel.add(new JLabel("Massa m1 (kg):"));
        inputPanel.add(new JLabel("Massa m2 (kg):"));
        inputPanel.add(new JLabel("Ângulo (graus):"));
        inputPanel.add(new JLabel(" "));
        inputPanel.add(new JLabel(" "));
        inputPanel.add(new JLabel(" "));

        inputPanel.add(inputF);
        inputPanel.add(inputM1);
        inputPanel.add(inputM2);
        inputPanel.add(inputAngulo);
        inputPanel.add(aplicar);
        inputPanel.add(pauseButton);
        inputPanel.add(reiniciarButton);

        add(inputPanel, BorderLayout.NORTH);

        aplicar.addActionListener(e -> {
            try {
                F = Double.parseDouble(inputF.getText());
                m1 = Double.parseDouble(inputM1.getText());
                m2 = Double.parseDouble(inputM2.getText());
                angulo = Math.toRadians(Double.parseDouble(inputAngulo.getText()));
                resultado = calcular();
                velocidade = 0;
                deslocamento = 0;
                repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Entrada inválida!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        pauseButton.addActionListener(e -> {
            paused = !paused;
            pauseButton.setText(paused ? "Continuar" : "Pausar");
        });

        reiniciarButton.addActionListener(e -> {
            F = 4.9;
            m1 = 3.0;
            m2 = 1.0;
            angulo = Math.toRadians(30);

            inputF.setText(Double.toString(F));
            inputM1.setText(Double.toString(m1));
            inputM2.setText(Double.toString(m2));
            inputAngulo.setText("30");

            velocidade = 0;
            deslocamento = 0;
            resultado = calcular();
            repaint();

            paused = true;
            pauseButton.setText("Continuar");
        });

        resultado = calcular();
        timer = new Timer(30, this);
        timer.start();
    }

    private double[] calcular() {
        double a = (F - m2 * g * Math.sin(angulo)) / (m1 + m2);
        double T = F - m1 * a;
        return new double[]{T, a};
    }
    
    private double calcularForcaMaxima() {
    return m2 * g * Math.sin(angulo);
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!paused) {
            double a = resultado[1];
            velocidade += a * 0.03;
            deslocamento += velocidade;
            if (Math.abs(deslocamento) > 100) velocidade = 0;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g2) {
        super.paintComponent(g2);
        Graphics2D g = (Graphics2D) g2;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double T = resultado[0];
        double a = resultado[1];

        g.drawString(String.format("F = %.1f N", F), 20, 100);
        g.drawString(String.format("Tensão = %.2f N", T), 20, 120);
        g.drawString(String.format("Aceleração = %.2f m/s²", a), 20, 140);
        g.drawString(String.format("F máximo sem folga = %.2f N", calcularForcaMaxima()), 20, 160);
        
        int baseX1 = 150 + (int) deslocamento;
        int baseY1 = 180;

        g.setColor(Color.GRAY);
        g.fillRect(0, baseY1 + 50, getWidth(), 10);

        g.setColor(Color.BLUE);
        g.fillRect(baseX1, baseY1, 80, 50);
        g.setColor(Color.BLACK);
        g.drawString("m1", baseX1 + 30, baseY1 - 5);

        int poliaX = baseX1 + 80 + 40;
        int poliaY = baseY1 + 18;
        g.setColor(Color.GRAY);
        g.fillOval(poliaX - 10, poliaY - 10, 20, 20);

        g.setColor(Color.DARK_GRAY);
        int planoL = 100;
        int dx = (int) (planoL * Math.cos(angulo));
        int dy = (int) (planoL * Math.sin(angulo));
        int planoBaseX = poliaX + 30;
        int planoBaseY = poliaY + 25;

        int caixaX = planoBaseX + (int) (deslocamento * Math.cos(angulo));
        int caixaY = planoBaseY + (int) (deslocamento * Math.sin(angulo));
        Polygon caixa = new Polygon();
        caixa.addPoint(caixaX, caixaY);
        caixa.addPoint(caixaX + 40, caixaY);
        caixa.addPoint(caixaX + 40, caixaY + 30);
        caixa.addPoint(caixaX, caixaY + 30);
        g.setColor(Color.RED);
        g.fillPolygon(caixa);
        g.setColor(Color.BLACK);
        g.drawString("m2", caixaX + 10, caixaY - 5);

        g.setColor(Color.BLACK);
        g.drawLine(baseX1 + 80, baseY1 + 18, poliaX, poliaY); 
        g.drawLine(poliaX, poliaY, caixaX + 20, caixaY);     
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Simulação da Tensão na Corda");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 500);
        frame.add(new TrabalhoFisica());
        frame.setVisible(true);
    }
}
