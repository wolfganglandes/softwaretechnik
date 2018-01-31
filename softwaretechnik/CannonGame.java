package de.tuberlin.sese.swtpp.gameserver.model.cannon;

import java.io.Serializable;
import java.util.*;

import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Move;
import de.tuberlin.sese.swtpp.gameserver.model.Player;

/**
 * Class LascaGame extends the abstract class Game as a concrete game instance that allows to play 
 * Lasca (http://www.lasca.org/).
 *
 */
public class CannonGame extends Game implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 5424778147226994452L;
	
	
	/************************
	 * member
	 ***********************/
	
	// just for better comprehensibility of the code: assign white and black player
	private Player blackPlayer;
	private Player whitePlayer;

	// internal representation of the game state
	// TODO: insert additional game data here
	
	char[][] board = new char[10][10];
	HashMap<Character,Integer> hmap = new HashMap<Character,Integer>();
	
	
	/************************
	 * constructors
	 ***********************/
	
	public CannonGame() {
		super();

		// TODO: add further initializations if necessary
		hmap.put('a',0);hmap.put('b',1);hmap.put('c',2);hmap.put('d',3);hmap.put('e',4);
		hmap.put('f',5);hmap.put('g',6);hmap.put('h',7);hmap.put('i',8);hmap.put('j',9);
		
		
	}
	
	/*******************************************
	 * Game class functions already implemented
	 ******************************************/
	
	@Override
	public boolean addPlayer(Player player) {
		if (!started) {
			players.add(player);
			
			if (players.size() == 2) {
				started = true;
				this.whitePlayer = players.get(0);
				this.blackPlayer = players.get(1);
				nextPlayer = this.whitePlayer;
			}
			return true;
		}
		
		return false;
	}

	@Override
	public String getStatus() {
		if (error) return "Error";
		if (!started) return "Wait";
		if (!finished) return "Started";
		if (surrendered) return "Surrendered";
		if (draw) return "Draw";
		
		return "Finished";
	}
	
	@Override
	public String gameInfo() {
		String gameInfo = "";
		
		if(started) {
			if(blackGaveUp()) gameInfo = "black gave up";
			else if(whiteGaveUp()) gameInfo = "white gave up";
			else if(didWhiteDraw() && !didBlackDraw()) gameInfo = "white called draw";
			else if(!didWhiteDraw() && didBlackDraw()) gameInfo = "black called draw";
			else if(draw) gameInfo = "draw game";
			else if(finished)  gameInfo = blackPlayer.isWinner()? "black won" : "white won";
		}
			
		return gameInfo;
	}	

	@Override
	public int getMinPlayers() {
		return 2;
	}

	@Override
	public int getMaxPlayers() {
		return 2;
	}
	
	@Override
	public boolean callDraw(Player player) {
		
		// save to status: player wants to call draw 
		if (this.started && ! this.finished) {
			player.requestDraw();
		} else {
			return false; 
		}
	
		// if both agreed on draw:
		// game is over
		if(players.stream().allMatch(p -> p.requestedDraw())) {
			this.finished = true;
			this.draw = true;
			whitePlayer.finishGame();
			blackPlayer.finishGame();
		}	
		return true;
	}
	
	@Override
	public boolean giveUp(Player player) {
		if (started && !finished) {
			if (this.whitePlayer == player) { 
				whitePlayer.surrender();
				blackPlayer.setWinner();
			}
			if (this.blackPlayer == player) {
				blackPlayer.surrender();
				whitePlayer.setWinner();
			}
			finished = true;
			surrendered = true;
			whitePlayer.finishGame();
			blackPlayer.finishGame();
			
			return true;
		}
		
		return false;
	}

	/*******************************************
	 * Helpful stuff
	 ******************************************/
	
	/**
	 * 
	 * @return True if it's white player's turn
	 */
	public boolean isWhiteNext() {
		return nextPlayer == whitePlayer;
	}
	
	/**
	 * Switch next player
	 */
	private void updateNext() {
		if (nextPlayer == whitePlayer) nextPlayer = blackPlayer;
		else nextPlayer = whitePlayer;
	}
	
	/**
	 * Finish game after regular move (save winner, move game to history etc.)
	 * 
	 * @param player
	 * @return
	 */
	public boolean finish(Player player) {
		// public for tests
		if (started && !finished) {
			player.setWinner();
			finished = true;
			whitePlayer.finishGame();
			blackPlayer.finishGame();
			
			return true;
		}
		return false;
	}

	public boolean didWhiteDraw() {
		return whitePlayer.requestedDraw();
	}

	public boolean didBlackDraw() {
		return blackPlayer.requestedDraw();
	}

	public boolean whiteGaveUp() {
		return whitePlayer.surrendered();
	}

	public boolean blackGaveUp() {
		return blackPlayer.surrendered();
	}

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 ******************************************/
	
	@Override
	public void setBoard(String state) {
		
		char[] stateArray;
		
		stateArray = state.toCharArray();
		int row = 0;
		int slot = 0;
		for(int i = 0; i<stateArray.length; i++){
			if(stateArray[i]=='/'){
				row++;
				slot=0;
			}
			else{
				if(stateArray[i] >= '1' && stateArray[i]<= '9'){
					for(int x = 0; x < Character.getNumericValue(stateArray[i]);x++){
						board[row][slot]='1';
						slot = slot + 1;
					}
					
				}else{
					
				board[row][slot]=stateArray[i];
				slot++;
				}
			}
		}
		// for case // empty row, fill with '1's.
		for(int i = 0 ; i<board.length; i++){
			for(int x = 0; x<board[i].length; x++){
				if(board[i][x]==0){board[i][x]='1';}
			}
		}
		
		
	}
	
	@Override
	public String getBoard() {
		String s = "";
		for(int i = 0 ; i<board.length; i++){
		int count = 0;
			for(int x = 0; x<board[i].length; x++){
				//s = s + board[i][x];
				if(board[i][x] != '1'){
					if(count!=0){
						s=s+(char)(count + '0');
						count = 0;
					}
				s = s + board[i][x];
				}
				else{count++;}
			}
			if(count!=0){
				s=s+(char)(count + '0');
			}
			if(i!=9){
					s=s+'/';
			}
		}
		
		//TODO: replace with real implementation
		return s;
	}
	
	private boolean checkInput(String moveString){
		
		if(!(hmap.containsKey(moveString.charAt(0)))||
				(!Character.isDigit(moveString.charAt(1)) &&
						Character.getNumericValue(moveString.charAt(1))>=0) ||
				!(hmap.containsKey(moveString.charAt(3)))||
				(!Character.isDigit(moveString.charAt(4))  &&
						Character.getNumericValue(moveString.charAt(4))>=0))
				{System.out.println("false xdd");
					return false;}else{return true;}
	}
	
	private boolean checkCorrectPlayer(char startLocation, Player player){
			if((startLocation=='w'&&this.whitePlayer==player)||(startLocation=='b' && this.blackPlayer==player)){
					return true;}else{return false;}
	}
	
	private boolean checkBasicMove( int start1, int start2, int goal1,int goal2
 ){		char startLocation = board[start1][start2];
 		char goalLocation = board[goal1][goal2];
		
			//Test Basic Move White
			if(startLocation == 'w' && goalLocation == 0&& (start2 == goal2-1 && (start1== goal1 || start1 == goal1-1 || start1 == goal1+1))){
				return true;
			//Test Basic Move Black
			}else if(startLocation == 'b' && goalLocation == 0&&(start2 == goal2+1 && (start1== goal1 || start1 == goal1-1 || start1 == goal1+1))){
				return true;
			}else{
				return false;
			}
	}
	
	// Retreat
	private boolean checkRetreatMove( int start1, int start2, int goal1,int goal2){
		char startLocation = board[start1][start2];
 		char goalLocation = board[goal1][goal2];
			//Test Retreat Move White
		if(startLocation == 'w' && goalLocation == 0 && start2 == goal2+2 && ((start1 == goal1 && board[start1][start2+1]==0) ||( start1 == goal1+2 && board[start1+1][start2+1]==0) || (start1==goal1-2 && board[start1-1][start2+1]==0))){
			if(board[start1+1][start2]=='b'||board[start1-1][start2]=='b'||board[start1+1][start2+1]=='b'
			||board[start1][start2+1]=='b'||board[start1-1][start2+1]=='b'||board[start1+1][start2-1]=='b'
			||board[start1][start2-1]=='b'||board[start1+1][start2-1]=='b'){
				
				return true;
			}
			//Test Retreat Move Black
		}else if(startLocation == 'b' && goalLocation == 0 && start2 == goal2-2 && ((start1 == goal1 && board[start1][start2+1]==0)|| (start1 == goal1+2 && board[start1+1][start2+1]==0) || (start1==goal1-2 && board[start1-1][start2+1]==0))){
			if(board[start1+1][start2]=='w'||board[start1-1][start2]=='w'||board[start1+1][start2+1]=='w'
			||board[start1][start2+1]=='w'||board[start1-1][start2+1]=='w'||board[start1+1][start2-1]=='w'
			||board[start1][start2-1]=='w'||board[start1+1][start2-1]=='w'){
				return true;
			}
		}
		return false;
	}
	
	private boolean checkCannonMoveWhite( int start1, int start2, int goal1,int goal2){
		char startLocation = board[start1][start2];
 		char goalLocation = board[goal1][goal2];
		if(startLocation == 'w'&&goalLocation == 0){
			if(start1==goal1&&start2==goal2+3 && board[start1][start2+1]=='w' && board[start1][start2+2]=='w'){
				return true;
			}else if (start1==goal1&&start2==goal2-3 &&  board[start1][start2-1]=='w' && board[start1][start2-2]=='w'){
				return true;
			}else if(start1==goal1+3 &&start2==goal2 &&  board[start1+1][start2]=='w' && board[start1+2][start2]=='w'){
				return true;
			}else if(start1==goal1-3&&start2==goal2 &&  board[start1-1][start2]=='w' && board[start1-2][start2]=='w'){
				return true;
			}else if(start1==goal1+3 &&start2==goal2+3 && board[start1+1][start2+1]=='w' && board[start1+2][start2+2]=='w'){
				return true;
			}else if(start1==goal1+3&&start2==goal2-3 && board[start1+1][start2-1]=='w' && board[start1+2][start2-2]=='w'){
				return true;
			}else if(start1==goal1-3 &&start2==goal2-3 && board[start1-1][start2-1]=='w' && board[start1-2][start2-2]=='w'){
				return true;
			}else if(start1==goal1-3&&start2==goal2+3 && board[start1-1][start2+1]=='w' && board[start1-2][start2+2]=='w'){
				return true;
			}
			}
		return false;}
	
	private boolean checkCannonMoveBlack( int start1, int start2, int goal1,int goal2){
		char startLocation = board[start1][start2];
 		char goalLocation = board[goal1][goal2];
		if(startLocation == 'b' && goalLocation == 0){
			if(start1==goal1 && start2==goal2+3 && board[start1][start2+1]=='b' && board[start1][start2+2]=='b'){
				return true;
			}else if (start1==goal1 && start2==goal2-3 &&  board[start1][start2-1]=='b' && board[start1][start2-2]=='b'){
				return true;
			}else if(start1==goal1+3 && start2==goal2 &&  board[start1+1][start2]=='b' && board[start1+2][start2]=='b'){
				return true;
			}else if(start1==goal1-3 && start2==goal2 &&  board[start1-1][start2]=='b' && board[start1-2][start2]=='b'){
				return true;
			}else if(start1==goal1+3 && start2==goal2+3 && board[start1+1][start2+1]=='b' && board[start1+2][start2+2]=='b'){
				return true;
			}else if(start1==goal1+3 && start2==goal2-3 && board[start1+1][start2-1]=='b' && board[start1+2][start2-2]=='b'){
				return true;
			}else if(start1==goal1-3 && start2==goal2-3 && board[start1-1][start2-1]=='b' && board[start1-2][start2-2]=='b'){
				return true;
			}else if(start1==goal1-3 && start2==goal2+3 && board[start1-1][start2+1]=='b' && board[start1-2][start2+2]=='b'){
				return true;
			}
		}
		return false;
	}
	
	private boolean checkBasicHit( int start1, int start2, int goal1,int goal2){
		char startLocation = board[start1][start2];
 		char goalLocation = board[goal1][goal2];
 		if(startLocation == 'w' && (goalLocation =='s'||goalLocation == 'S')){
			if(start2 == goal2-1 && (start1== goal1 || start1 == goal1-1 || start1 == goal1+1)||(start2 == goal2&&(start1==goal1-1 ||start1==goal1+1))){
					return true;
				}
		}
		if(startLocation == 'b' && (goalLocation =='w'||goalLocation == 'W')){
			if(start2 == goal2+1 && (start1== goal1 || start1 == goal1-1 || start1 == goal1+1)||(start2 == goal2&&(start1==goal1-1 ||start1==goal1+1))){
					return true;
				}
		}
	return false;
	}
	
	private boolean checkCannonShotWhite( int start1, int start2, int goal1,int goal2){
		char startLocation = board[start1][start2];
 		char goalLocation = board[goal1][goal2];
		if(startLocation == 'w'&& (goalLocation =='s'||goalLocation == 'S')){
			if(start1==goal1&&start2==goal2+4|| start1==goal1&&start2==goal2+5 && board[start1][start2+1]=='w' && board[start1][start2+2]=='w'&& board[start1][start2+3]==0){
				return true;
			}else if (start1==goal1 && start2==goal2-4 || start1==goal1 && start2==goal2-5 && board[start1][start2-1]=='w' && board[start1][start2-2]=='w'&& board[start1][start2-3]==0){
				return true;
			}else if(start1==goal1+4 && start2==goal2 || start1==goal1+5 &&start2==goal2 && board[start1+1][start2]=='w' && board[start1+2][start2]=='w'&& board[start1+3][start2]==0){
				return true;
			}else if(start1==goal1-4 && start2==goal2 || start1==goal1-5 && start2==goal2 && board[start1-1][start2]=='w' && board[start1-2][start2]=='w'&& board[start1-3][start2]==0){
				return true;
			}else if(start1==goal1+4 && start2==goal2+4 || start1==goal1+5 && start2==goal2+5 &&  board[start1+1][start2+1]=='w' && board[start1+2][start2+2]=='w'&& board[start1+3][start2+3]==0){
				return true;
			}else if(start1==goal1+4 && start2==goal2-4 || start1==goal1+5 && start2==goal2-5 && board[start1+1][start2-1]=='w' && board[start1+2][start2-2]=='w'&& board[start1+3][start2-3]==0){
				return true;
			}else if(start1==goal1-4 && start2==goal2-4 || start1==goal1-5 && start2==goal2-5 && board[start1-1][start2-1]=='w' && board[start1-2][start2-2]=='w'&& board[start1-3][start2-3]==0){
				return true;
			}else if(start1==goal1-4 && start2==goal2+4 || start1==goal1-5 && start2==goal2+5 && board[start1-1][start2+1]=='w' && board[start1-2][start2+2]=='w'&& board[start1-3][start2+3]==0){
				return true;
			}
		}
	return false;
	}
	
	private boolean checkCannonShotBlack( int start1, int start2, int goal1,int goal2){
		char startLocation = board[start1][start2];
 		char goalLocation = board[goal1][goal2];
		if(startLocation == 'b'&& (goalLocation =='w'||goalLocation == 'W')){
			if(start1==goal1&&start2==goal2+4|| start1==goal1&&start2==goal2+5 && board[start1][start2+1]=='b' && board[start1][start2+2]=='b'&& board[start1][start2+3]==0){
				return true;
			}else if (start1==goal1&&start2==goal2-4 || start1==goal1&&start2==goal2-5 && board[start1][start2-1]=='b' && board[start1][start2-2]=='b'&& board[start1][start2-3]==0){
				return true;
			}else if(start1==goal1+4 &&start2==goal2 || start1==goal1+5&&start2==goal2 && board[start1+1][start2]=='b' && board[start1+2][start2]=='b'&& board[start1+3][start2]==0){
				return true;
			}else if(start1==goal1-4&&start2==goal2 || start1==goal1-5&&start2==goal2 && board[start1-1][start2]=='b' && board[start1-2][start2]=='b'&& board[start1-3][start2]==0){
				return true;
			}else if(start1==goal1+4 &&start2==goal2+4 || start1==goal1+5&&start2==goal2+5 &&  board[start1+1][start2+1]=='b' && board[start1+2][start2+2]=='b'&& board[start1+3][start2+3]==0){
				return true;
			}else if(start1==goal1+4&&start2==goal2-4 || start1==goal1+5&&start2==goal2-5 && board[start1+1][start2-1]=='b' && board[start1+2][start2-2]=='b'&& board[start1+3][start2-3]==0){
				return true;
			}else if(start1==goal1-4 &&start2==goal2-4 || start1==goal1-5&&start2==goal2-5 && board[start1-1][start2-1]=='b' && board[start1-2][start2-2]=='b'&& board[start1-3][start2-3]==0){
				return true;
			}else if(start1==goal1-4&&start2==goal2+4 || start1==goal1-5&&start2==goal2+5 && board[start1-1][start2+1]=='b' && board[start1-2][start2+2]=='b'&& board[start1-3][start2+3]==0){
				return true;
			}
		}
	return false;
	}
	
	private boolean setCity(int start1, int start2, Player player){
		if(player ==this.whitePlayer){
			if(start1<9&&start1>0&&start2==9){
				board[start1][start2]='W';
				return true;
			}
			
		}else if(player == this.blackPlayer){
			if(start1<9&&start1>0&&start2==0){
				board[start1][start2]='B';
				return true;
			}
		}
		
			return false;
	}
	private boolean checkCity(Player player){
		if(player==this.whitePlayer){
			for(int i = 0 ; i<board.length; i++){
				for(int x = 0; x<board[i].length; x++){
					if(board[i][x]=='W'){return true;}
				}
			}
		}else if(player==this.whitePlayer){
			for(int i = 0 ; i<board.length; i++){
				for(int x = 0; x<board[i].length; x++){
					if(board[i][x]=='B'){return true;}
				}
			}
		}
		return false;
	}
	private boolean move(int start1, int start2, int goal1,int goal2){
		board[goal1][goal2]=board[start1][start2];
		board[start1][start2]='1';
		return true;
	}
	private boolean kill(int goal1,int goal2, Player player){
		if((player==this.whitePlayer &&board[goal1][goal2]=='B')
			||(player==this.blackPlayer &&board[goal1][goal2]=='W')){
			player.isWinner();
			this.finished=true;
			return true;
		}
		
		board[goal1][goal2]='1';
		return true;
	}

	private boolean checkMoveLeft(Player player){
		if(player==this.whitePlayer){
			for(int i = 0 ; i<board.length; i++){
				for(int x = 0; x<board[i].length; x++){
					if(board[i][x]=='w'){
						for(int ii = 0 ; ii<board.length; ii++){
							for(int xx = 0; xx<board[ii].length; xx++){
								if(checkMoves(i,x,ii,xx)||
								checkCannonShotWhite( i,x,ii,xx)
								||checkCannonShotBlack( i,x,ii,xx )||
								checkBasicHit( i,x,ii,xx )){return true;}
							}
						}
					}
				}
			}
		}else if(player==this.whitePlayer){
			for(int i = 0 ; i<board.length; i++){
				for(int x = 0; x<board[i].length; x++){
					if(board[i][x]=='b'){
						for(int ii = 0 ; ii<board.length; ii++){
							for(int xx = 0; xx<board[ii].length; xx++){
								if(checkMoves(i,x,ii,xx)||
								checkCannonShotWhite( i,x,ii,xx)
								||checkCannonShotBlack( i,x,ii,xx )||
								checkBasicHit( i,x,ii,xx )){return true;}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	private boolean checkMoves ( int start1, int start2, int goal1,int goal2){
		if(checkBasicMove( start1, start2,  goal1, goal2 )){
		
			return move( start1, start2,  goal1, goal2 );
		}
		if(checkRetreatMove( start1, start2,  goal1, goal2 )){
			return move( start1, start2,  goal1, goal2 );
		}
		if(checkCannonMoveWhite(start1, start2,  goal1, goal2 )
			||checkCannonMoveBlack( start1, start2,  goal1, goal2 )){
			return move( start1, start2,  goal1, goal2 );
		}
		return false;
	}

	
	@Override
	public boolean tryMove(String moveString, Player player) {
		//CHECK IF MOVE COMMAND ON BOARD!
		if(!checkInput(moveString)){return false;}
		
		//Example move: "h6-h5"
		int start1 = hmap.get(moveString.charAt(0));
		int start2 = Character.getNumericValue(moveString.charAt(1));
		int goal1 = hmap.get(moveString.charAt(3));
		int goal2 = Character.getNumericValue(moveString.charAt(4));
		
		char startLocation = board[start1][start2];
 		char goalLocation = board[goal1][goal2];
 		
 		//NOTHING or WRONG color selected
 		if(!checkCorrectPlayer(startLocation, player)){return false;}
 		
 		//SET CITY
		if(startLocation == goalLocation){
			//Set city
			if(checkCity(player)){
				return setCity(start1, start2, player);
			}else return false;
		}
		
		
		//ist city set f�r W and B
		if(checkCity(player)){}
		
		
		//////////////////////////
		// VALID???
		//////////////////////////
		
		if(checkMoves(start1, start2,  goal1, goal2)){
			move(start1, start2,  goal1, goal2);
			updateNext();
			if(!checkMoveLeft(this.nextPlayer)){
				player.isWinner();
				this.finished=true;
			}
			
		}else if(checkCannonShotWhite( start1, start2,  goal1, goal2 )
				||checkCannonShotBlack( start1, start2,  goal1, goal2 )){
			kill( goal1, goal2,player);
			updateNext();
			if(!checkMoveLeft(this.nextPlayer)){
				player.isWinner();
				this.finished=true;
			}
			
		}else if(checkBasicHit( start1, start2,  goal1, goal2 )){
			move(start1, start2,  goal1, goal2);
			kill( goal1, goal2, player);
			updateNext();
			if(!checkMoveLeft(this.nextPlayer)){
				player.isWinner();
				this.finished=true;
			}
		}
			
	
		///////////////////
		//CAN BE PERFORMED
		///////////////////
		
		
		// UPDATE BOARD STATUS
		
		
		// CHECK IF GAME FINISHED
		//can he move.
		// SET NEXT PLAYER 
		
		return true;
		//TODO: implement
		/**
		 * This method checks if the supplied move is possible to perform 
		 * in the current game state/status and, if so, does it.
		 * The following has to be checked/might be changed:
		 * - it has to be checked if the move can be performed
		 * ---- it is a valid move
		 * ---- it is done by the right player
		 * ---- there is no other move that the player is forced to perform
		 * - if the move can be performed, the following has to be done:
		 * ---- the board state has to be updated (e.g. figures moved/deleted)
		 * ---- the board status has to be updated (check if game is finished)
		 * ---- the next player has to be set (if move is over, it's next player's turn)
		 * ---- history is updated
		 * 
		 * @param move String representation of move
		 * @param player The player that tries the move
		 * @return true if the move was performed
		 */
		//return false;
	}
	

}