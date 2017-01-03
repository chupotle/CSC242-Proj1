import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
public class Board {
	Player currentTurn;
	Player user;
	Player vs;
	Player[][][] board;
	String[][][] v;
	int currBoard;
	boolean over;
	public Board(){
		Scanner cont;
		String Cont="";
		for(;; ) {
			Game();
		}
	}
	public void Game(){
		currBoard=0;
		over=false;
		currentTurn=Player.X;
		board = new Player[10][3][3];
		v = new String[10][3][3];
		System.err.println("Would you like to be \"X\" or \"O\"?");
		Scanner input = new Scanner(System.in);
		String usr = "";
		do {
			usr = input.nextLine().toUpperCase();
			if(!usr.equals("X")&&!usr.equals("O"))
				System.err.println("Invalid input");
		} while(!usr.equals("X")&&!usr.equals("O"));
		switch (usr) {
		case ("X"):
			user = Player.X;
			vs = Player.O;
			break;
		case ("O"):
			user = Player.O;
			vs = Player.X;
			break;
		}
		for(int w=0; w<10; w++) {
			for(int i=0; i<3; i++) {
				for(int j=0; j<3; j++) {
					board[w][i][j]=Player.Q;
					v[w][i][j]=String.valueOf((i*3)+(j+1));
				}
			}
		}

		printBoard();
		System.err.println("X starts");
		while(!over) {
			if(user==currentTurn) {
				int b=currBoard;
				if(currBoard==0) {
					System.err.println("Enter the board you want to mark");
					b=input.nextInt();
					currBoard=b;
				}
				System.err.println("Enter the position you want to mark");
				System.err.println("Currently in board "+currBoard);
				ArrayList<Integer> used = getMoves(b);
				int i=input.nextInt();
				if(used.indexOf(i)!=-1) {
					mark(user,b,i);
				}
				else{
					System.err.println("Invalid input");
					changeTurn();
				}
			}
			else{
				int[] vsMov=mm();
				mark(vs,vsMov[1],vsMov[2]);
			}
			changeTurn();
			System.err.println("\n\n\n");
			printBoard();
			checkWin();
		}
		if(checkWin()!=null) {
			System.err.println(checkWin().toString()+" won");
		}
		else{
			System.err.println("Draw");
		}
		System.err.println("\n\n\n");
	}
	public ArrayList<Integer> getMoves(int b){
		ArrayList<Integer> moves = new ArrayList<Integer>();
		for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {
				if(board[b][i][j]!=Player.X&&board[b][i][j]!=Player.O) {
					moves.add(Integer.valueOf(v[b][i][j]));
				}
			}
		}
		return moves;
	}
	public int[] mm(){
		Player[][][] tempbd=board;
		String[][][] tempv=v;
		Player tempt=currentTurn;
		int[] ret = miniMax(8,vs, Integer.MIN_VALUE, Integer.MAX_VALUE);
		board=tempbd;
		v=tempv;
		System.err.println("Utility of AI move is "+ret[0]);
		currentTurn=tempt;
		return ret;
	}
	private int[] miniMax(int level,Player p, int alpha, int beta){
		ArrayList<Integer> avail = getMoves(currBoard);
		ArrayList<Integer> availB = new ArrayList<Integer>();
		if(currBoard==0) {
			for(int i=1; i<10; i++) {
				if(getMoves(i)!=null) {
					availB.add(i);
				}
			}

		}
		else{
			availB.add(currBoard);
		}
		//printBoard();
		int util;
		int mmMove=0;
		int boardnum=currBoard;
		if(level==0||avail.isEmpty()) {
			util=eval(vs);
			over=false;
			return new int[] {util,currBoard, mmMove};
		}
		else{
			for(int b : availB) {
				for(int i : avail) {
					mark(p,b,i);
					if(p==vs) { //when it is the AI's turn, maximizing utility
						util = miniMax(level-1, user, alpha, beta)[0];
						if(util>alpha) {
							alpha=util;
							boardnum=b;
							mmMove=i;
						}
					}
					else{
						util = miniMax(level-1, vs, alpha, beta)[0];
						if(util<beta) {  //when it isnt the AI's turn, minimizing utility
							beta=util;
							boardnum=b;
							mmMove=i;
						}
					}
					undo(b,i);
					if(alpha>=beta) {
						break;
					}
				}
			}
			if(p==vs) {
				return new int[] {alpha,boardnum, mmMove};
			}
			else{
				return new int[] {beta,boardnum, mmMove};
			}
		}
	}
	public int eval(Player P){
		Player VS;
		int ret;
		if(P==Player.X)
			VS=Player.O;
		else
			VS=Player.X;
		ret=(evalu(P)-evalu(VS));
		//printBoard();
		return ret;
	}
	private int evalu(Player P){
		Player VS;
		int ret=0;
		if(P==Player.X)
			VS=Player.O;
		else
			VS=Player.X;
		for(int w=1; w<10; w++) {
			for( int row = 0; row < 3; row++ ) {
				if(board[w][row][0]!=VS
				   &&board[w][row][1]!=VS
				   &&board[w][row][2]!=VS) {
					int valtemp=-1;
					for(int i=0; i<3; i++) {
						if(board[w][row][i]==P) {
							valtemp++;
						}
					}
					if(valtemp!=-1) {
						ret+=(Math.pow(10, valtemp));
					}
				}
				if(board[w][0][row]!=VS
				   &&board[w][1][row]!=VS
				   &&board[w][2][row]!=VS) {
					int valtemp=-1;
					for(int i=0; i<3; i++) {
						if(board[w][i][row]==P) {
							valtemp++;
						}
					}
					if(valtemp!=-1) {
						ret+=(Math.pow(10, valtemp));
					}
				}
			}
			if(board[w][0][0]!=VS
			   &&board[w][1][1]!=VS
			   &&board[w][2][2]!=VS) {
				int valtemp=-1;
				for(int i=0; i<3; i++) {
					if(board[w][i][i]==P) {
						valtemp++;
					}
				}
				if(valtemp!=-1) {
					ret+=(Math.pow(10, valtemp));
				}
			}
			if(board[w][0][2]!=VS
			   &&board[w][1][1]!=VS
			   &&board[w][2][0]!=VS) {
				int valtemp=-1;
				for(int i=0; i<3; i++) {
					if(board[w][i][2-i]==P) {
						valtemp++;
					}
				}
				if(valtemp!=-1) {
					ret+=(Math.pow(10, valtemp));
				}
			}
		}

		if(checkWin()==P) {
			over=false;
			return 10000;
		}
		return ret;
	}
	public Player checkWin(){
		for(int w=1; w<10; w++) {
			for( int row = 0; row < 3; row++ ) {
				if(board[w][row][0]!=Player.Q) {
					if(board[w][row][0]==board[w][row][1]
					   &&board[w][row][1]==board[w][row][2]) {
						over=true;
						return board[w][row][0];
					}
				}
				if(board[w][0][row]!=Player.Q) {
					if(board[w][0][row]==board[w][1][row]
					   &&board[w][1][row]==board[w][2][row]) {
						over=true;
						return board[w][0][row];
					}
				}
			}
			if(board[w][0][0]!=Player.Q) {
				if(board[w][0][0]==board[w][1][1]
				   &&board[w][1][1]==board[w][2][2]) {
					over=true;
					return board[w][0][0];
				}
			}
			if(board[w][0][2]!=Player.Q) {
				if(board[w][0][2]==board[w][1][1]
				   &&board[w][1][1]==board[w][2][0]) {
					over=true;
					return board[w][0][2];
				}
			}
		}
		return null;
	}
	public void mark(Player p, int b, int j){
		currBoard=j;
		int i=0;
		for(; j>3; j-=3) {
			i++;
		}
		j--;
		if(board[b][i][j]==Player.Q) {
			switch (p) {
			case X:
				board[b][i][j] = Player.X;
				v[b][i][j]="X";
				if(getMoves(currBoard).isEmpty())
					currBoard=0;
				break;
			case O:
				board[b][i][j] = Player.O;
				v[b][i][j]="O";
				if(getMoves(currBoard).isEmpty())
					currBoard=0;
				break;
			case Q:
				break;
			default:
				break;
			}
		}
		else{
			System.err.println("Invalid input");
		}

	}
	public void undo(int b, int j){
		int temp = j;
		int i=0;
		for(; j>3; j-=3) {
			i++;
		}
		j--;
		board[b][i][j] = Player.Q;
		v[b][i][j]=String.valueOf(temp);
	}
	public void changeTurn(){
		switch (currentTurn) {
		case X:
			this.currentTurn = Player.O;
			break;
		case O:
			this.currentTurn = Player.X;
			break;
		case Q:
			break;
		default:
			break;
		}
	}

	public void printBoard(){
		System.err.println("  (1)  |  (2)  |  (3) ");
		System.err.println(" "+v[1][0][0]+" "+v[1][0][1]+" "+v[1][0][2]+" | "+v[2][0][0]+" "+v[2][0][1]+" "+v[2][0][2]+" | "+v[3][0][0]+" "+v[3][0][1]+" "+v[3][0][2]+"");
		System.err.println(" "+v[1][1][0]+" "+v[1][1][1]+" "+v[1][1][2]+" | "+v[2][1][0]+" "+v[2][1][1]+" "+v[2][1][2]+" | "+v[3][1][0]+" "+v[3][1][1]+" "+v[3][1][2]+"");
		System.err.println(" "+v[1][2][0]+" "+v[1][2][1]+" "+v[1][2][2]+" | "+v[2][2][0]+" "+v[2][2][1]+" "+v[2][2][2]+" | "+v[3][2][0]+" "+v[3][2][1]+" "+v[3][2][2]+"");
		System.err.println("-----------------------");
		System.err.println("  (4)  |  (5)  |  (6) ");
		System.err.println(" "+v[4][0][0]+" "+v[4][0][1]+" "+v[4][0][2]+" | "+v[5][0][0]+" "+v[5][0][1]+" "+v[5][0][2]+" | "+v[6][0][0]+" "+v[6][0][1]+" "+v[6][0][2]+"");
		System.err.println(" "+v[4][1][0]+" "+v[4][1][1]+" "+v[4][1][2]+" | "+v[5][1][0]+" "+v[5][1][1]+" "+v[5][1][2]+" | "+v[6][1][0]+" "+v[6][1][1]+" "+v[6][1][2]+"");
		System.err.println(" "+v[4][2][0]+" "+v[4][2][1]+" "+v[4][2][2]+" | "+v[5][2][0]+" "+v[5][2][1]+" "+v[5][2][2]+" | "+v[6][2][0]+" "+v[6][2][1]+" "+v[6][2][2]+"");
		System.err.println("-----------------------");
		System.err.println("  (7)  |  (8)  |  (9) ");
		System.err.println(" "+v[7][0][0]+" "+v[7][0][1]+" "+v[7][0][2]+" | "+v[8][0][0]+" "+v[8][0][1]+" "+v[8][0][2]+" | "+v[9][0][0]+" "+v[9][0][1]+" "+v[9][0][2]+"");
		System.err.println(" "+v[7][1][0]+" "+v[7][1][1]+" "+v[7][1][2]+" | "+v[8][1][0]+" "+v[8][1][1]+" "+v[8][1][2]+" | "+v[9][1][0]+" "+v[9][1][1]+" "+v[9][1][2]+"");
		System.err.println(" "+v[7][2][0]+" "+v[7][2][1]+" "+v[7][2][2]+" | "+v[8][2][0]+" "+v[8][2][1]+" "+v[8][2][2]+" | "+v[9][2][0]+" "+v[9][2][1]+" "+v[9][2][2]+"");
	}
}
