package wvs.scene;

import java.util.HashMap;
import java.util.LinkedList;

import wvs.WvsApp;
import wvs.map.Player;
import wvs.map.Portal;
import wvs.map.WvsMap;
import wvs.net.PacketReader;
import wvs.net.PacketWriter;

public class GameScene extends WvsScene {
	private WvsMap map;	
	private Player player;
	
	private HashMap<Integer, Player> otherPlayers;
	private LinkedList<Portal> portals;
	
	public GameScene(WvsApp app, double width, double height) {
		super(app, width, height);
		player = new Player();
		otherPlayers = new HashMap<>();
		portals = new LinkedList<>();
		map = new WvsMap(app, player);
		root.getChildren().add(map);
		map.requestFocus();
	}

	@Override
	public void initialize() {
	}
	
	@Override
	public void finalization() {
		
	}
	

	@Override
	public void messageReciver(PacketReader r) {
		short packetId = r.readShort();
		//System.out.println("-----------"+packetId+"-----------");
		switch (packetId) {
		case 5: {
			otherPlayers.clear();
			portals.clear();
			int id = r.readInt();
			String name = r.readString();
			int mapId = r.readInt();
			int face = r.readInt();
			int playerX = r.readInt();
			int playerY = r.readInt();
			int num = r.readInt();
			//System.out.println("||"+id+"||"+name+"||"+mapId+"||"+face+"||"+playerX+"||"+playerY+"||"+num+"||");
			player.set(id, name, mapId, face);
			for (int i = 0; i < num; i++) {
				int type = r.readInt();
				int x = r.readInt();
				int y = r.readInt();
				portals.add(new Portal(type, x, y));
			}
			map.setPlayerX(playerX);
			map.setPlayerY(playerY);
			map.changeMap(player.getMap());
			map.setPortals(portals);
		}
		break;
		case 6: {
			int id = r.readInt();
			int face = r.readInt();
			int x = r.readInt();
			int y = r.readInt();
			otherPlayers.put(id, new Player(face, x, y));
			map.setPlayers(new LinkedList<Player>(otherPlayers.values()));
		}
		break;
		case 7: {
			int id = r.readInt();
			otherPlayers.remove(id);
			map.setPlayers(new LinkedList<Player>(otherPlayers.values()));
		}
		break;
		case 10: {
			int id = r.readInt();
			int face = r.readInt();
			int x = r.readInt();
			int y = r.readInt();
			if (id == player.getId()) {
				player.setFace(face);
				player.setX(x);
				player.setY(y);
			} else {
				Player p = otherPlayers.get(id);
				if (p != null) {
					otherPlayers.get(id).setFace(face);
					otherPlayers.get(id).setX(x);
					otherPlayers.get(id).setY(y);
				} else {
					otherPlayers.put(id, new Player(face, x, y));
					map.setPlayers(new LinkedList<Player>(otherPlayers.values()));
				}
			}
		}
		break;
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
