import java.awt.*;
import java.lang.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.*;
import java.awt.Point;

public class PacMan {
  JFrame frame;
	char[][] map = new char[36][28];
	PacManPanel panel;
	MrPacMan bob = new MrPacMan();
	Ghost blinky = new Blinky();

    public static void main (String[] args) {
        PacMan p = new PacMan();
        p.go();
    }

    public void go() {
		//set up the map
		InputStream fis;
		BufferedReader br;
		String line;
		int i = 0;

		try {
			fis = new FileInputStream("src/map.txt");
			br = new BufferedReader (new InputStreamReader(fis, Charset.forName("UTF-8")));
			while ((line = br.readLine()) != null && i < 36) {
				for (int j = 0; j < 28; j++) {
					map[i][j] = line.charAt(j);
				}
				i++;
			}
		} catch (IOException e) {
			System.err.println("Caught IOException: " + e.getMessage());
		}

		//setup
		frame = new JFrame ();
		panel = new PacManPanel();
		panel.setLayout(null);
		panel.setFocusable(true);
		panel.addKeyListener(new MrPacManListener());
		frame.getContentPane().add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(250, 350);
        frame.setVisible(true);

        while (bob.lives > 0) {
       		try {
       			Thread.sleep(100);
       		} catch (InterruptedException e) {
       			e.printStackTrace();
       		} 
       		blinky.determineDirection();
       		blinky.move();
       		panel.repaint();
        }
    }

    class MrPacMan {
    	int x, y, lives, points;
		Image img;

		public MrPacMan() {
			x = 26;
			y = 14;
			lives = 3;
			points = 0;
			img = new ImageIcon("src/Mr. Pac-Man.png").getImage();
		}
    }

    abstract class Ghost {
    	int x, y;

    	public Ghost() {
    		x = 14;
    		y = 14;
    	}

    	abstract void determineDirection();
    	abstract void move();
    }

    class Blinky extends Ghost {
    	char dir;

    	void determineDirection() {
    		int horizontalDistance = this.x - bob.x;
    		int verticalDistance = this.y - bob.y;

    		if (Math.abs(verticalDistance) <= Math.abs(horizontalDistance)) {
    			if (verticalDistance >= 0)  dir = 'u';
    			else if (verticalDistance <= 0) dir = 'd';
    		} else {
    			if (horizontalDistance >= 0) dir = 'r';
    			else dir = 'l';
    		}
    	}

    	void move() {
    		map[this.x][this.y] = 'm';
    		if (dir == 'u' && map[this.x - 1][this.y] != 'n') {
    			this.x = this.x - 1;
    		}
    		else if (dir == 'd' && map[this.x + 1][this.y] != 'n') {
				this.x = this.x + 1;
			}
			else if (dir == 'l' && map[this.x][this.y - 1] != 'n') {
				this.y = this.y - 1;
			} 
			else if (dir == 'r' && map[this.x][this.y + 1] != 'n') {
				this.y = this.y + 1;
			}
			else if (map[this.x - 1][this.y] != 'n') {
    			this.x = this.x - 1;	
			}
			else if (map[this.x + 1][this.y] != 'n') {
        this.x = this.x + 1;
			}
			else if (map[this.x][this.y - 1] != 'n') this.y = this.y - 1;
			else if (map[this.x][this.y + 1] != 'n') this.y = this.y + 1;

      if (this.y <= 0) this.y = 27;
      else if (this.y >= 27) this.y = 0;

			if (this.x == bob.x && this.y == bob.y) {
        bob.lives--;
				map[bob.x][bob.y] = 'm';
				bob.x = 26;	
				bob.y = 14;
			}
    }
  } 

  class MrPacManListener implements KeyListener {

   	public void keyTyped(KeyEvent e) {

   	}

   	public void keyPressed(KeyEvent e) {
   		int keyCode = e.getKeyCode();
   		if (keyCode == KeyEvent.VK_UP && map[bob.x - 1][bob.y] != 'n') {
   			map[bob.x][bob.y] = 'm';
   			bob.x = bob.x - 1;
   		}
   		else if (keyCode == KeyEvent.VK_DOWN && map[bob.x + 1][bob.y] != 'n') {
   			map[bob.x][bob.y] = 'm';
   			bob.x = bob.x + 1;
   		}
   		else if (keyCode == KeyEvent.VK_LEFT && map[bob.x][bob.y - 1] != 'n') {
   			map[bob.x][bob.y] = 'm';
   			bob.y = bob.y - 1;
   			if (bob.y <= 0) bob.y = 27;
   		}
   		else if (keyCode == KeyEvent.VK_RIGHT && map[bob.x][bob.y + 1] != 'n') {
   			map[bob.x][bob.y] = 'm';
   			bob.y = bob.y + 1;
   			if (bob.y >= 27) bob.y = 0;
   		}

   		if (map[bob.x][bob.y] == 'd') bob.points += 10;
   		else if (map[bob.x][bob.y] == 'e') bob.points += 50;
   	}

   	public void keyReleased(KeyEvent e) {

   	}
  }

    class PacManPanel extends JPanel {
		public void paintComponent(Graphics g) {
			int index = 0;
			map[bob.x][bob.y] = 'p';
			map[blinky.x][blinky.y] = 'b';
			Image dot = new ImageIcon("src/dot.png").getImage();
			Image space = new ImageIcon("src/space.png").getImage();
			Image energizer = new ImageIcon("src/energizer.png").getImage();
			Image blinky = new ImageIcon("src/blinky.jpg").getImage();
			for (int i = 0; i < 36; i++) {
				for (int j = 0; j < 28; j++) {
					if (map[i][j] == 'd') g.drawImage(dot, j * 8, i * 8, this);
					else if (map[i][j] == 'm') g.drawImage(space, j * 8, i * 8, this);
					else if (map[i][j] == 'e') g.drawImage(energizer, j * 8, i * 8, this);
					else if (map[i][j] == 'p') g.drawImage(bob.img, j * 8, i * 8, this);
					else if (map[i][j] == 'b') g.drawImage(blinky, j * 8, i * 8, this);
					else {
						Image tile = new ImageIcon("src/tiles/tile" + index + ".png").getImage();
						g.drawImage(tile, j * 8, i * 8, this);
					}
					index++;
				}
			}
		}
	}
}


