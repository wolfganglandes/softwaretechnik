package de.tuberlin.sese.swtpp.gameserver.test.cannon;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.control.GameController;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.User;
import de.tuberlin.sese.swtpp.gameserver.model.cannon.CannonGame;

public class TryMoveTest {

	User user1 = new User("Alice", "alice");
	User user2 = new User("Bob", "bob");
	
	Player whitePlayer = null;
	Player blackPlayer = null;
	CannonGame game = null;
	GameController controller;
	
	@Before
	public void setUp() throws Exception {
		controller = GameController.getInstance();
		controller.clear();
		
		int gameID = controller.startGame(user1, "");
		
		game = (CannonGame) controller.getGame(gameID);
		whitePlayer = game.getPlayer(user1);

	}
	
	public void startGame(String initialBoard, boolean whiteNext) {
		controller.joinGame(user2);		
		blackPlayer = game.getPlayer(user2);
		
		game.setBoard(initialBoard);
		game.setNextPlayer(whiteNext? whitePlayer:blackPlayer);
	}
	
	public void assertMove(String move, boolean white, boolean expectedResult) {
		if (white)
			assertEquals(expectedResult, game.tryMove(move, whitePlayer));
		else 
			assertEquals(expectedResult,game.tryMove(move, blackPlayer));
	}
	
	public void assertGameState(String expectedBoard, boolean whiteNext, boolean finished, boolean whiteWon) {
		assertEquals(expectedBoard,game.getBoard().replaceAll("e", ""));
		assertEquals(whiteNext, game.isWhiteNext());

		assertEquals(finished, game.isFinished());
		if (!game.isFinished()) {
			assertEquals(whiteNext, game.isWhiteNext());
		} else {
			assertEquals(whiteWon, whitePlayer.isWinner());
			assertEquals(!whiteWon, blackPlayer.isWinner());
		}
	}
	

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 *******************************************/
	
	@Test
	public void exampleTest() {
		startGame("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w1w1w/2w7/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",true);
		assertMove("h6-h5",true,true);
		assertGameState("5W4/1w1w1w1w1w/1w1w1w1w1w/1w3w3w/2w4w2/5b4/b1b3b1b1/b1b1b1b1b1/b1b1b1b1b1/3B6",false,false,false);
	}

	//TODO: implement test cases of same kind as example here
	
