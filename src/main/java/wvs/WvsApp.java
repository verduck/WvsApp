package wvs;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import wvs.net.PacketReader;
import wvs.scene.GameScene;
import wvs.scene.LoginScene;
import wvs.scene.WvsScene;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;

public class WvsApp extends Application {
	private Stage stage;
	private ArrayList<WvsScene> scenes = new ArrayList<>();
	private int currentScene = 0;

	private Socket socket;
	private MsgRecvThread thread;
	
	private AnimationTimer animationTimer;

	@Override
	public void start(Stage primaryStage) {
		try {
			stage = primaryStage;
			stage.setOnCloseRequest(new EventHandler<>() {
				@Override
				public void handle(WindowEvent arg0) {
					System.exit(0);
				}

			});
			scenes.add(new LoginScene(this, 800, 600));
			scenes.add(new GameScene(this, 800, 600));
			scenes.get(currentScene).initialize();
			stage.setScene(scenes.get(currentScene));
			animationTimer = new AnimationTimer() {
				private long start = System.nanoTime();
				
				@Override
				public void handle(long now) {
					double delta = (now - start) / 1000000000.0 * 6;	
					scenes.get(currentScene).update(delta);
					scenes.get(currentScene).render();
					if (delta >= 1)  {
						start = now;
					}
				}
			};
			animationTimer.start();
			socket = new Socket();
			socket.connect(new InetSocketAddress("localhost", 8888));
			thread = new MsgRecvThread();
			thread.start();
			primaryStage.show();
		} catch (ConnectException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Wvs");
			alert.setHeaderText(null);
			alert.setContentText("로그인 서버에 연결 할 수 없습니다.");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				System.exit(0);
			}
		} catch (IOException e) {
			
		}
	}

	public void changeScene(int sceneNum) {
		animationTimer.stop();
		scenes.get(currentScene).finalization();
		currentScene = sceneNum;
		scenes.get(currentScene).initialize();
		stage.setScene(scenes.get(currentScene));
		animationTimer.start();
	}
	
	public void pause() {
		animationTimer.stop();
	}
	
	public void resume() {
		animationTimer.start();
	}

	public static void main(String args[]) {
		launch(args);
	}

	public void sendPacket(byte[] data) {
		OutputStream out;
		try {
			out = socket.getOutputStream();
			out.write(data);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reconnect(String host, int port) {
		try {
			socket.close();
			socket = new Socket();
			socket.connect(new InetSocketAddress(host, port));
			thread = new MsgRecvThread();
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeSocket() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class MsgRecvThread extends Thread {
		private InputStream in;
		private byte[] data;

		public MsgRecvThread() throws IOException {
			super();
			in = socket.getInputStream();
		}

		@Override
		public void run() {
			try {
				while (true) {
					if (socket.isClosed()) {
						break;
					}
					data = new byte[1024];
					int count = in.read(data);
					if (count <= 0) {
						break;
					}
					PacketReader reader = new PacketReader(data);
					scenes.get(currentScene).messageReciver(reader);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
