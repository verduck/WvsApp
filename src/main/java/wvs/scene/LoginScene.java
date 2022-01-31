package wvs.scene;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import wvs.WvsApp;
import wvs.WzManager;
import wvs.map.Player;
import wvs.map.WvsMap;
import wvs.net.PacketReader;
import wvs.net.PacketWriter;
import wvs.ui.LoginNotice;
import wvs.ui.WvsButton;
import wvs.wz.property.WzCanvas;

public class LoginScene extends WvsScene {
	private WvsMap map;
	private Player player;

	private ImageView frame;
	private LoginNotice notice;

	private ArrayList<AnchorPane> uiPanes = new ArrayList<>();
	private int currentStep;

	private Player selectPlayer;
	private boolean isNameCheck = false;
	int id;
	String name;
	int mapId;
	int face;

	public LoginScene(WvsApp app, double width, double height) {
		super(app, width, height);
	}

	@Override
	public void initialize() {
		player = new Player();
		player.setX(-45);
		player.setY(1850 - (600 * 0));
		map = new WvsMap(app, player);
		root.getChildren().add(map);
		frame = new ImageView(
				((WzCanvas) WzManager.getInstance().getObject("Data/UI/Login.img/Common/frame")).getFxImage());
		root.getChildren().add(frame);
		uiPanes.add(new LoginPane());
		uiPanes.add(new WorldSelectPane());
		uiPanes.add(new CharSelectPane());
		uiPanes.add(new NewCharPane());
		for (AnchorPane a : uiPanes) {
			root.getChildren().add(a);
		}
		notice = new LoginNotice();
		root.getChildren().add(notice);
		currentStep = 0;
		changeStep(currentStep);
		map.getCamera().setTranslateX(-45);
		map.getCamera().setTranslateY(1850 - (600 * 0));
		map.changeLoginMap();
		uiPanes.get(currentStep).setVisible(true);
		isNameCheck = false;
	}

	@Override
	public void finalization() {
		map.clear();
		map = null;

		for (AnchorPane a : uiPanes) {
			root.getChildren().remove(a);
			a = null;
		}
		uiPanes.clear();

		player = null;
	}

