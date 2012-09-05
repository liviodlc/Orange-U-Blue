package view;

import java.io.File;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.Player;

public class ScoreBoard extends JPanel {
	
	private static ScoreBoard me;
	
	public static ScoreBoard makeMe(GameWorld gw){
		me = new ScoreBoard(gw);
		return me;
	}
	
	public static ScoreBoard getMe(){
		return me;
	}
	
	private GameWorld gw;
	
	private int p1;
	private int p2;
	
	private JLabel p1_digit1;
	private JLabel p1_digit2;
	private JLabel p2_digit1;
	private JLabel p2_digit2;
	
	private ImageIcon[] p1_imgs;
	private ImageIcon[] p2_imgs;
	
	private ScoreBoard(GameWorld gw){
		this.gw = gw;
		JLabel div = new JLabel();
		div.setIcon(new ImageIcon("images"+File.separator+"--.png"));
		div.setLocation(GameFrame.WINDOW_WIDTH/2-30,200);//
		div.setBounds(GameFrame.WINDOW_WIDTH/2-30,200, 61, 26);
		gw.add(div, 0);
		
		p1_imgs = new ImageIcon[10];
		for(int i = 0; i<10; i++){
			p1_imgs[i] = new ImageIcon("images"+File.separator+"p1_"+i+".png");
		}
		p2_imgs = new ImageIcon[10];
		for(int i = 0; i<10; i++){
			p2_imgs[i] = new ImageIcon("images"+File.separator+"p2_"+i+".png");
		}
		
		
		p1_digit1 = new JLabel();
		p1_digit1.setLocation(div.getX()+65, div.getY()-80);
		p1_digit1.setBounds(div.getX()+65, div.getY()-80, 104,178);
		p1_digit2 = new JLabel();
		p1_digit2.setLocation(p1_digit1.getX()+100, p1_digit1.getY());
		p1_digit2.setBounds(p1_digit1.getX()+100, p1_digit1.getY(), 104,178);
		paintP1Score();
		gw.add(p1_digit1,0);
		gw.add(p1_digit2,0);
		
		p2_digit1 = new JLabel();
		p2_digit1.setLocation(10, div.getY()-80);
		p2_digit1.setBounds(10, div.getY()-80, 104,178);
		p2_digit2 = new JLabel();
		p2_digit2.setLocation(p2_digit1.getX()+100, p2_digit1.getY());
		p2_digit2.setBounds(p2_digit1.getX()+100, p2_digit1.getY(), 104,178);
		paintP2Score();
		gw.add(p2_digit1,0);
		gw.add(p2_digit2,0);
	}
	
	private void paintP1Score(){
		int left = p1 / 10;
		int right = p1 % 10;
		
		p1_digit1.setIcon(p1_imgs[left]);
		p1_digit2.setIcon(p1_imgs[right]);
	}

	private void paintP2Score(){
		int left = p2 / 10;
		int right = p2 % 10;
		
		p2_digit1.setIcon(p2_imgs[left]);
		p2_digit2.setIcon(p2_imgs[right]);
	}
	
	public void p1GotShot(){
		p1++;
		paintP1Score();
		if(p1>=40){
			gw.isPaused=true;
			gw.repaint();
			long battleLength = System.nanoTime() - gw.startTime;
			Date date = new Date(battleLength / 1000000);
			int i = JOptionPane.showConfirmDialog(null, "Mr. Blue wins!" + "\n\nMr. Blue shot himself:    " + gw.player1.selfShotCount + " times!" + "\nMr. Orange shot himself:         " + gw.player2.selfShotCount + " times!\nThe match lasted for "+date.getSeconds()+" seconds.\n\nRestart?", "Game Over", JOptionPane.YES_NO_OPTION);
			if(i==JOptionPane.YES_OPTION)
				gw.restart();
			else
				System.exit(0);
		}
	}
	
	public void p2GotShot(){
		p2++;
		paintP2Score();
		if(p2>=40){
			gw.isPaused=true;
			gw.repaint();
			long battleLength = System.nanoTime() - gw.startTime;
			Date date = new Date(battleLength / 1000000);
			int i = JOptionPane.showConfirmDialog(null, "Mr. Orange wins!" + "\n\nOrange shot himself:    " + gw.player1.selfShotCount + " times!" + "\nBlue shot himself:         " + gw.player2.selfShotCount + " times!\nThe match lasted for "+date.getSeconds()+" seconds."+"\n\nRestart?", "Game Over", JOptionPane.YES_NO_OPTION);
			if(i==JOptionPane.YES_OPTION)
				gw.restart();
			else
				System.exit(0);
		}
	}
}
