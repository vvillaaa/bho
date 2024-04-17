/*
Autore: Dario Nappi
Classe: 4^F
Data: 10/3/'24
Testo: Picross
*/
import javax.swing.*;

class Casella{
    //attributi
    JButton btn;
    boolean occupato;
    //costruttori
    Casella(){
        this.btn = new JButton();
        this.occupato = false;
    }
    Casella(String etichetta){
        this.btn = new JButton(etichetta);
        this.occupato = false;
    }
    Casella(JButton btn){
        this.btn = btn;
        this.occupato = false;
    }
    Casella(String etichetta, boolean occupato){
        this.btn = new JButton(etichetta);
        this.occupato = occupato;
    }
    Casella(JButton btn, boolean occupato){
        this.btn = btn;
        this.occupato = occupato;
    }
    //metodi
    void invertiStato(){
        if(occupato)
            occupato=false;
        else
            occupato=true;
    }
}
