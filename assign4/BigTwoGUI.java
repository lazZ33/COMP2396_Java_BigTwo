import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.lang.Integer;

/**
 * The BigTwoGUI class implements the CardGameUI interface. It is used to build a GUI for the Big Two card game and handle all user actions.
 * @author Horace
 *
 */
public class BigTwoGUI implements CardGameUI {
	
	/**
	 * public constructor for creating a BigTwoGUI.
	 * @param game a reference to a BigTwo card game associates with this GUI.
	 */
	public BigTwoGUI(BigTwo game) {
		// GUI variable init
		this.game = game;
		this.selected_cards = new ArrayList<Integer>();
		
		// game frame init
		this.frame = new JFrame();	
		this.frame.setSize(1500, 1000);
		this.frame.setLayout(new GridBagLayout());
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        // menu bar init
        JMenuBar menu_bar = new JMenuBar();
        JMenu Game_menu = new JMenu("Game");
        JMenuItem restart_item = new JMenuItem("Connect");
        restart_item.addActionListener(new ConnectMenuItemListener());
        Game_menu.add(restart_item);
        JMenuItem quit_item= new JMenuItem("Quit");
        quit_item.addActionListener(new QuitMenuItemListener());
        Game_menu.add(quit_item);
        menu_bar.add(Game_menu);
        this.frame.setJMenuBar(menu_bar);

		// player hands display init
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.ipadx = 0;
		c.ipady = 0;
		c.insets.top = 0;
		c.insets.bottom = 0;
		c.insets.left = 0;
		c.insets.right = 0;
		c.gridx = c.gridy = 0;
		c.gridwidth = c.gridheight = 1;
		
		this.playerHands = new PlayerHandPane[4];
		for (int i = 0; i < this.game.getNumOfPlayers(); i++) {
			c.gridy = i;
			this.playerHands[i] = new PlayerHandPane(i);
			this.frame.add(this.playerHands[i], c);
		}
		
		// previous hand display init
		c.gridy = 5;
		this.handsOnTable = new JLayeredPane();
		this.handsOnTable.setPreferredSize(new Dimension(1000, 150));
		this.handsOnTable.setBackground(new Color(0, 128, 0));
		this.handsOnTable.setOpaque(true);
		this.frame.add(this.handsOnTable, c);

		// base bar init
		c.weightx = 0.0;
		c.weighty = 0.1;
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 1;
		c.gridheight = 1;
		JPanel base_bar = new JPanel();
		base_bar.setLayout(new GridBagLayout());
		base_bar.setPreferredSize(new Dimension(1500, 100));
		base_bar.setOpaque(true);
		this.frame.add(base_bar, c);
		this.passButton = new JButton("pass");
		this.playButton = new JButton("play");
		this.chatInput = new JTextField();
		this.passButton.addActionListener(new PassButtonListener());
		this.playButton.addActionListener(new PlayButtonListener());
		this.chatInput.setEditable(true);
		this.chatInput.setPreferredSize(new Dimension(400,30));
		this.chatInput.addActionListener(new ChatInputListener());
		GridBagConstraints barC = new GridBagConstraints();
		barC.gridx = barC.gridy = 0;
		base_bar.add(this.passButton, barC);
		barC.gridx = 1;
		base_bar.add(this.playButton, barC);
		barC.gridx = 2;
		base_bar.add(this.chatInput, barC);
		
		// game message area init
		c.weightx = 0.5;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 3;
		this.msgArea = new JTextArea();
		this.msgArea.setBackground(Color.gray);
		this.msgArea.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		this.msgArea.setPreferredSize(new Dimension(400, 700));
		this.msgArea.setEditable(false);
		this.msgArea.append("Here is message area.\n");
		this.msgScrollPane = new JScrollPane(msgArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.frame.add(this.msgScrollPane, c);

		// chat message area init
		c.gridy = 3;
		c.gridheight = 3;
		this.chatArea = new JTextArea();
		this.chatArea.setBackground(Color.white);
		this.chatArea.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		this.chatArea.setPreferredSize(new Dimension(800, 700));
		this.chatArea.setEditable(false);
		this.chatArea.append("Here is chat area. Type in the box below to chat.\n");
		this.chatScrollPane = new JScrollPane(chatArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.frame.add(this.chatScrollPane, c);

		// repaint on display
		this.frame.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				repaint();
			}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		});
		// display the frame
		this.frame.setVisible(true);
	}
	
	private BigTwo game;
	private ArrayList<Integer> selected_cards;
	private int activePlayer;
	private JFrame frame;
	private PlayerHandPane[] playerHands;
	private JLayeredPane handsOnTable;
	private JButton playButton;
	private JButton passButton;
	private JTextArea msgArea;
	private JScrollPane msgScrollPane;
	private JTextArea chatArea;
	private JScrollPane chatScrollPane;
	private JTextField chatInput;
	
	private String[] rank_cvrt = {"a", "2", "3", "4", "5", "6", "7", "8", "9", "t", "j", "q", "k"};
	private String[] suit_cvrt = {"d", "c", "h", "s"};
	private boolean enabled = true;
	private double card_gap = 0.02;
	private double card_dx = 0.15;
	private double card_dy = 0.02;
	
    @Override
    /**
     * a method for setting the index of the active player (i.e., the player having control of the GUI)
     * @param activePlayer the player to be set active
     */
    public void setActivePlayer(int activePlayer) {
    	this.activePlayer = activePlayer;
    }

    @Override
    /**
     * a method for repainting the GUI.
     */
    public void repaint() {
    	// reset selection
    	this.selected_cards.clear();
    	
    	if (this.game.getClient() == null) {return;}
    	
    	// populating playerHands
    	for (int cur_player_idx = 0; cur_player_idx < this.game.getNumOfPlayers(); cur_player_idx++) {
    		this.playerHands[cur_player_idx].removeAll();
    		CardGamePlayer cur_player = this.game.getPlayerList().get(cur_player_idx);
    		Card cur_cards;
    		
    		if(cur_player_idx == this.game.getClient().getPlayerID()) {
    			for (int cur_card_idx = cur_player.getNumOfCards()-1; cur_card_idx > -1; cur_card_idx--) {
    				cur_cards = cur_player.getCardsInHand().getCard(cur_card_idx);
    				int cur_rank = cur_cards.getRank();
    				int cur_suit = cur_cards.getSuit();
    				this.playerHands[cur_player_idx].add(new BigTwoCardPanel(cur_card_idx, cur_rank, cur_suit, cur_card_idx*(int)(this.frame.getWidth()*this.card_gap) + (int)(this.frame.getWidth()*this.card_dx), (int)(this.frame.getHeight()*this.card_dy), cur_player_idx == this.activePlayer), cur_player.getNumOfCards()-1-cur_card_idx);
    			}
    		}else{
    			for (int cur_card_idx = cur_player.getNumOfCards()-1; cur_card_idx > -1; cur_card_idx--) {
    				this.playerHands[cur_player_idx].add(new BigTwoCardPanel(cur_card_idx*(int)(this.frame.getWidth()*this.card_gap) + (int)(this.frame.getWidth()*this.card_dx), (int)(this.frame.getHeight()*this.card_dy)), cur_player.getNumOfCards()-1-cur_card_idx); // cur_player.getNumOfCards()-1-
    			}
    		}
    	}
    	
    	// populating HandsOnTable in GUI
    	ArrayList<Hand> hand_on_table = this.game.getHandsOnTable();
		this.handsOnTable.removeAll();
    	if (hand_on_table.size() > 0) {
    		Hand cur_hand = hand_on_table.get(hand_on_table.size()-1);
    		for (int cur_card_idx = cur_hand.size()-1; cur_card_idx > -1; cur_card_idx--) {
    			Card cur_cards = cur_hand.getCard(cur_card_idx);
    			int cur_rank = cur_cards.getRank();
    			int cur_suit = cur_cards.getSuit();
    			this.handsOnTable.add(new BigTwoCardPanel(cur_card_idx, cur_rank, cur_suit, cur_card_idx*(int)(this.frame.getWidth()*this.card_gap) + (int)(this.frame.getWidth()*this.card_dx), (int)(this.frame.getHeight()*this.card_dy), false), cur_hand.size()-1-cur_card_idx);
    		}
    	}

    	this.frame.revalidate();
    	this.frame.repaint();
    }
    
    /**
     * a method for showing info to player with a window
     * @param msg the message to display
     * @param title the title of the window
     */
    public void windowPromptInfo(String msg, String title) {
    	JOptionPane.showMessageDialog(this.frame, msg, title,JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * a method for printing the specified string to the system message area of the GUI.
     * @param msg the message to be printed
     */
    public void printSysMessage(String msg) {
		this.msgArea.append(msg);
		int offset = 0;
		if (msgArea.getLineCount() >= msgArea.getHeight()/15.7) {
			try{
				offset = msgArea.getLineEndOffset(0);
			}catch(Exception exp){}
			msgArea.replaceRange("", 0, offset);
		}
		msgArea.setCaretPosition(msgArea.getDocument().getLength());
    }
    
    /**
     * a method for printing the specified string to the chat message area of the GUI.
     * @param msg the message to be printed
     */
    public void printMsg(String msg) {
		chatArea.append(msg);
		chatInput.setText("");
		int offset = 0;
		if (chatArea.getLineCount() >= chatArea.getHeight()/15.7) {
			try{
				offset = msgArea.getLineEndOffset(0);
			}catch(Exception exp){}
			chatArea.replaceRange("", 0, offset);
		}
		chatArea.setCaretPosition(chatArea.getDocument().getLength());
	}

    @Override
    /**
     * a method for clearing the message area of the GUI.
     */
    public void clearMsgArea() {
    	this.msgArea.setText("");
    }

    @Override
    /**
     * a method for resetting the GUI.
     */
    public void reset() {
    	this.deselect_cards();
    	this.clearMsgArea();
    	this.enable();

    }

    @Override
    /**
     * a method for enabling user interactions with the GUI.
     */
    public void enable() {
    	this.playButton.setEnabled(true);
    	this.passButton.setEnabled(true);
//    	this.chatInput.setEnabled(true);
    	this.enabled = true;
    }

    @Override
    /**
     * a method for disabling user interactions with the GUI.
     */
    public void disable() {
    	// (i) disable the “Play” button and “Pass” button (i.e., making them not clickable)
    	this.playButton.setEnabled(false);
    	this.passButton.setEnabled(false);
        // (ii) disable the chat input;
//    	this.chatInput.setEnabled(false);
    	// (iii) disable the BigTwoPanel for selection of cards through mouse clicks
    	this.enabled = false;
    }

    @Override
    /**
     * a method for prompting the active player to select cards and make his/her move
     */
    public void promptActivePlayer() {
    	// A message should be displayed in the message area showing it is the active player’s turn.
		this.printSysMessage( this.game.getPlayerList().get(activePlayer).getName() + "'s turn:\n");
    }
    
    /**
     an inner class that extends the JPanel class and implements the MouseListener interface
     * @author Horace
     *
     */
    class BigTwoCardPanel extends JPanel implements MouseListener{

		private static final long serialVersionUID = 1L;

		/**
		 * public constructor of BigTwoCardPanel, for displaying cards with suit and rank.
		 * @param card_idx the corresponding card index in the player hand list
		 * @param rank the rank of this card
		 * @param suit the suit of this card
		 * @param x the x coordinate of the card on the panel
		 * @param y the y coordinate of the card on the panel
		 * @param interact
		 */
		public BigTwoCardPanel(int card_idx, int rank, int suit, int x, int y, boolean interact) {   			
    		this.card_idx = card_idx;
    		this.x = x;
    		this.y = y;
			this.selected = false;
			this.interact = interact;

    		this.setBounds(x, y , 75, 100);
    		this.image = new ImageIcon("cards/" + rank_cvrt[rank] + suit_cvrt[suit] + ".gif").getImage();
    		this.addMouseListener(this);
    	}

		/**
		 * public constructor of BigTwoCardPanel, for displaying cards with no suit and rank.
		 * @param x the x coordinate of the card on the panel
		 * @param y the y coordinate of the card on the panel
		 */
		public BigTwoCardPanel(int x, int y) {   			
    		this.card_idx = -1;
    		this.x = x;
    		this.y = y;
			this.selected = false;
			this.interact = false;
			
    		this.setBounds(x, y, 75, 100);
    		this.image = new ImageIcon("cards/b.gif").getImage();
    	}
    	
    	private int card_idx;
    	private Image image;
    	private int x;
    	private int y;
		private boolean selected;
		private boolean interact;
    	
		@Override
    	public void paintComponent(Graphics g) {
    		// draw the card game table
    		g.drawImage(this.image, 0, 0, (int)(frame.getWidth()*0.05), (int)(frame.getHeight()*0.1), this);
    	}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (!this.interact | !enabled) {
				return;
			}
			if (this.selected == false) {
				if (selected_cards.size() > 5) {
					return;
				}
				selected_cards.add(this.card_idx);
				this.setLocation(x, y-10);
				playerHands[activePlayer].repaint();
				this.selected = true;
			}
			else {
				selected_cards.remove(new Integer(this.card_idx));
				this.setLocation(x, y);
				playerHands[activePlayer].repaint();
				this.selected = false;
			}
		}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
    }
    
    private void deselect_cards() {
		selected_cards = new ArrayList<Integer>();
    }
    
    /**
     * an inner class that extends the JLayeredPane class. Display a player's hand, avatar and name
     * @author Horace
     *
     */
    public class PlayerHandPane extends JLayeredPane{
    	
    	/**
    	 * public constructor of PlayerHandPane
    	 * @param playerIdx the player index of the pane
    	 */
    	public PlayerHandPane(int playerIdx) {
    		// fetch image from file system
    		this.image = new ImageIcon("players/player" + playerIdx + ".jpeg").getImage();
			this.setPreferredSize(new Dimension(1000, 150));
			this.setBackground(new Color(0,128,0));
			this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			this.setOpaque(true);

			this.playerIdx = playerIdx;
    	}
    	
    	private int playerIdx;
    	private Image image;
    	
    	@Override
    	public void paintComponent(Graphics g) {
    		CardGamePlayer player = game.getPlayerList().get(playerIdx);
    		if (player.getName() != null && player.getName() != "") {
	    		g.drawString(player.getName(), 5, 20);
	    		g.drawImage(this.image, 0, (int)(frame.getHeight()*0.04), (int)(frame.getWidth()*0.05), (int)(frame.getHeight()*0.1), this);
    		}else {
    			g.drawString("waiting for other players...", (int)(this.getHeight()*0.5), (int)(this.getWidth()*0.05));
    		}
    	}
    	
    }
    
    /**
     * an inner class that implements the ActionListener interface. Play the selected card when "play" button is pressed.
     * @author Horace
     *
     */
    public class PlayButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (game.getClient().getPlayerID() == activePlayer) {
				int[] input = new int[5];
				for (int i = 0; i < 5; i++) {
					if (i < selected_cards.size()) {
						input[i] = selected_cards.get(i);
						continue;
					}
					input[i] = -1;
				}
				game.makeMove(activePlayer, input);
				deselect_cards();
				repaint();
			}
		}
    	
    }
    
    /**
     * an inner class that implements the ActionListener interface. Pass the turn when "pass" button is pressed.
     * @author Horace
     *
     */
    public class PassButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (game.getClient().getPlayerID() == activePlayer) {
				game.makeMove(activePlayer, null);
				deselect_cards();
				repaint();
			}
		}
    	
    }
    
    /**
     * an inner class that implements the ActionListener interface. Restart the game when "restart" is pressed.
     * @author Horace
     *
     */
    public class ConnectMenuItemListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (game.getClient() == null) {
				if (game.getClient() == null) {
					game.setClient(new BigTwoClient(game, BigTwoGUI.this));
				}
				return;
			}
			game.getClient().connect();
		}
    	
    }
    
    /**
     * an inner class that implements the ActionListener interface. Quit the game when "quit" item is pressed.
     * @author Horace
     *
     */
    public class QuitMenuItemListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);			
		}
    	
    }
    
    /**
     * an inner class that implements the ActionListener interface. Append input messages to chat area when appropriate.
     * @author Horace
     *
     */
    public class ChatInputListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String msg = chatInput.getText() + "\n";
			game.getClient().sendMessage(new CardGameMessage(CardGameMessage.MSG, -1, msg));
		}
    }
}
