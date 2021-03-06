public class Game implements Runnable {
	private Thread thread;
	private Window window;
	private ActionListener al;
	private AbstractGame game;
	
	private boolean running = false;
	private final double UPDATE_CAP = 1.0/60.0;
	
	public static final int width = 1620;
	public static final int height = 1000;
	public static final float scale = 1f;
	public static final String title = "Coin Collector";
	
	private static int fps;
	
	private static int threeSecs = 3;
	private static boolean isThreeSecs = false;
	static int oneSec;

	public Game(AbstractGame game) {
		this.game = game;
	}
	
	public Window getWindow() {
		return window;
	}
	
	public synchronized void start() {
		if(running) return;
		running = true;
		
		window = new Window(width, height, scale, title, this);
		al = new ActionListener(this);
		
		thread = new Thread(this);
		thread.run();
	}
	
	public ActionListener getAl() {
		return al;
	}

	public synchronized void stop() {
		if(!running) 
			return;
		running = false;
		
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		boolean render  = false;
		double firstTime = 0;
		double lastTime = System.nanoTime() / 1000000000.0;
		double passedTime = 0;
		double unprocessedTime = 0;
		
		double frameTime = 0;
		int frames = 0;
		fps = 0;
		
		while(running) {
			render = false;
			
			firstTime = System.nanoTime() / 1000000000.0;
			passedTime = firstTime - lastTime;
			lastTime = firstTime;
			
			unprocessedTime += passedTime;
			frameTime += passedTime;
			
			while(unprocessedTime >= UPDATE_CAP) {
				unprocessedTime -= UPDATE_CAP;
				render = true;
				
				game.update(this, (float)UPDATE_CAP);
				al.update();

				if(frameTime >= 1.0) {
					frameTime = 0;
					fps = frames;
					frames = 0;
					
					if(oneSec == 0) {
						oneSec++;
						window.ifOneSecGone();
					} else {
						oneSec = 0;
					}
					
					if(!isThreeSecs) {
						threeSecs--;
						if(threeSecs < 0) {
							isThreeSecs = true;
						}
					}
				}
			}
			
			if(render) {
				window.update();
				frames++;
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		stop();
	}

	public static int getThreeSecs() {
		return threeSecs;
	}

	public static boolean isThreeSecs() {
		return isThreeSecs;
	}

	public static int getFps() {
		return fps;
	}
}