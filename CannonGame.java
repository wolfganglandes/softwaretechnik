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
		setBoard("/1w1w1w1w1w/1w1w1w1w1w/1w1w1w1w1w///b1b1b1b1b1/b1b1b1b1b1/b1b1b1b1b1/");
		
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
		for( int i = 0; i < board.length; i++ )
			   Arrays.fill( board[i], '1' );
		char[] stateArray = state.toCharArray();
		int row = 9;
		int slot = 0;
		for(int i =0; i< stateArray.length; i++){
			if(stateArray[i]=='/'){
				row--;
				slot=0;
			}else{if(stateArray[i] >= '1' && stateArray[i]<= '9'){
					for(int x = 0; x < Character.getNumericValue(stateArray[i]);x++){
						board[row][slot]='1';
						slot = slot + 1;
				}}else{
				board[row][slot]=stateArray[i];
				slot++;
				}
			}
		}
	}
	
	@Override
	public String getBoard() {
		String s = "";
		for(int i = board.length-1 ; i>=0; i--){
		int count = 0;
			for(int x = 0; x<board.length; x++){
				if(board[i][x] != '1'){
					if(count!=0){
						s=s+(char)(count + '0');
						count = 0;
					}
				s = s + board[i][x];
				}
				else{count++;}
			}
			if(count!=0 && count !=10){
				s=s+(char)(count + '0');
			}
			if(i!=0){
					s=s+'/';
			}
		}
		return s;
	}
	
	private boolean checkInput(String moveString){
		if(!(hmap.containsKey(moveString.charAt(0)))||
				!Character.isDigit(moveString.charAt(1))  ||
				(!Character.toString(moveString.charAt(2)).equals("-"))||
			!(hmap.containsKey(moveString.charAt(3)))||
				!Character.isDigit(moveString.charAt(4)))
				{return false;}else{return true;}
	}
	
	private boolean checkCorrectPlayer(char startLocation, Player player){
			if(((startLocation=='w')&&this.whitePlayer==player)
					||((startLocation=='b') && this.blackPlayer==player)){
					return true;
					}else{return false;}
	}
	
	private boolean checkBasicMove( int start1, int start2, int goal1,int goal2){
		char startLocation = board[start1][start2];
 		char goalLocation = board[goal1][goal2];
			//Test Basic Move White
			if(startLocation == 'w' && goalLocation == '1'&& (start1-1 == goal1 && (start2== goal2 || start2 == goal2-1 || start2 == goal2+1))){
				return true;
			//Test Basic Move Black
			}else if(startLocation == 'b' && goalLocation == '1'&&(start1+1 == goal1 && (start2== goal2 || start2 == goal2-1 || start2 == goal2+1))){
				return true;
			}else{
				return false;
			}
	}
	
	private boolean checkRetreatMove( int start1, int start2, int goal1,int goal2){
		char startLocation = board[start1][start2];
 		char goalLocation = board[goal1][goal2];
 		try{if(startLocation == 'b' && goalLocation == '1' && start1 == goal1+2 && ((start2 == goal2 && board[start1-1][start2]=='1') ||( start2 == goal2+2 && board[start1-1][start2-1]=='1') || (start2==goal2-2 && board[start1-1][start2+1]=='1'))){
 			if(checkSurrounding(start1, start2, goal1,goal2)){
				return true;
			}
			//Test Retreat Move white
		}}catch(IndexOutOfBoundsException e){} 
 		try{ if(startLocation == 'w' && goalLocation == '1' && start1 == goal1-2 && ((start2 == goal2 && board[start1+1][start2]=='1')|| (start2 == goal2+2 && board[start1+1][start2-1]=='1') || (start2==goal2-2 && board[start1+1][start2+1]=='1'))){
			if(checkSurrounding(start1, start2, goal1,goal2)){
				return true;
			}
		}}catch(IndexOutOfBoundsException e){} 
		return false;
	}
	private boolean checkSurrounding (int start1, int start2, int goal1, int goal2){
		char opp =' ';
		if(board[start1][start2]=='w'){
			opp = 'b';
		}else{
			opp ='w';
		}
		try {if(board[start1][start2+1]==opp){return true;}}catch(IndexOutOfBoundsException e){}
		try {if(board[start1][start2-1]==opp){return true;}}catch(IndexOutOfBoundsException e){}
		try {if(board[start1+1][start2+1]==opp){return true;}}catch(IndexOutOfBoundsException e){}
		try {if(board[start1+1][start2]==opp){return true;}}catch(IndexOutOfBoundsException e){}
		try {if(board[start1+1][start2-1]==opp){return true;}}catch(IndexOutOfBoundsException e){}
		try {if(board[start1-1][start2+1]==opp){return true;}}catch(IndexOutOfBoundsException e){}
		try {if(board[start1-1][start2]==opp){return true;}}catch(IndexOutOfBoundsException e){}
		try {if(board[start1-1][start2-1]==opp){return true;}}catch(IndexOutOfBoundsException e){}
		return false;
	}
	
	private boolean checkCannonMoveWhite( int start1, int start2, int goal1,int goal2){
		char startLocation = board[start1][start2];
 		char goalLocation = board[goal1][goal2];
		if(startLocation == 'w'&&goalLocation == '1'){
			try{ if( start1==goal1 && start2+3==goal2 && board[start1][start2+1]=='w' && board[start1][start2+2]=='w'){
				return true;}}catch(IndexOutOfBoundsException e){} 
			try{ if (start1==goal1 && start2-3==goal2 &&  board[start1][start2-1]=='w' && board[start1][start2-2]=='w'){
				return true;}}catch(IndexOutOfBoundsException e){} 
			try{ if(start1==goal1-3 && start2==goal2 &&  board[start1+1][start2]=='w' && board[start1+2][start2]=='w'){
				return true;}}catch(IndexOutOfBoundsException e){} 
			try{ if(start1==goal1+3 && start2==goal2 &&  board[start1-1][start2]=='w' && board[start1-2][start2]=='w'){
				return true;}}catch(IndexOutOfBoundsException e){} 
			try{ if(start1==goal1+3 && start2==goal2-3 && board[start1-1][start2+1]=='w' && board[start1-2][start2+2]=='w'){
				return true;}}catch(IndexOutOfBoundsException e){}
			try{ if(start1==goal1+3 && start2==goal2+3 && board[start1-1][start2-1]=='w' && board[start1-2][start2-2]=='w'){
				return true;}}catch(IndexOutOfBoundsException e){} 
			try{ if(start1==goal1-3 && start2==goal2-3 && board[start1+1][start2+1]=='w' && board[start1+2][start2+2]=='w'){
				return true;}}catch(IndexOutOfBoundsException e){}
			try{if(start1==goal1-3 && start2==goal2+3 && board[start1+1][start2-1]=='w' && board[start1+2][start2-2]=='w'){
				return true;}}catch(IndexOutOfBoundsException e){} 
			}
		return false;}
	
	private boolean checkCannonMoveBlack( int start1, int start2, int goal1,int goal2){
		char startLocation = board[start1][start2];
 		char goalLocation = board[goal1][goal2];
		if(startLocation == 'b' && goalLocation == '1'){
			try{if(start1==goal1 && start2==goal2+3 && board[start1][start2-1]=='b' && board[start1][start2-2]=='b'){
				return true;} }catch(IndexOutOfBoundsException e){} 
			try{if (start1==goal1 && start2==goal2-3 &&  board[start1][start2+1]=='b' && board[start1][start2+2]=='b'){
				return true;} }catch(IndexOutOfBoundsException e){} 
			try{ if(start1==goal1+3 && start2==goal2 &&  board[start1-1][start2]=='b' && board[start1-2][start2]=='b'){
				return true;}}catch(IndexOutOfBoundsException e){} 
			try{if(start1==goal1-3 && start2==goal2 &&  board[start1+1][start2]=='b' && board[start1+2][start2]=='b'){
				return true;} }catch(IndexOutOfBoundsException e){} 
			try{if(start1==goal1+3 && start2==goal2+3 && board[start1-1][start2-1]=='b' && board[start1-2][start2-2]=='b'){
				return true;} }catch(IndexOutOfBoundsException e){} 
			try{if(start1==goal1+3 && start2==goal2-3 && board[start1-1][start2+1]=='b' && board[start1+2][start2+2]=='b'){
				return true;} }catch(IndexOutOfBoundsException e){} 
			try{if(start1==goal1-3 && start2==goal2-3 && board[start1+1][start2+1]=='b' && board[start1+2][start2+2]=='b'){
				return true;} }catch(IndexOutOfBoundsException e){} 
			try{if(start1==goal1-3 && start2==goal2+3 && board[start1+1][start2-1]=='b' && board[start1+2][start2-2]=='b'){
				return true;} }catch(IndexOutOfBoundsException e){} 
		}
		return false;
	}
	
	private boolean checkBasicHit( int start1, int start2, int goal1,int goal2){
		char startLocation = board[start1][start2];
 		char goalLocation = board[goal1][goal2];
 		if(startLocation == 'w' && (goalLocation =='b'||goalLocation == 'B')){
 			if((start1 == goal1+1 && (start2== goal2 || start2 == goal2-1 || start2 == goal2+1))||(start1 == goal1&&(start2==goal2-1 ||start2==goal2+1))){
 				return true;
				}
		}
		if(startLocation == 'b' && (goalLocation =='w'||goalLocation == 'W')){
			if((start1 == goal1-1 && (start2== goal2 || start2 == goal2-1 || start2 == goal2+1))||(start1 == goal1&&(start2==goal2-1 ||start2==goal2+1))){
					return true;
				}
		}
	return false;
	}
	
	
	private boolean checkCannonShotWhite( int start1, int start2, int goal1,int goal2){
		char startLocation = board[start1][start2];
 		char goalLocation = board[goal1][goal2];
 		if(startLocation == 'w'&& (goalLocation =='b'||goalLocation == 'B')){
			try{if(((start2==goal2&&start1==goal1+4)|| (start2==goal2&&start1==goal1+5)) && board[start1-1][start2]=='w' && board[start1-2][start2]=='w'&& board[start1-3][start2]=='1'){
				return true; }}catch(IndexOutOfBoundsException e){} 
			try{ if (((start2==goal2 && start1==goal1-4 )||( start2==goal2 && start1==goal1-5)) && board[start1+1][start2+1]=='w' && board[start1+2][start2]=='w'&& board[start1+3][start2]=='1'){
				return true; }}catch(IndexOutOfBoundsException e){} 
			try{ if(((start2==goal2+4 && start1==goal1 )||( start2==goal2+5 &&start1==goal1)) && board[start1][start2-1]=='w' && board[start1][start2-2]=='w'&& board[start1][start2-3]=='1'){
				return true;} }catch(IndexOutOfBoundsException e){} 
			try{ if(((start2==goal2-4 && start1==goal1 )||( start2==goal2-5 && start1==goal1)) && board[start1][start2+1]=='w' && board[start1][start2+2]=='w'&& board[start1][start2+3]=='1'){
				return true;}}catch(IndexOutOfBoundsException e){} 
			try{ if(((start2==goal2+4 && start1==goal1+4 )||( start2==goal2+5 && start1==goal1+5)) &&  board[start1-1][start2-1]=='w' && board[start1-2][start2-2]=='w'&& board[start1-3][start2-3]=='1'){
				return true; } }catch(IndexOutOfBoundsException e){} 
			try{ if(((start2==goal2+4 && start1==goal1-4 )||( start2==goal2+5 && start1==goal1-5)) && board[start1+1][start2-1]=='w' && board[start1+2][start2-2]=='w'&& board[start1+3][start2-3]=='1'){
				return true; } }catch(IndexOutOfBoundsException e){} 
			try{ if(((start2==goal2-4 && start1==goal1-4 )||( start2==goal2-5 && start1==goal1-5)) && board[start1+1][start2+1]=='w' && board[start1+2][start2+2]=='w'&& board[start1+3][start2+3]=='1'){
				return true; }}catch(IndexOutOfBoundsException e){} 
			try{ if(((start2==goal2-4 && start1==goal1+4 )||( start2==goal2-5 && start1==goal1+5)) && board[start1-1][start2+1]=='w' && board[start1-2][start2+2]=='w'&& board[start1-3][start2+3]=='1'){
				return true; }}catch(IndexOutOfBoundsException e){} 		
		}
	return false;
	}
	//Check if cannonshot is possible for black
	private boolean checkCannonShotBlack( int start1, int start2, int goal1,int goal2){
		char startLocation = board[start1][start2];
 		char goalLocation = board[goal1][goal2];
		if(startLocation == 'b'&& (goalLocation =='w'||goalLocation == 'W')){
			try{if(((start2==goal2&&start1==goal1+4)||( start2==goal2&&start1==goal1+5)) && board[start1-1][start2]=='b' && board[start1-2][start2]=='b'&& board[start1-3][start2]=='1'){
				return true;}}catch(IndexOutOfBoundsException e){} 
			try{ if(((start2==goal2 && start1==goal1-4 )||( start2==goal2&&start1==goal1-5)) && board[start1+1][start2]=='b' && board[start1+2][start2]=='b'&& board[start1+3][start2]=='1'){
				return true;} }catch(IndexOutOfBoundsException e){} 
			try{ if(((start2==goal2+4 &&start1==goal1 )||( start2==goal2+5&&start1==goal1)) && board[start1][start2-1]=='b' && board[start1][start2-2]=='b'&& board[start1][start2-3]=='1'){
				return true; } }catch(IndexOutOfBoundsException e){} 
			try{ if(((start2==goal2-4&&start1==goal1 )||( start2==goal2-5&&start1==goal1)) && board[start1][start2+1]=='b' && board[start1][start2+2]=='b'&& board[start1][start2+3]=='1'){
				return true;} }catch(IndexOutOfBoundsException e){} 
			try{ if(((start2==goal2+4 &&start1==goal1+4 )||( start2==goal2+5&&start1==goal1+5)) &&  board[start1-1][start2-1]=='b' && board[start1-2][start2-2]=='b'&& board[start1-3][start2-3]=='1'){
				return true;} }catch(IndexOutOfBoundsException e){} 
			try{if(((start2==goal2+4&&start1==goal1-4 )||( start2==goal2+5&&start1==goal1-5)) && board[start1+1][start2-1]=='b' && board[start1+2][start2-2]=='b'&& board[start1+3][start2-3]=='1'){
				return true;} }catch(IndexOutOfBoundsException e){} 
			try{ if(((start2==goal2-4 &&start1==goal1-4 )||( start2==goal2-5&&start1==goal1-5)) && board[start1+1][start2+1]=='b' && board[start1+2][start2+2]=='b'&& board[start1+3][start2+3]=='1'){
				return true;} }catch(IndexOutOfBoundsException e){} 
			try{ if(((start2==goal2-4&&start1==goal1+4 )||( start2==goal2-5&&start1==goal1+5)) && board[start1-1][start2+1]=='b' && board[start1-2][start2+2]=='b'&& board[start1-3][start2+3]=='1'){
				return true;} }catch(IndexOutOfBoundsException e){} 
		}
	return false;
	}
	//Check if city is allowed to be placed and does it if ok 
	private boolean setCity(int start1, int start2, Player player){
		if(player ==this.whitePlayer){
			if(start2<9 && start2>0 && start1==9){
				board[start1][start2]='W';
				return true;
			}
			
		}else 
			if(start2<9&&start2>0&&start1==0){
				board[start1][start2]='B';
				return true;
			}
		
		return false;
	}
	//Guess what, checks city
	private boolean checkCity(Player player){
		if(player==this.whitePlayer){
			for(int i = 0 ; i<board.length; i++){
				for(int x = 0; x<board[i].length; x++){
					if(board[i][x]=='W'){return true;}
				}
			}
		}else 
			for(int i = 0 ; i<board.length; i++){
				for(int x = 0; x<board[i].length; x++){
					if(board[i][x]=='B'){return true;}
				}
			}
		
		return false;
	}
	
	//Move just move nothing more... ;) Set start field to '1'
	private boolean move(int start1, int start2, int goal1,int goal2){
		board[goal1][goal2]=board[start1][start2];
		board[start1][start2]='1';
		return true;
	}
	
	//Check if cannon shot kills City
	private boolean killShot(int goal1,int goal2, Player player){
		if((player==this.whitePlayer &&board[goal1][goal2]=='B')
			||(player==this.blackPlayer &&board[goal1][goal2]=='W')){
			board[goal1][goal2]='1';
			finish(player);
			updateNext();
			return true;
		}
		board[goal1][goal2]='1';
		return false;
	}
	
	//Check if basic hit kills city
	private boolean killHit(int start1, int start2, int goal1,int goal2, Player player){
		if((player==this.whitePlayer &&board[goal1][goal2]=='B')
			||(player==this.blackPlayer &&board[goal1][goal2]=='W')){
			board[goal1][goal2]=board[start1][start2];
			board[start1][start2]='1';
			finish(player);
			updateNext();
			return true;
		}
		return false;
	}
