package wvs.scene;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import wvs.WvsApp;
import wvs.net.PacketReader;

public abstract class WvsScene extends Scene {
	protected WvsApp app;
	protected AnchorPane root;
	
	public WvsScene(WvsApp app, double width, double height) {
		super(new AnchorPane(), width, height);
		this.app = app;
		root = (AnchorPane) getRoot();
		root.setPrefSize(width, height);
	}

	public abstract void initialize();
	
	public abstract void finalization();
	
	public abstract void update(double delta);
	public abstract void render();

	public abstract void messageReciver(PacketReader r);
}
