package wvs.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import wvs.WzManager;
import wvs.wz.property.WzCanvas;

public class LoginNotice extends SubScene {
	private AnchorPane root;
	
	private ImageView background;
	private ImageView text;
	
	private Button btnYes;
	private Button btnNo;
	
	public static final int NORMAL = 0;
	public static final int WARNING = 1;
	public static final int INPUT = 2;

	public LoginNotice() {
		super(new AnchorPane(), 0, 0);
		root = (AnchorPane) getRoot();
		root.setBackground(null);
		setVisible(false);
		btnYes = new WvsButton("Data/UI/Login.img/Notice/BtYes");
		btnYes.setOnAction(new EventHandler<>() {
			@Override
			public void handle(ActionEvent arg0) {
				close();
			}
			
		});
		btnYes.setLayoutX(150);
		btnYes.setLayoutY(145);
		root.getChildren().add(btnYes);
	}
	
	public void show(int type, int text) {
		WzCanvas backCanvas = (WzCanvas) WzManager.getInstance().getObject("Data/UI/Login.img/Notice/backgrnd/" + type);
		WzCanvas textCanvas = (WzCanvas) WzManager.getInstance().getObject("Data/UI/Login.img/Notice/text/" + text);
		
		background = new ImageView(backCanvas.getFxImage());
		setWidth(backCanvas.getWidth());
		setHeight(backCanvas.getHeight());
		root.setPrefSize(getWidth(), getHeight());
		setLayoutX(400 - getWidth() / 2);
		setLayoutY(300 - getHeight() / 2);
		root.getChildren().add(background);
		background.toBack();
		
		this.text = new ImageView(textCanvas.getFxImage());
		this.text.setLayoutX(120);
		this.text.setLayoutY(21);
		root.getChildren().add(this.text);
		setVisible(true);
	}
	
	public void close() {
		setVisible(false);
		root.getChildren().remove(background);
		root.getChildren().remove(text);
		background = null;
		text = null;
	}

}
