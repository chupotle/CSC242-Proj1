import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
public class Board {
	Player currentTurn;
	Player user;
	Player vs;
	Player[][] board;
	String[][] v;
	boolean over;
	public Board(){
		Scanner cont;
		String Cont="";
		for(;;){
			Game();
		}
	}
	public void Game(){
		over=false;
		currentTurn=Player.X;
		board = new Player[3][3];
		v = new String[3][3];
		System.err.println("Would you like to be \"X\" or \"O\"?");
		Scanner input = new Scanner(System.in);
		String usr = "";
		do{
			usr = input.nextLine().toUpperCase();
			if(!usr.equals("X")&&!usr.equals("O"))
				System.err.println("Invalid input");
		}while(!usr.equals("X")&&!usr.equals("O"));
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
		for(int i=0; i<3; i++){
			for(int j=0; j<3; j++){
				board[i][j]=Player.Q;
				v[i][j]=String.valueOf((i*3)+(j+1));
			}
		}
		printBoard();
		System.err.println("X starts");
		while(!over&&getMoves().size()!=0){
			if(user==currentTurn){
				int i=input.nextInt();
				ArrayList<Integer> used = getMoves();
				if(used.indexOf(i)!=-1){
					mark(user,i);
				}
				else{
					System.err.println("Invalid input");
					changeTurn();
				}
				
			}
			else{
				mark(vs, mm());
			}
			changeTurn();
			System.err.println("\n\n\n");
			try {
			    Thread.sleep(500);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}

			printBoard();
			checkWin();
		}
		if(checkWin()!=null){
			System.err.println(checkWin().toString()+" won");
		}
		else{
			System.err.println("Draw");
		}
		System.err.println("\n\n\n");
	}
	public ArrayList<Integer> getMoves(){
		ArrayList<Integer> moves = new ArrayList<Integer>();
		for(int i=0; i<3; i++){
			for(int j=0; j<3; j++){
				if(board[i][j]!=Player.X&&board[i][j]!=Player.O){
					moves.add(Integer.valueOf(v[i][j]));
				}
			}
		}
		return moves;
	}
	public int mm(){
		int[] ret = miniMax(6,vs, Integer.MIN_VALUE, Integer.MAX_VALUE);
		System.err.println("Util of move is "+ret[0]);
		return ret[1];
	}
	private int[] miniMax(int level,Player p, int alpha, int beta){
		ArrayList<Integer> avail = getMoves();
		//printBoard();
		int util;
		int mmMove=0;
		if(level==0||avail.isEmpty()){
			util=eval(vs);
			over=false;
			return new int[] {util, mmMove};
		}
		else{
			for(int i : avail){
				mark(p,i);
				if(p==vs){ //when it is the AI's turn, maximizing utility
					util = miniMax(level-1, user, alpha, beta)[0];
					if(util>alpha){
						alpha=util;
						mmMove=i;
					}
				}
				else{
					util = miniMax(level-1, vs, alpha, beta)[0];
					if(util<beta){  //when it isnt the AI's turn, minimizing utility
						beta=util;
						mmMove=i;
					}
				}
				undo(i);
				if(alpha>=beta){
					break;
				}
			}

			if(p==vs){
				return new int[] {alpha, mmMove};
			}
			else{
				return new int[] {beta, mmMove};
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
		for( int row = 0; row < 3; row++ ){
			if(board[row][0]!=VS
					&&board[row][1]!=VS
					&&board[row][2]!=VS){
				int valtemp=-1;
				for(int i=0; i<3;i++){
					if(board[row][i]==P){
						valtemp++;
					}
				}
				if(valtemp!=-1){
					ret+=(Math.pow(10, valtemp));
				}
			}
			if(board[0][row]!=VS
					&&board[1][row]!=VS
					&&board[2][row]!=VS){
				int valtemp=-1;
				for(int i=0; i<3;i++){
					if(board[i][row]==P){
						valtemp++;
					}
				}
				if(valtemp!=-1){
					ret+=(Math.pow(10, valtemp));
				}
			}
        }
		if(board[0][0]!=VS
				&&board[1][1]!=VS
				&&board[2][2]!=VS){
			int valtemp=-1;
			for(int i=0; i<3;i++){
				if(board[i][i]==P){
					valtemp++;
				}
			}
			if(valtemp!=-1){
				ret+=(Math.pow(10, valtemp));
			}
		}
		if(board[0][2]!=VS
				&&board[1][1]!=VS
				&&board[2][0]!=VS){
			int valtemp=-1;
			for(int i=0; i<3;i++){
				if(board[i][2-i]==P){
					valtemp++;
				}
			}
			if(valtemp!=-1){
				ret+=(Math.pow(10, valtemp));
			}
		}
		if(checkWin()==P){
			over=false;
			return 10000;
		}
		return ret;
	}
	public Player checkWin(){
		for( int row = 0; row < 3; row++ ){
			if(board[row][0]!=Player.Q){
				if(board[row][0]==board[row][1]
						&&board[row][1]==board[row][2]){
					over=true;
					return board[row][0];
				}
			}
			if(board[0][row]!=Player.Q){
				if(board[0][row]==board[1][row]
						&&board[1][row]==board[2][row]){
					over=true;
					return board[0][row];
				}
			}
        }
		if(board[0][0]!=Player.Q){
			if(board[0][0]==board[1][1]
					&&board[1][1]==board[2][2]){
				over=true;
				return board[0][0];
			}
		}
		if(board[0][2]!=Player.Q){
			if(board[0][2]==board[1][1]
					&&board[1][1]==board[2][0]){
				over=true;
				return board[0][2];
			}
		}
		return null;
	}
	public void mark(Player p, int j){
		int i=0;
		for(; j>3; j-=3){
			i++;
		}
		j--;
		if(board[i][j]==Player.Q){
			switch (p) {
			case X:
				board[i][j] = Player.X;
				v[i][j]="X";

				break;
			case O:
				board[i][j] = Player.O;
				v[i][j]="O";
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
	public void undo(int j){
		int temp = j;
		int i=0;
		for(; j>3; j-=3){
			i++;
		}
		j--;
		board[i][j] = Player.Q;
		v[i][j]=String.valueOf(temp);
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

		System.err.println(" "+v[0][0]+" | "+v[0][1]+" | "+v[0][2]+" ");
		System.err.println("---|---|---");
		System.err.println(" "+v[1][0]+" | "+v[1][1]+" | "+v[1][2]+" ");
		System.err.println("---|---|---");
		System.err.println(" "+v[2][0]+" | "+v[2][1]+" | "+v[2][2]+" ");
	}
}