	public void changeStep(int step) {
		uiPanes.get(currentStep).setVisible(false);
		player.setY(1850 - (600 * step));
		currentStep = step;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (Math.round(map.getCamera().getTranslateY()) != player.getY())
					;
				uiPanes.get(currentStep).setVisible(true);
			}
		});
		t.start();
	}
	
	@Override
	public void messageReciver(PacketReader r) {
		short packetId = r.readShort();
		System.out.println("받은 패킷 번호 : " + packetId);
		switch (packetId) {
		case 0: {
			int text = r.readInt();
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					notice.show(2, text);
				}
			});
		}
			break;
		case 1: { // 로그인 성공 시
			id = r.readInt();
			name = r.readString();
			mapId = r.readInt();
			face = r.readInt();
			System.out.println(id + "," + name + "," + map + "," + face);
			selectPlayer = new Player(id, name, mapId, face);
			app.reconnect("localhost", 9090);
			app.sendPacket(selectedPlayerInfo(selectPlayer).getPacket());
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					app.changeScene(1);
				}
			});
			break;
		}
		case 2: {
			byte result = r.readByte();
			if (result == 0) {
				notice.show(2, 8);
				isNameCheck = false;
			} else if (result == 1) {
				notice.show(2, 6);
				isNameCheck = true;
			}
			break;
		}
		default:
			break;
		}
	}
	
	public PacketWriter selectedPlayerInfo(Player player) {
		PacketWriter packet = new PacketWriter();
		packet.writeShort(5);
		packet.writeInt(player.getId());
		packet.writeString(player.getName());
		packet.writeInt(player.getMap());
		packet.writeInt(player.getFace());
		return packet;
	}

	class LoginPane extends AnchorPane {
		private TextField tfEmail;
		private PasswordField pfPassword;

		private WvsButton btnLogin;
		private WvsButton btnNew;
		private WvsButton btnHomePage;
		private WvsButton btnQuit;

		public LoginPane() {
			super();
			setPrefSize(800, 600);

			tfEmail = new TextField();
			tfEmail.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white");
			tfEmail.setPrefWidth(148);
			tfEmail.setLayoutX(440);
			tfEmail.setLayoutY(240);
			tfEmail.requestFocus();
			getChildren().add(tfEmail);

			pfPassword = new PasswordField();
			pfPassword.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white");
			pfPassword.setPrefWidth(148);
			pfPassword.setLayoutX(440);
			pfPassword.setLayoutY(268);
			getChildren().add(pfPassword);

			btnLogin = new WvsButton("Data/UI/Login.img/Title/BtLogin");
			btnLogin.setLayoutX(580);
			btnLogin.setLayoutY(235);
			btnLogin.setOnAction(new EventHandler<>() {
				@Override
				public void handle(ActionEvent e) {
					PacketWriter packet = new PacketWriter(128);
					packet.writeShort(0x01);
					packet.writeString(tfEmail.getText());
					packet.writeString(pfPassword.getText());
					app.sendPacket(packet.getPacket());
				}

			});
			getChildren().add(btnLogin);

			btnNew = new WvsButton("Data/UI/Login.img/Title/BtNew");
			btnNew.setLayoutX(385);
			btnNew.setLayoutY(351);
			getChildren().add(btnNew);

			btnHomePage = new WvsButton("Data/UI/Login.img/Title/BtHomePage");
			btnHomePage.setLayoutX(485);
			btnHomePage.setLayoutY(351);
			getChildren().add(btnHomePage);

			btnQuit = new WvsButton("Data/UI/Login.img/Title/BtQuit");
			btnQuit.setLayoutX(585);
			btnQuit.setLayoutY(350);
			btnQuit.setOnAction(new EventHandler<>() {
				@Override
				public void handle(ActionEvent e) {
					System.exit(0);
				}

			});
			getChildren().add(btnQuit);

			setVisible(false);
		}
	}

	class WorldSelectPane extends AnchorPane {
		private ImageView shadow;
		private ImageView step;

		private WvsButton btnToStart;

		public WorldSelectPane() {
			super();
			setPrefSize(800, 600);

			shadow = new ImageView(
					((WzCanvas) WzManager.getInstance().getObject("Data/UI/Login.img/Common/shadow/0")).getFxImage());
			shadow.setLayoutY(30);
			getChildren().add(shadow);

			step = new ImageView(
					((WzCanvas) WzManager.getInstance().getObject("Data/UI/Login.img/Common/step/1")).getFxImage());
			step.setLayoutY(30);
			getChildren().add(step);

			btnToStart = new WvsButton("Data/UI/Login.img/Common/BtStart");
			btnToStart.setLayoutX(-10);
			btnToStart.setLayoutY(420);
			btnToStart.setOnAction(new EventHandler<>() {
				@Override
				public void handle(ActionEvent arg0) {
					changeStep(0);
				}

			});
			getChildren().add(btnToStart);

			setVisible(false);
		}
	}

	class CharSelectPane extends AnchorPane {
		private ImageView shadow;
		private ImageView step;

		private WvsButton btnSelect;
		private WvsButton btnNew;
		private WvsButton btnDelete;

		private WvsButton btnToStart;

		public CharSelectPane() {
			super();
			setPrefSize(800, 600);

			shadow = new ImageView(
					((WzCanvas) WzManager.getInstance().getObject("Data/UI/Login.img/Common/shadow/0")).getFxImage());
			shadow.setLayoutY(30);
			getChildren().add(shadow);

			step = new ImageView(
					((WzCanvas) WzManager.getInstance().getObject("Data/UI/Login.img/Common/step/2")).getFxImage());
			step.setLayoutY(30);
			getChildren().add(step);

			btnSelect = new WvsButton("Data/UI/Login.img/CharSelect/BtSelect");
			btnSelect.setLayoutX(567);
			btnSelect.setLayoutY(149);
			btnSelect.setOnAction(new EventHandler<>() {
				@Override
				public void handle(ActionEvent arg0) {
					app.reconnect("localhost", 9090);
					app.sendPacket(selectedPlayerInfo().getPacket());
					app.changeScene(1);
				}

			});
			getChildren().add(btnSelect);

			btnNew = new WvsButton("Data/UI/Login.img/CharSelect/BtNew");
			btnNew.setLayoutX(567);
			btnNew.setLayoutY(185);
			btnNew.setOnAction(new EventHandler<>() {
				@Override
				public void handle(ActionEvent arg0) {
					changeStep(3);
				}

			});
			getChildren().add(btnNew);

			btnDelete = new WvsButton("Data/UI/Login.img/CharSelect/BtDelete");
			btnDelete.setLayoutX(567);
			btnDelete.setLayoutY(237);
			getChildren().add(btnDelete);

			btnToStart = new WvsButton("Data/UI/Login.img/Common/BtStart");
			btnToStart.setLayoutX(-10);
			btnToStart.setLayoutY(420);
			btnToStart.setOnAction(new EventHandler<>() {
				@Override
				public void handle(ActionEvent arg0) {
					changeStep(0);
				}

			});
			getChildren().add(btnToStart);

			setVisible(false);
		}
		
		public PacketWriter selectedPlayerInfo() {
			PacketWriter packet = new PacketWriter();
			packet.writeShort(5);
			packet.writeInt(id);
			packet.writeString(name);
			packet.writeInt(mapId);
			packet.writeInt(face);
			return packet;
		}
	}

	class NewCharPane extends AnchorPane {
		private ImageView shadow;
		private ImageView step;

		private TextField tfName;
		private WvsButton btnCheck;
		private WvsButton btnYes;
		private WvsButton btnNo;

		private WvsButton btnToStart;

		public NewCharPane() {
			super();
			setPrefSize(800, 600);

			shadow = new ImageView(
					((WzCanvas) WzManager.getInstance().getObject("Data/UI/Login.img/Common/shadow/0")).getFxImage());
			shadow.setLayoutY(30);
			getChildren().add(shadow);

			step = new ImageView(
					((WzCanvas) WzManager.getInstance().getObject("Data/UI/Login.img/Common/step/3")).getFxImage());
			step.setLayoutY(30);
			getChildren().add(step);

			tfName = new TextField();
			tfName.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white");
			tfName.setPrefWidth(148);
			tfName.setLayoutX(515);
			tfName.setLayoutY(196);
			tfName.setText("");
			getChildren().add(tfName);

			btnCheck = new WvsButton("Data/UI/Login.img/NewChar/BtCheck");
			btnCheck.setLayoutX(605);
			btnCheck.setLayoutY(162);
			btnCheck.setOnAction(new EventHandler<>() {
				@Override
				public void handle(ActionEvent e) {
					if (!tfName.equals("")) {
						PacketWriter write = new PacketWriter();
						write.writeShort(2);
						write.writeString(tfName.getText());
						app.sendPacket(write.getPacket());
					} else {
						
					}
				}
			});
			getChildren().add(btnCheck);

			btnYes = new WvsButton("Data/UI/Login.img/NewChar/BtYes");
			btnYes.setLayoutX(504);
			btnYes.setLayoutY(382);
			btnYes.setOnAction(new EventHandler<>() {
				@Override
				public void handle(ActionEvent e) {
					if (!isNameCheck) {
						notice.show(2, 12);
					} else {
						PacketWriter write = new PacketWriter();
						write.writeShort(3);
						write.writeString(tfName.getText());
						write.writeInt(0);
						app.sendPacket(write.getPacket());
					}
				}
			});
			getChildren().add(btnYes);

			btnNo = new WvsButton("Data/UI/Login.img/NewChar/BtNo");
			btnNo.setLayoutX(580);
			btnNo.setLayoutY(382);
			btnNo.setOnAction(new EventHandler<>() {
				@Override
				public void handle(ActionEvent e) {
					isNameCheck = false;
					changeStep(2);
				}
			});
			getChildren().add(btnNo);

			btnToStart = new WvsButton("Data/UI/Login.img/Common/BtStart");
			btnToStart.setLayoutX(-10);
			btnToStart.setLayoutY(420);
			btnToStart.setOnAction(new EventHandler<>() {
				@Override
				public void handle(ActionEvent e) {
					changeStep(0);
				}

			});
			getChildren().add(btnToStart);

			setVisible(false);
		}
	}

	@Override
	public void update(double delta) {
		map.update(delta);
	}

	@Override
	public void render() {
		map.render();
	}
}
