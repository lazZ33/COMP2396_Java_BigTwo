import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

/**
 * Class implements the NetworkGame interface. It is used to model a Big
 * Two game client that is responsible for establishing a connection and communicating with
 * the Big Two game server
 * @author Horace
 *
 */
public class BigTwoClient implements NetworkGame {
	
	/**
	 * a public constructor for creating a Big Two client. 
	 * @param game a reference to a BigTwo object associated with this client
	 * @param gui a reference to a BigTwoGUI object associated the BigTwo object
	 */
	public BigTwoClient(BigTwo game, BigTwoGUI gui) {
		this.game = game;
		this.gui = gui;
		
		this.playerName = JOptionPane.showInputDialog("Please enter your name: ");
		this.serverIP = "127.0.0.1";
		this.serverPort = 8000;
		
		this.connect();
	}
	
	private BigTwo game;
	private BigTwoGUI gui;
	private Socket sock;
	private ObjectOutputStream oos;
	private int playerID;
	private String playerName;
	private String serverIP;
	private int serverPort;
	
	@Override
	public int getPlayerID() {
		return this.playerID;
	}

	@Override
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	@Override
	public String getPlayerName() {
		return this.playerName;
	}

	@Override
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	@Override
	public String getServerIP() {
		return this.serverIP;
	}

	@Override
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	@Override
	public int getServerPort() {
		return this.serverPort;
	}

	@Override
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	@Override
	public void connect() {
		if (this.sock != null) {
			if (this.sock.isConnected()) {
				return;
			}
		}
		
		try {
			this.sock = new Socket(this.serverIP, this.serverPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			this.oos = new ObjectOutputStream(this.sock.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Thread receiver = new Thread(new ServerHandler());
		receiver.start();
	}

	@Override
	public synchronized void parseMessage(GameMessage message) {
		switch(message.getType()) {
			case CardGameMessage.PLAYER_LIST:
				this.playerID = message.getPlayerID();
				String[] player_list = (String[])message.getData();
				for (int i = 0; i < player_list.length; i++) {		
					this.game.getPlayerList().get(i).setName(player_list[i]);
				}
				this.sendMessage(new CardGameMessage(CardGameMessage.JOIN, -1, this.playerName));
				break;
			case CardGameMessage.JOIN:
				int player_id = message.getPlayerID();
				this.game.getPlayerList().get(player_id).setName((String)message.getData());
				
				if (this.playerID == player_id) {
					this.sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
				}else {
					this.gui.printSysMessage((String)message.getData() + " joins the game.\n");
				}
				break;
			case CardGameMessage.FULL:
				this.gui.printSysMessage("the server is full, cannot join the game\n");
				break;
			case CardGameMessage.QUIT:
				this.gui.printSysMessage(this.game.getPlayerList().get(message.getPlayerID()).getName() + " has left the game\n");
				CardGamePlayer quit_player = this.game.getPlayerList().get(message.getPlayerID());
				quit_player.removeAllCards();
				quit_player.setName("");
				// check if game in progress
				this.gui.disable();
				this.sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
				this.gui.repaint();
				break;
			case CardGameMessage.READY:
				this.gui.printSysMessage(this.game.getPlayerList().get(message.getPlayerID()).getName() + " is ready.\n");
				this.gui.repaint();
				break;
			case CardGameMessage.START:
				this.game.start((Deck)message.getData());
				this.gui.disable();
				if (this.playerID == this.game.getCurrentPlayerIdx()) {
					this.gui.enable();
				}
				break;
			case CardGameMessage.MOVE:
				if (message.getPlayerID() != this.playerID) {
					this.game.checkMove(message.getPlayerID(), (int[])message.getData());
					if (this.playerID == this.game.getCurrentPlayerIdx()) {
						this.gui.enable();
					}
					break;
				}
				this.gui.disable();
				break;
			case CardGameMessage.MSG:
				this.gui.printMsg((String)message.getData());
				// TODO: change the printMsg implementation
				break;
		}
		
	}

	@Override
	public synchronized void sendMessage(GameMessage message) {
		try {
			this.oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * an inner class that implements the Runnable interface
	 * @author Horace
	 *
	 */
	private class ServerHandler implements Runnable{

		@Override
		public void run() {
			
			try {
				ObjectInputStream reader = new ObjectInputStream(sock.getInputStream());
				while (true) {
					parseMessage((CardGameMessage)reader.readObject());
				}
				
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
}
