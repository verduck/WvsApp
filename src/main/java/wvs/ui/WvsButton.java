package wvs.ui;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import wvs.WzManager;
import wvs.wz.WzObject;
import wvs.wz.property.WzCanvas;

public class WvsButton extends Button {
	private ArrayList<ImageView> btnImages = new ArrayList<>();
	
	public WvsButton(String wzPath) {
		super();
		WzObject o = WzManager.getInstance().getObject(wzPath);
		btnImages.add(new ImageView(((WzCanvas) o.getObject("disabled/0")).getFxImage()));
		btnImages.add(new ImageView(((WzCanvas) o.getObject("normal/0")).getFxImage()));
		btnImages.add(new ImageView(((WzCanvas) o.getObject("mouseOver/0")).getFxImage()));
		btnImages.add(new ImageView(((WzCanvas) o.getObject("pressed/0")).getFxImage()));
		setStyle("-fx-background-color: transparent");
		setGraphic(btnImages.get(1));
		initializeListener();
	}
	
	private void initializeListener() {
		setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.getButton().equals(MouseButton.PRIMARY)) {
					setGraphic(btnImages.get(3));
				}
			}
			
		});
		super.isHover();
		setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.getButton().equals(MouseButton.PRIMARY)) {
					if (isHover()) {
						setGraphic(btnImages.get(2));
					} else {
						setGraphic(btnImages.get(1));
					}
				}
			}
			
		});
		
		setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				setGraphic(btnImages.get(2));
			}
			
		});
		
		setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				setGraphic(btnImages.get(1));
			}
			
		});
	}
}
