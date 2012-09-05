package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Creates the JFrame for the client.
 * 
 * Also contains a main method, so this is the class that launches the program.
 * 
 * This class also receives updates from the Client and passes them on to the
 * GameWorld.
 * 
 * @author Tyler Cox, Livio De La Cruz
 * 
 * 
 */
public class GameFrame extends JFrame {

	static final int WINDOW_WIDTH = 500;
	static final int WINDOW_HEIGHT = 500;
	private static final String WINDOW_TITLE = "The Gordon Freemen's 3-Week Game";
	private final String creditsText = "This game was created by\nTHE GORDAN FREEMEN:\nTyler Cox\nLivio De La Cruz\nWesin Smith\n\nSprites made by Philipp Lenssen. Used with\npermission under Creative Commons License.";

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		Thread game = new Thread(new Runnable() {
			@Override
			public void run() {
				new GameFrame();
			}
		});
		game.start();
	}

	private GameWorld world;

	public GameFrame() {
		initFrame();
		setLookAndFeel();
		initMenuBar();

		initGameWorld();

		setVisible(true);
		repaint();

		world.grabFocus();
		world.startGame();
	}

	private void initFrame() {
		setLayout(new BorderLayout());
		setFocusable(true);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setTitle(WINDOW_TITLE);
		setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		setLocation(150, 100);
	}

	/**
	 * Set cross-platform Java L&F (also called "Metal")
	 */
	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
	}

	public static void showHowToPlay() {
		JOptionPane.showMessageDialog(null, "                           First to 40 wins!" +
				"\n\nMR BLUE CONTROLS:\n\n        Move:         " +
				"Up                     Shoot:  Numpad0 or End\n                Left  Down  Right\n                                                         " +
				"        \n\n\nMR ORANGE CONTROLS:\n\n         Move:         W                     " +
				"Shoot:  Space\n                        A  S  D\n\n" +
				"\nPAUSE:  Esc or Shift", "How to Play", JOptionPane.DEFAULT_OPTION, null);
	}

	private void initMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		gameMenu.setMnemonic('G');

		JMenuItem rs = new JMenuItem("Restart");
		rs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				world.restart();
			}
		});
		gameMenu.add(rs);

		JMenuItem ab = new JMenuItem("Quit");
		ab.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		});
		gameMenu.add(ab);

		menuBar.add(gameMenu);

		JMenu help = new JMenu("Help");

		JMenuItem howto = new JMenuItem("How to Play");
		howto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showHowToPlay();
			}
		});
		help.add(howto);

		JMenuItem credits = new JMenuItem("Credits");
		credits.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JOptionPane.showMessageDialog(null, creditsText, "Credits",
						JOptionPane.DEFAULT_OPTION, null);
			}
		});
		help.add(credits);

		menuBar.add(help);
		add(menuBar, BorderLayout.NORTH);
	}

	private void initGameWorld() {
		world = new GameWorld();
		add(world, BorderLayout.CENTER);
	}
}