//Find all figures left for player and start checkMoveLeft2
	private boolean checkMoveLeft1(Player player){
			for(int i = 0 ; i<board.length; i++){
				for(int x = 0; x<board[i].length; x++){
					if((board[i][x]=='w'&&player==this.whitePlayer)||(board[i][x]=='b'&&player==this.blackPlayer)){
						if(checkMoveLeft2(i,x,player)){return true;}
					}
				}
			}
		if(player==this.whitePlayer){finish(this.blackPlayer);}
		else{finish(this.whitePlayer);}	
		return false;
	}
	//Check for each start position all goal position if any move is legit
	private boolean checkMoveLeft2(int i, int x, Player player){
		for(int ii = 0 ; ii<board.length; ii++){
			for(int xx = 0; xx<board[ii].length; xx++){
				if(checkCannonShotWhite( i,x,ii,xx)				
				||checkCannonShotBlack( i,x,ii,xx )||
				checkBasicHit( i,x,ii,xx )||checkMoves(i,x,ii,xx)){
					return true;}
				}
			}
	return false;
}
	//Checked Basic moves: Basic move + retreat + CannonMove
	private boolean checkMoves ( int start1, int start2, int goal1,int goal2){
		
		if(checkBasicMove( start1, start2,  goal1, goal2 )){
			return true;
		}
		if(checkRetreatMove( start1, start2,  goal1, goal2 )){
			return true;
		}
		if(checkCannonMoveWhite(start1, start2,  goal1, goal2 )
			||checkCannonMoveBlack( start1, start2,  goal1, goal2 )){
			return true;
		}
		return false;
	}

	//Checked all the possible moves
	private boolean masterChecker ( int start1, int start2, int goal1,int goal2, Player player){
		if(checkMoves(start1, start2,  goal1, goal2)){
			move(start1, start2,  goal1, goal2);
			updateNext();
			checkMoveLeft1(this.nextPlayer);
			return true;
		}else if(checkCannonShotWhite( start1, start2,  goal1, goal2 )
				||checkCannonShotBlack( start1, start2,  goal1, goal2 )){
			if(killShot( goal1, goal2,player)){return true;}
			updateNext();
			checkMoveLeft1(this.nextPlayer);
			return true;
		}else if(checkBasicHit( start1, start2,  goal1, goal2 )){
			if(killHit( start1, start2, goal1, goal2, player)){
				return true;}
			move(start1, start2,  goal1, goal2);
			updateNext();
			checkMoveLeft1(this.nextPlayer);
			return true;
		}return false;
	}
	
	@Override
	public boolean tryMove(String moveString, Player player) {
		String currentBoard=getBoard();
		if(!checkInput(moveString)){return false;}
		int start1 =Character.getNumericValue(moveString.charAt(1));
		int start2 = hmap.get(moveString.charAt(0));
		int goal1= Character.getNumericValue(moveString.charAt(4));
		int goal2 = hmap.get(moveString.charAt(3));
		if(!checkCity(player)){ 
 			if(board[start1][start2] == board[goal1][goal2]){
				if( setCity(start1, start2, player)){
					updateNext();
					return true;
				}else{return false;}
			}else return false;
		}
		if(!checkCorrectPlayer(board[start1][start2], player)){
 			return false;
 		}
 		//Beware the incredible masterChecker!!!
 		if(masterChecker(start1, start2,  goal1, goal2, player)){
 			history.add(new Move(moveString,currentBoard,player));
 			return true;
 		}else{return false;}	
	}

}