	//-------------------------CITY TESTS--------------------------------------------------------
	//t0: Test to move city FRONT / SIDE
	@Test
	public void moveCityR() {
		startGame("4W5/1w1w1w1w1w/1w1w1w3w/1w3www1w//7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true);
		assertMove("e9-f9",true,false);
		assertGameState("4W5/1w1w1w1w1w/1w1w1w3w/1w3www1w//7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true,false,false);
	}
	
	@Test
	public void moveCityF() {
		startGame("4W5/1w1w1w1w1w/1w1w1w3w/1w3www1w//7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true);
		assertMove("e9-e8",true,false);
		assertGameState("4W5/1w1w1w1w1w/1w1w1w3w/1w3www1w//7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true,false,false);
	}
	
	//-----------------------SOLDIER TESTS----------------------------------------------------
	
	//t1: Testing soldier movement: (1) front, (2) diagonal R/L, (3) obstacle opponent case 1-3, (4) unvalid move 
	//-----------(1) soldier front-------------------##
	@Test
	public void moveSoldierF() {
		startGame("4W5/1w1w1w1w1w/1w1w1w3w/1w3www1w//7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true);
		assertMove("b6-b5",true,true);
		assertGameState("4W5/1w1w1w1w1w/1w1w1w3w/5www1w/1w8/7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",false,false,false);
	}
	
	//-----------(2) soldier diagonal-----------------##
	@Test
	public void moveSoldierDR() {
		startGame("4W5/1w1w1w1w1w/1w1w1w3w/1w3www1w//7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true);
		assertMove("f6-g5",true,true);
		assertGameState("4W5/1w1w1w1w1w/1w1w1w3w/1w4ww1w/6w3/7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",false,false,false);
	}
	
	@Test
	public void moveSoldierDL() {
		startGame("4W5/1w1w1w1w1w/1w1w1w3w/1w3www1w//7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true);
		assertMove("f6-e5",true,true);
		assertGameState("4W5/1w1w1w1w1w/1w1w1w3w/1w4ww1w/4w5/7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",false,false,false);
	}
	
	//-----------(3) soldier obstacles white-----------------##  ||W  SCENARIO ALSO NEEDED FOR BLACK **
	@Test
	public void moveSoldierObst1F() {
		startGame("4W5/1w1w1w1w1w/1w1w1w3w/1w3www1w//7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true);
		assertMove("b7-b6",true,false);
		assertGameState("4W5/1w1w1w1w1w/1w1w1w3w/1w3www1w//7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true,false,false);
	}
	
	@Test
	public void moveSoldierObst2DR() {
		startGame("4W5/1w1w1w1w1w/1w1w1w3w/1w3www1w//7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true);
		assertMove("f7-g6",true,false);
		assertGameState("4W5/1w1w1w1w1w/1w1w1w3w/1w3www1w//7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true,false,false);
	}
	
	@Test
	public void moveSoldierObst3DL() {
		startGame("4W5/1w1w1w1w1w/1w1w1w3w/1w2ww1w1w//7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true);
		assertMove("f7-e6",true,false);
		assertGameState("4W5/1w1w1w1w1w/1w1w1w3w/1w2ww1w1w//7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true,false,false);
	}
	
	
	//-----------(4) soldier move right, expect: false-----------------## 
	@Test
	public void moveSoldierToRight() {
		startGame("4W5/1w1w1w1w1w/1w1w1w3w/1w2ww1w1w//7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true);
		assertMove("h4-g4",true,false);
		assertGameState("4W5/1w1w1w1w1w/1w1w1w3w/1w2ww1w1w//7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true,false,false);
	}
	
	
	//-------------------------------RETREAT-----------------------------------------
	//t3: test retreat to back/ diagonal back 2 steps: (TRUE) - enemy is a threat 
	
	//-----2 straight back ##
	@Test
	public void retreatBacktrue() {
		startGame("4W5/1w1w1w1w1w/1w1w1w3w/1w3ww1ww//7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true);
		assertMove("h4-h6",true,true);
		assertGameState("4W5/1w1w1w1w1w/1w1w1w3w/1w3wwwww///b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",false,false,false);
	}
	
	//-----2 diagonal right back ##
	@Test
	public void retreatDRtrue() {
		startGame("4W5/1w1w1w1w1w/1w1w1w3w/5www1w//1w5w2/2bb2bbb1/bbb3b1b1/b1b1b3bb/2B7",true);
		assertMove("b4-d6",true,true); //forgot the move 
		assertGameState("4W5/1w1w1w1w1w/1w1w1w3w/3w1www1w//7w2/2bb2bbb1/bbb3b1b1/b1b1b3bb/2B7",false,false,false);
	}
	
	//-----2 diagonal left back ##
	@Test
	public void retreatDLtrue() {
		startGame("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w//7w2/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true);
		assertMove("h4-f6",true,true);
		assertGameState("4W5/1w2ww1w1w/1w1w1w3w/1w2wwww1w///b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",false,false,false);
	}
	
	//-------------------------------BLOCKED RETREAT-----------------------------------------
	// expected: false 
	//----- Retreat with 2 steps is blocked --> own player (1)diagonal (2)straight back ##
	@Test
	public void retreatBackIsBlockedDw() {
		startGame("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w//4b5/b1bb1wbbb1/b1b3b1b1/b1b1b3bb/2B7",true);
		assertMove("f3-d5",true,false);
		assertGameState("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w//4b5/b1bb1wbbb1/b1b3b1b1/b1b1b3bb/2B7",true,false,false);
	}
	
	@Test
	public void retreatBackIsBlockedSw() {
		startGame("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w//5b4/b1bb1wbbb1/b1b3b1b1/b1b1b3bb/2B7",true);
		assertMove("f3-f5",true,false);
		assertGameState("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w//5b4/b1bb1wbbb1/b1b3b1b1/b1b1b3bb/2B7",true,false,false);
	}
		
	//IMPORTANT --> can't kill to the back 
	//-----Wrong retreat with 1 step is blocked --> own player (1)diagonal (2)straight back ##
	@Test
	public void wrongRetreatBackIsBlockedDw() {
			startGame("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w//4b5/b1bb1wbbb1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("f3-e4",true,false);
			assertGameState("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w//4b5/b1bb1wbbb1/b1b3b1b1/b1b1b3bb/2B7",true,false,false);
	}
		
	@Test
	public void wrongRetreatBackIsBlockedSw() {
			startGame("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w//5b4/b1bb1wbbb1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("f3-f4",true,false);
			assertGameState("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w//5b4/b1bb1wbbb1/b1b3b1b1/b1b1b3bb/2B7",true,false,false);
	}		

	//-------------------------------DEATHMATCH KILL-MODE-----------------------------------------
	// expected: true 
	//--------SOLDIER WAR---------------
	@Test //##
	public void killEnemyFront() {
			startGame("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w/5w4/5b4/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("f5-f4",true,true);
			assertGameState("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w//5w4/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",false,false,false);
	}		
	
	@Test //##
	public void killEnemyLEFT() {
			startGame("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w//6b3/b1bw2bbb1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("d3-c3",true,true);
			assertGameState("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w//6b3/b1w3bbb1/b1b3b1b1/b1b1b3bb/2B7",false,false,false);
	}		
	
	@Test //##
	public void killEnemyRIGHT() {
			startGame("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w///b1bb1wbbb1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("f3-g3",true,true);
			assertGameState("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w///b1bb2wbb1/b1b3b1b1/b1b1b3bb/2B7",false,false,false);
	}	
	
	@Test //##
	public void killEnemyDiagonalL() {
			startGame("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w//4w1b3/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("e4-d3",true,true);
			assertGameState("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w//6b3/b1bw2bbb1/b1b3b1b1/b1b1b3bb/2B7",false,false,false);
	}	
	
	@Test //##
	public void killEnemyDiagonalR() {
			startGame("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w//5w4/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("f4-g3",true,true);
			assertGameState("4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w///b1bb2wbb1/b1b3b1b1/b1b1b3bb/2B7",false,false,false);
	}	
	
	
	//--------CANNON WAR----------------
	
	// -- Killing front enemy, distance (1):false, (2):true, (3):true
	@Test //##
	public void cannonKillFront1() {
			startGame("4W5/1w2ww1w1w/1w1w1w1w1w/1w2w1ww1w/7b2//b1bb2b1b1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("h8-h5",true,false);
			assertGameState("4W5/1w2ww1w1w/1w1w1w1w1w/1w2w1ww1w/7b2//b1bb2b1b1/b1b3b1b1/b1b1b3bb/2B7",true,false,false);
	}	
	
	@Test //##
	public void cannonKillFront2() {
			startGame("4W5/1w2ww1w1w/1w1w1w1w1w/1w2w1ww1w//7b2/b1bb2b1b1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("h8-h4",true,true);
			assertGameState("4W5/1w2ww1w1w/1w1w1w1w1w/1w2w1ww1w///b1bb2b1b1/b1b3b1b1/b1b1b3bb/2B7",false,false,false);
	}	
	
	@Test //##
	public void cannonKillFront3() {
			startGame("4W5/1w2ww1w1w/1w1w1w1w1w/1w2w1ww1w///b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("h8-h3",true,true);
			assertGameState("4W5/1w2ww1w1w/1w1w1w1w1w/1w2w1ww1w///b1bb2b1b1/b1b3b1b1/b1b1b3bb/2B7",false,false,false);
	}	
	
	// -- Killing diagonal enemy, distance (1):false, (2):true, (3):true
	@Test //##
	public void cannonKillDiagonal1() {
			startGame("4W5/1w2ww3w/1w1w1w1w1w/1w2w1ww1w/5w4/4b2b2/b1b3b1b1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("h7-e4",true,false);
			assertGameState("4W5/1w2ww3w/1w1w1w1w1w/1w2w1ww1w/5w4/4b2b2/b1b3b1b1/b1b3b1b1/b1b1b3bb/2B7",true,false,false);
	}	
	
	@Test //##
	public void cannonKillDiagonal2() {
			startGame("4W5/1w2ww3w/1w1w1w1w1w/1w2w1ww1w/5w4/7b2/b1bb2b1b1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("h7-d3",true,true);
			assertGameState("4W5/1w2ww3w/1w1w1w1w1w/1w2w1ww1w/5w4/7b2/b1b3b1b1/b1b3b1b1/b1b1b3bb/2B7",false,false,false);
	}	
	
	@Test //##
	public void cannonKillDiagonal3() {
			startGame("4W5/1w2ww3w/1w1w1w1w1w/1w2w1ww1w/5w4/3b3b2/b1b3b1b1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("h7-c2",true,true);
			assertGameState("4W5/1w2ww3w/1w1w1w1w1w/1w2w1ww1w/5w4/3b3b2/b1b3b1b1/b5b1b1/b1b1b3bb/2B7",false,false,false);
	}	
	
	// -- Killing with the other end of cannon, distance (1):false, (2):true, (3):true
	@Test //##
	public void cannonKillBack1() {
			startGame("4W5/1w2ww3w/1w1w1w1b1w/1w2w1ww1w/5w4/4w2b2/b1b3b1b1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("e4-h7",true,false);
			assertGameState("4W5/1w2ww3w/1w1w1w1b1w/1w2w1ww1w/5w4/4w2b2/b1b3b1b1/b1b3b1b1/b1b1b3bb/2B7",true,false,false);
	}	
	
	@Test //##
	public void cannonKillBack2() {
			startGame("4W5/1w2ww2bw/1w1w1w3w/1w2w1ww1w/5w4/4w2b2/b1b3b1b1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("e4-i8",true,true);
			assertGameState("4W5/1w2ww3w/1w1w1w3w/1w2w1ww1w/5w4/4w2b2/b1b3b1b1/b1b3b1b1/b1b1b3bb/2B7",false,false,false);
	}	
	
	@Test //##
	public void cannonKillBack3() {
			startGame("4W4b/1w2ww3w/1w1w1w3w/1w2w1ww1w/5w4/4w2b2/b1b3b1b1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("e4-j9",true,true);
			assertGameState("4W5/1w2ww3w/1w1w1w3w/1w2w1ww1w/5w4/4w2b2/b1b3b1b1/b1b3b1b1/b1b1b3bb/2B7",false,false,false);
	}	
	
	//Black cannon 4W5/1w2ww1w1w/1w1w1w3w/1w2w1ww1w//4w1b3/b1bb2bbb1/b1b3b1b1/b1b1b3bb/2B7
	//-------------------------------DEATHMATCH KILL-FAIL-----------------------------------------
	// expected: false 
	//--------SOLDIER WAR---------------
	//--------CANNON WAR----------------
	
	//-------------------------------CANNON MOVEMENT---------------------------------------------
	// expected: true 
	@Test //##
	public void moveCannonFront() {
			startGame("4W5/1w2w4w/1w1w1w1w1w/1w2wwww1w/5w4/3b3b2/b1b3b1b1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("f7-f4",true,true);
			assertGameState("4W5/1w2w4w/1w1w3w1w/1w2wwww1w/5w4/3b1w1b2/b1b3b1b1/b1b3b1b1/b1b1b3bb/2B7",false,false,false);
	}	
	
	@Test //##
	public void moveCannonDiagonal() {
			startGame("4W5/1w2w4w/1w1w3w1w/1w2wwww1w/5w4/3b1w1b2/b1b3b1b1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("h7-e4",true,true);
			assertGameState("4W5/1w2w4w/1w1w5w/1w2wwww1w/5w4/3bww1b2/b1b3b1b1/b1b3b1b1/b1b1b3bb/2B7",false,false,false);
	}
	
	
	
	//-------------------------------UNVALID RANDOM MOVEMENT-----------------------------------------
	//expected: false 
	//walking out of the field, moving n>1 step for soldier
	
	//---(1) Random step with Soldier out of board
	@Test 
	public void stepOutOfBoardSoldier() {
			startGame("4W5/1w2w4w/1w1w1w1w1w/1w2wwww1w/5w4/3b3b2/b1b3b1b1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("j6-xx",true,false);
			assertGameState("4W5/1w2w4w/1w1w1w1w1w/1w2wwww1w/5w4/3b3b2/b1b3b1b1/b1b3b1b1/b1b1b3bb/2B7",true,false,false);
	}	
	
	//---(2) Random step with Cannon out of board
	@Test 
	public void stepOutOfBoardCannon() {
			startGame("4W5/1w2w4w/1w1w1w1w1w/1w2ww1www/5w4/3b3b2/b1b3b1b1/b1b3b1b1/b1b1b3bb/2B7",true);
			assertMove("h6-xx",true,false);
			assertGameState("4W5/1w2w4w/1w1w1w1w1w/1w2ww1www/5w4/3b3b2/b1b3b1b1/b1b3b1b1/b1b1b3bb/2B7",true,false,false);
	}	
	
	//---(3) Last Wall try Step 
	
	
	//-------------------------------WINNER - KILLING CITY---------------------------------------
	//---WHITE kills Bcity >> wins game, finished
	public void whiteKillsCity() {
		startGame("4W5/1w2w4w////3b3b2///2w6b/2B7",true);
		assertMove("c1-c0",true,true);
		assertGameState("4W5/1w2w4w////3b3b2///9b/2w7",false,true,true);
   }	

	//---BLACK kills Wcity >> wins game, finished 
	public void blackKillsCity() {
		startGame("4W5/1w1b5w////3b3b2///2b6b/2B7",true);
		assertMove("d8-e9",true,true);
		assertGameState("4b5/1w7w////3b3b2///2b6b/2B7",false,true,false);
   }	
	
	
	//------------------------------END OF GAME-----------------------------------
	//no moves valid anymore 
	
	
	//Some black soldier movement
	//---front move Bsoldier 
	public void moveSoldierBlackF() {
		startGame("4W5/1w2w4w////3b3b2///2w6b/2B7",true);
		assertMove("c1-c0",true,true);
		assertGameState("4W5/1w2w4w///7b2/3b6///2w6b/2B7",true,false,false);
   }
	
	//---diagonal move Bsoldier 
	public void moveSoldierBlackD() {
		startGame("4W5/1w2w4w////3b3b2///2w6b/2B7",true);
		assertMove("c1-c0",true,true);
		assertGameState("4W5/1w2w4w///8b1/3b6///2w6b/2B7",true,false,false);
   }
	
}
