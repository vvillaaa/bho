/*
Autore: Dario Nappi
Classe: 4^F
Data: 10/3/'24
Testo: Picross
*/
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Picross{
	private final int righe;
	private final int colonne; //per fila
	final Color colorSelez = Color.BLACK;
	final Color colorSfondo = Color.WHITE;
	final Color colorEscluso = Color.LIGHT_GRAY;
	Font font = new Font("Arial", Font.PLAIN,16);
	File file;

	JFrame frame;

	JDialog dlgHaiVinto; //TODO metti roba nel dialog della vittoria

	JFileChooser fchChooser;

	JPanel pnlCas;//elle
	JPanel pnlLblRig;
	JPanel pnlLblCol;
	JPanel pnlTabella; //TODO better allignement for the gameboard
	JPanel pnlOpzioni;

	GridBagConstraints gbc;

	JTextArea txtaCrono;//logia

	JScrollPane spCrono;

	JButton btnSalva;
	JButton btnCarica;

	JLabel lblRighe[];
	JLabel lblColonne[]; //TODO scrivi testo verticalmente

	Casella[][] casPuls;//antiera
	boolean[][] casSoluz;//ione



	Picross(){
		this(10, 10);
	}
	Picross(int righe, int colonne){
		this.righe = righe;
		this.colonne = colonne;
		frame = new JFrame("Picross");
		//
		fchChooser = new JFileChooser("./");
		//
		pnlCas = new JPanel(new GridLayout(righe, colonne));
		pnlLblRig = new JPanel(new GridLayout(righe, 1));
		pnlLblCol = new JPanel(new GridLayout(1, colonne));
		pnlTabella = new JPanel(new GridBagLayout());
		pnlOpzioni = new JPanel(new GridLayout(1, 2));
		//
		gbc = new GridBagConstraints();
		//
			//txtNomeFile = new JTextField();
		//
		txtaCrono = new JTextArea();
		txtaCrono.setEditable(false);
		//
		spCrono = new JScrollPane(txtaCrono);
		spCrono.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		spCrono.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		spCrono.setPreferredSize(new Dimension(200, 300));
		//
		btnSalva = new JButton("SALVA");
		btnCarica = new JButton("CARICA");
		//
		lblRighe = new JLabel[righe];
		lblColonne = new JLabel[colonne];
		//
		casPuls = new Casella[righe][colonne];
		//
		casSoluz = new boolean[righe][colonne];
		//

		//font
		btnSalva.setFont(font);
		btnCarica.setFont(font);

		//colori

		//add
		frame.add(pnlTabella, "Center");
		frame.add(spCrono, "East");
		frame.add(pnlOpzioni,"South");

		gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        pnlTabella.add(pnlLblCol, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        pnlTabella.add(pnlLblRig, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
		pnlTabella.add(pnlCas, gbc);

		for(int i=0; i<righe; i++){
			lblRighe[i] = new JLabel();
			lblColonne[i] = new JLabel();
			lblRighe[i].setPreferredSize(new Dimension(70, 70));
			lblColonne[i].setPreferredSize(new Dimension(70, 70));
			//
			lblColonne[i].setVerticalAlignment(SwingConstants.BOTTOM);
			//
			lblRighe[i].setHorizontalAlignment(SwingConstants.RIGHT);
			lblColonne[i].setHorizontalAlignment(SwingConstants.CENTER);
			//
			lblRighe[i].setFont(font);
			lblColonne[i].setFont(font);
			//
			pnlLblRig.add(lblRighe[i]);
			pnlLblCol.add(lblColonne[i]);

			for(int j=0; j<colonne; j++){
				casPuls[i][j] = new Casella();
				casPuls[i][j].btn.setPreferredSize(new Dimension(70, 70));
				casPuls[i][j].btn.setFont(font);
				casPuls[i][j].btn.setBackground(colorSfondo);
				pnlCas.add(casPuls[i][j].btn);
			}
		}
		//pnlOpzioni.add(txtNomeFile);
		pnlOpzioni.add(btnCarica);
		pnlOpzioni.add(btnSalva);

		//eventi
		for(Casella[] fila : casPuls){
			for(Casella casella : fila){
				ClickCasella listener = new ClickCasella(casella);
				casella.btn.addActionListener(listener);
				casella.btn.addMouseListener(listener);
			}
		}
		btnCarica.addActionListener(new ClickCarica());
		btnSalva.addActionListener(new ClickSalva());

		//impostazioni frame
		frame.setSize(1300,850);
		frame.setLocation(140,0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		//
	}
	class ClickCasella implements ActionListener, MouseListener{
		//attributo
		private Casella casella;
		//costruttore
		ClickCasella(Casella casella){
			this.casella = casella;
		}
		//metodi
		public void actionPerformed(ActionEvent e){
			casella.invertiStato();
			if(casella.occupato){
				casella.btn.setBackground(colorSelez);
			}else{
				casella.btn.setBackground(colorSfondo);
			}
			if(file!=null&&controllaVittoria()){
				haiVinto();
			}
		}
		public void mousePressed(MouseEvent e){
			if(SwingUtilities.isRightMouseButton(e)) {
				if(!(casella.btn.getBackground()==colorEscluso)){
					casella.btn.setBackground(colorEscluso);
					casella.occupato = false;
				}else
					casella.btn.setBackground(colorSfondo);
			}
		}
		public void mouseClicked(MouseEvent e){}
		public void mouseReleased(MouseEvent e){}
		public void mouseEntered(MouseEvent e){}
		public void mouseExited(MouseEvent e){}

		private boolean controllaVittoria(){ //TODO ottimizza controllo della vittoria
			boolean vinto=true;
			for(int i=0; i<righe&&vinto; i++){
				for(int j=0; j<colonne&&vinto; j++){
					vinto = vinto && (casPuls[i][j].occupato == casSoluz[i][j]);
				}
			}
			return vinto;
		}
	}
	void haiVinto(){
		JLabel lblHaiVinto = new JLabel("Hai vinto!");

		lblHaiVinto.setFont(new Font("Arial", Font.PLAIN,64));
		lblHaiVinto.setHorizontalAlignment(SwingConstants.CENTER);

		txtaCrono.append("Hai vinto!\n");
		dlgHaiVinto = new JDialog(frame, true);
		dlgHaiVinto.setSize(400,300);
		dlgHaiVinto.setLocation(500,200); //TODO always center frame
		dlgHaiVinto.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);


		dlgHaiVinto.add(lblHaiVinto);

		dlgHaiVinto.setVisible(true);
	}
	class ClickCarica implements ActionListener{
		//attributi
		//private File file;
		private FileReader fR;
		private BufferedReader reader;
		private String riga;
		private boolean colonna[] = new boolean[righe];
		private int opzioneScelta;
		public void actionPerformed(ActionEvent e){
			opzioneScelta = fchChooser.showOpenDialog(frame);
			if(opzioneScelta == JFileChooser.APPROVE_OPTION){
				file = fchChooser.getSelectedFile();
			}
			try{
				fR = new FileReader(file);
				reader = new BufferedReader(fR);

				for(int i=0; i<righe; i++){
					riga = reader.readLine();
					for(int j=0; j<colonne; j++){
						casPuls[i][j].btn.setBackground(colorSfondo);
						casPuls[i][j].occupato = false;
						casSoluz[i][j] = riga.charAt(j)=='o';
					}
					lblRighe[i].setText(contaCaselleLinea(casSoluz[i]));
				}
				fR.close();
				for(int i=0; i<colonne; i++){
					for(int j=0; j<righe; j++){
						colonna[j] = casSoluz[j][i];
					}
					lblColonne[i].setText(contaCaselleLinea(colonna));
				}

				txtaCrono.append(file.getName()+" caricato con successo\n");
				frame.setTitle(file.getName());
			}catch(IOException ioE){
				txtaCrono.append(ioE.getMessage());
			}
		}
	}
	String contaCaselleLinea(boolean linea[]){
        String delim = " ";
        String str="";
        int count=0;
        for(int i=0; i<linea.length; i++){
            if(linea[i]){
               count++;
            }else if(count!=0){
                str+=count+delim;
                count=0;
            }
        }
        if(count!=0 || str=="")
            str+=""+count;

		return str.trim();
	}
	class ClickSalva implements ActionListener{
		//attributi
		//private File file;
		private FileWriter fW;
		private PrintWriter writer;
		private String riga;
		private int opzioneScelta;
		public void actionPerformed(ActionEvent e){
			opzioneScelta = fchChooser.showSaveDialog(frame);
			if(opzioneScelta == JFileChooser.APPROVE_OPTION){
				file = fchChooser.getSelectedFile();
			}
			try{
				fW = new FileWriter(file);
				writer = new PrintWriter(fW);
				for(int i=0; i<righe; i++){
					riga="";
					for(int j=0; j<colonne; j++){
						riga += ""+(casPuls[i][j].occupato?'o':'l');
					}
					writer.println(riga);
				}
				fW.close();
				txtaCrono.append(file.getName()+" salvato con successo\n");
				frame.setTitle(file.getName());
			}catch(IOException ioE){
				txtaCrono.append(ioE.getMessage());
			}
		}
	}
	public static void main(String args[]){
		new Picross();
	}
}
