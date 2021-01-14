package wvs.wz.property;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Inflater;

import javax.imageio.ImageIO;

import javafx.scene.image.Image;
import wvs.wz.WzImg;
import wvs.wz.WzInputStream;
import wvs.wz.WzObject;

public class WzCanvas extends WzObject {

	private WzProperty prop;
	private int width;
	private int height;
	private int format;
	private byte magLevel;
	private byte[] data;
	private BufferedImage image;
	private Image fxImage;
	
	private static final int[] ZAHLEN = new int[]{2, 1, 0, 3};
	
	
	public WzCanvas(String name, WzObject parent, WzImg parentImg) {
		super(name, parent, parentImg);
		// TODO Auto-generated constructor stub
	}

	public void parse(WzInputStream reader, long offset) {
		try {
			reader.skip(1);
			
			byte type = reader.readByte();
			if (type == 1) {
				prop = new WzProperty(name, parent, parentImg);
				prop.parse(reader, offset);
			}
			
			width = reader.readCompressedInt();
			height = reader.readCompressedInt();
			format = reader.readCompressedInt();
			magLevel = reader.readByte();
			if (reader.readInt() != 0) {
				return;
			}
			int len = reader.readInt() - 1;
			reader.skip(1);
			data = new byte[len];
			reader.read(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createImage() {
		int sizeUncompressed = 0;
        int size8888 = 0;
        int maxWriteBuf = 2;
        int maxHeight = 3;
        byte[] writeBuf = new byte[maxWriteBuf];
        switch (getFormat()) {
            case 1:
            case 513:
                sizeUncompressed = getHeight() * getWidth() * 2;
                break;
            case 2:
                sizeUncompressed = getHeight() * getWidth() * 4;
                break;
            case 517:
                sizeUncompressed = getHeight() * getWidth() / 128;
                break;
        }
        size8888 = getHeight() * getWidth() * 8;
        if (size8888 > maxWriteBuf) {
            maxWriteBuf = size8888;
            writeBuf = new byte[maxWriteBuf];
        }
        if (getHeight() > maxHeight) {
            maxHeight = getHeight();
        }
        Inflater dec = new Inflater();
        dec.setInput(data, 0, data.length);
        int declen = 0;
        byte[] uc = new byte[sizeUncompressed];
        try {
            declen = dec.inflate(uc);
        } catch (Exception ex) {
            throw new RuntimeException();
        }
        dec.end();
        if (getFormat() == 1) {
            for (int i = 0; i < sizeUncompressed; i++) {
                byte low = (byte) (uc[i] & 0x0F);
                byte high = (byte) (uc[i] & 0xF0);
                writeBuf[(i << 1)] = (byte) (((low << 4) | low) & 0xFF);
                writeBuf[(i << 1) + 1] = (byte) (high | ((high >>> 4) & 0xF));
            }
        } else if (getFormat() == 2) { // abgr8888 형식
            writeBuf = uc;
        } else if (getFormat() == 513) { // bgr565 형식
        	for (int i = 0; i < declen; i += 2) {
                int r = (uc[i + 1]) & 0xF8;
                int g = ((uc[i + 1] & 0x07) << 5) | ((uc[i] & 0xE0) >> 3);
                int b = ((uc[i] & 0x1F) << 3);
                writeBuf[(i << 1)] = (byte) (b | (b >> 5));
                writeBuf[(i << 1) + 1] = (byte) (g | (g >> 6));
                writeBuf[(i << 1) + 2] = (byte) (r | (r >> 5));
                writeBuf[(i << 1) + 3] = (byte) 0xFF;
            }
        } else if (getFormat() == 517) {
            byte b = 0x00;
            int pixelIndex = 0;
            for (int i = 0; i < declen; i++) {
                for (int j = 0; j < 8; j++) {
                    b = (byte) (((uc[i] & (0x01 << (7 - j))) >> (7 - j)) * 255);
                    for (int k = 0; k < 16; k++) {
                        pixelIndex = (i << 9) + (j << 6) + k * 2;
                        writeBuf[pixelIndex] = b;
                        writeBuf[pixelIndex + 1] = b;
                        writeBuf[pixelIndex + 2] = b;
                        writeBuf[pixelIndex + 3] = (byte) 0xFF;
                    }
                }
            }
        }
        DataBufferByte imgData = new DataBufferByte(writeBuf, sizeUncompressed);
        SampleModel model = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, getWidth(), getHeight(), 4, getWidth() * 4, ZAHLEN);
        WritableRaster imgRaster = Raster.createWritableRaster(model, imgData, new Point(0, 0));
        image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
      	image.setData(imgRaster);
      	//fxImage = SwingFXUtils.toFXImage(image, null);
      	ByteArrayOutputStream out = new ByteArrayOutputStream();
      	try {
			ImageIO.write(image, "png", out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      	fxImage = new Image(new ByteArrayInputStream(out.toByteArray()));
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public int getFormat() {
		return format;
	}

	public byte getMagLevel() {
		return magLevel;
	}
	
	public BufferedImage getImage() {
		if (image == null) {
			createImage();
		}
		return image;
	}
	
	public Image getFxImage() {
		if (fxImage == null) {
			createImage();
		}
		return fxImage;
	}
	
	public WzVector2D getOrigin() {
		try {
			return (WzVector2D) this.getObject("origin");
		} catch (NullPointerException e) {
			return new WzVector2D(this, 0, 0);
		}
	}
	
	public WzObject get(String name) {
		return prop.get(name);
	}

}
